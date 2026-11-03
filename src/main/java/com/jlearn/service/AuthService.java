package com.jlearn.service;

import com.jlearn.config.AppConfig;
import com.jlearn.dao.LoginAttemptDao;
import com.jlearn.dao.RoleDao;
import com.jlearn.dao.UserDao;
import com.jlearn.exception.NotFoundException;
import com.jlearn.model.LoginResult;
import com.jlearn.model.User;
import com.jlearn.util.DbUtil;
import com.jlearn.util.PasswordHasher;
import com.jlearn.util.Validator;

import java.util.Locale;
import java.util.Map;

/** F01 dang ky, F02 dang nhap (khoa tam), F04 doi mat khau. Phan session/cookie nam o servlet. */
public class AuthService {
    private final UserDao userDao = new UserDao();
    private final RoleDao roleDao = new RoleDao();
    private final LoginAttemptDao attemptDao = new LoginAttemptDao();

    private static int maxFailures() { return AppConfig.getInt("security.login.maxFailures", 5); }
    private static int windowMinutes() { return AppConfig.getInt("security.login.windowMinutes", 15); }
    private static int ipMaxFailures() { return AppConfig.getInt("security.login.ipMaxFailures", 30); }

    // ---------------------------------------------------------------- F01

    /**
     * @return map loi theo o (rong = thanh cong). Role luon la 'user' (STUDENT), KHONG nhan role tu client.
     */
    public Map<String, String> register(String username, String email, String password, String passwordConfirm) {
        String u = username == null ? "" : username.trim();
        String e = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String p = password == null ? "" : password;

        Validator v = new Validator();
        v.username("username", u).email("email", e).newPassword("password", p, u, e)
         .matches("password_confirm", p, passwordConfirm, "Mật khẩu xác nhận không khớp");
        if (!v.ok()) return v.errors();

        return DbUtil.tx(c -> {
            Validator d = new Validator();
            if (userDao.usernameExists(c, u)) d.error("username", "Tên đăng nhập đã được sử dụng");
            if (userDao.emailExists(c, e)) d.error("email", "Email đã được sử dụng");
            if (!d.ok()) return d.errors();

            Long roleId = roleDao.idByName(c, "user");
            if (roleId == null) throw new IllegalStateException("Thiếu role 'user' trong bảng roles");
            try {
                userDao.insert(c, roleId, u, u, e, PasswordHasher.hash(p));
            } catch (UserDao.DuplicateException dup) {          // hai nguoi dang ky cung luc
                d.error(dup.getField(), "email".equals(dup.getField()) ? "Email đã được sử dụng" : "Tên đăng nhập đã được sử dụng");
                return d.errors();
            }
            return new java.util.LinkedHashMap<String, String>();
        });
    }

    // ---------------------------------------------------------------- F02

    /**
     * Khoa tam: maxFailures lan sai trong windowMinutes phut thi khoa windowMinutes phut (tinh tu lan sai cuoi).
     * Khoa theo tai khoan (email chuan hoa) va theo IP. Thong bao sai KHONG cho biet sai email hay sai mat khau;
     * tai khoan bi khoa chi bao khi mat khau dung (tranh do tai khoan).
     */
    public LoginResult login(String identifier, String password, byte[] ip) {
        final String id = identifier == null ? "" : identifier.trim().toLowerCase(Locale.ROOT);
        if (id.isEmpty() || password == null || password.isEmpty() || password.length() > 200) {
            return LoginResult.of(LoginResult.BAD_CREDENTIALS, null, 0);
        }
        return DbUtil.tx(c -> {
            User user = userDao.findByLogin(c, id);
            String key = user != null ? user.getEmail().toLowerCase(Locale.ROOT) : id;

            LoginAttemptDao.Lock byId = attemptDao.byIdentifier(c, key, windowMinutes());
            if (byId.failures >= maxFailures() && byId.secondsLeft > 0) {
                return LoginResult.of(LoginResult.TEMP_LOCKED, null, byId.secondsLeft);
            }
            if (ip != null) {
                LoginAttemptDao.Lock byIp = attemptDao.byIp(c, ip, windowMinutes());
                if (byIp.failures >= ipMaxFailures() && byIp.secondsLeft > 0) {
                    return LoginResult.of(LoginResult.TEMP_LOCKED, null, byIp.secondsLeft);
                }
            }

            boolean ok = PasswordHasher.verify(password, user == null ? null : user.getPasswordHash());
            if (!ok) {
                attemptDao.recordFailure(c, user == null ? null : user.getId(), key, ip);
                return LoginResult.of(LoginResult.BAD_CREDENTIALS, null, 0);
            }
            if (!user.isActive()) {
                return LoginResult.of(LoginResult.ACCOUNT_LOCKED, null, 0);
            }
            attemptDao.clear(c, key);
            userDao.touchLastLogin(c, user.getId());
            return LoginResult.of(LoginResult.OK, user, 0);
        });
    }

    // ---------------------------------------------------------------- F04

    public static final class ChangeResult {
        public final Map<String, String> errors;
        public final boolean tooManyAttempts;
        public final long retryAfterSeconds;
        ChangeResult(Map<String, String> errors, boolean tooMany, long retry) {
            this.errors = errors; this.tooManyAttempts = tooMany; this.retryAfterSeconds = retry;
        }
        public boolean ok() { return errors.isEmpty() && !tooManyAttempts; }
    }

    /** Bat buoc mat khau cu; mat khau cu sai qua nhieu lan thi khoa tam (dung chung co che F02). */
    public ChangeResult changePassword(long userId, String oldPw, String newPw, String confirm, byte[] ip) {
        final String key = "pwchange:" + userId;
        final String o = oldPw == null ? "" : oldPw;
        final String n = newPw == null ? "" : newPw;
        return DbUtil.tx(c -> {
            User user = userDao.findById(c, userId);
            if (user == null) throw new NotFoundException("Không tìm thấy tài khoản");

            LoginAttemptDao.Lock lock = attemptDao.byIdentifier(c, key, windowMinutes());
            if (lock.failures >= maxFailures() && lock.secondsLeft > 0) {
                return new ChangeResult(new java.util.LinkedHashMap<String, String>(), true, lock.secondsLeft);
            }
            Validator v = new Validator();
            if (!PasswordHasher.verify(o, user.getPasswordHash())) {
                attemptDao.recordFailure(c, userId, key, ip);
                v.error("old_password", "Mật khẩu hiện tại không đúng");
                return new ChangeResult(v.errors(), false, 0);
            }
            v.newPassword("new_password", n, user.getUsername(), user.getEmail())
             .matches("new_password_confirm", n, confirm, "Mật khẩu xác nhận không khớp");
            if (v.ok() && n.equals(o)) v.error("new_password", "Mật khẩu mới phải khác mật khẩu cũ");
            if (!v.ok()) return new ChangeResult(v.errors(), false, 0);

            userDao.updatePassword(c, userId, PasswordHasher.hash(n));
            attemptDao.clear(c, key);
            return new ChangeResult(v.errors(), false, 0);
        });
    }
}
