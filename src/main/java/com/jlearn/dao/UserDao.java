package com.jlearn.dao;

import com.jlearn.model.User;
import com.jlearn.model.AdminUserRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String SELECT =
            "SELECT u.id, u.role_id, r.name AS role_name, u.username, u.full_name, u.email, u.password_hash, "
          + "u.avatar_path, u.gender, u.status, u.target_level_id, u.coin_balance FROM users u JOIN roles r ON r.id = u.role_id ";

    public List<AdminUserRow> findAdminUsers(Connection c, String query, String role, String status, int limit, int offset) throws SQLException {
        StringBuilder sql=new StringBuilder("SELECT u.id,u.username,u.full_name,u.email,r.name role_name,u.status,u.coin_balance,u.created_at,u.last_login_at FROM users u JOIN roles r ON r.id=u.role_id WHERE 1=1");
        appendAdminFilters(sql,query,role,status);
        sql.append(" ORDER BY u.created_at DESC,u.id DESC LIMIT ? OFFSET ?");
        List<AdminUserRow> rows=new ArrayList<>();
        try(PreparedStatement ps=c.prepareStatement(sql.toString())){
            int i=bindAdminFilters(ps,query,role,status); ps.setInt(i++,limit); ps.setInt(i,offset);
            try(ResultSet rs=ps.executeQuery()){while(rs.next()){
                AdminUserRow row=new AdminUserRow();row.setId(rs.getLong("id"));row.setUsername(rs.getString("username"));row.setFullName(rs.getString("full_name"));row.setEmail(rs.getString("email"));row.setRoleName(rs.getString("role_name"));row.setStatus(rs.getString("status"));row.setCoinBalance(rs.getLong("coin_balance"));row.setCreatedAt(rs.getTimestamp("created_at"));row.setLastLoginAt(rs.getTimestamp("last_login_at"));rows.add(row);
            }}
        }
        return rows;
    }

    public long countAdminUsers(Connection c,String query,String role,String status) throws SQLException {
        StringBuilder sql=new StringBuilder("SELECT COUNT(*) FROM users u JOIN roles r ON r.id=u.role_id WHERE 1=1");appendAdminFilters(sql,query,role,status);
        try(PreparedStatement ps=c.prepareStatement(sql.toString())){bindAdminFilters(ps,query,role,status);try(ResultSet rs=ps.executeQuery()){rs.next();return rs.getLong(1);}}
    }

    private void appendAdminFilters(StringBuilder sql,String query,String role,String status){
        if(query!=null&&!query.isEmpty())sql.append(" AND (u.full_name LIKE ? OR u.email LIKE ? OR u.username LIKE ?)");
        if(role!=null&&!role.isEmpty())sql.append(" AND r.name=?");
        if(status!=null&&!status.isEmpty())sql.append(" AND u.status=?");
    }
    private int bindAdminFilters(PreparedStatement ps,String query,String role,String status) throws SQLException {
        int i=1;if(query!=null&&!query.isEmpty()){String like="%"+query+"%";ps.setString(i++,like);ps.setString(i++,like);ps.setString(i++,like);}if(role!=null&&!role.isEmpty())ps.setString(i++,role);if(status!=null&&!status.isEmpty())ps.setString(i++,status);return i;
    }

    /** Serializes admin role/status changes so concurrent requests cannot disable the final active admin. */
    public void lockAdminRole(Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT id FROM roles WHERE name='admin' FOR UPDATE");ResultSet rs=ps.executeQuery()){if(!rs.next())throw new SQLException("Missing admin role");}
    }
    public boolean hasOtherActiveAdmin(Connection c,long userId) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT 1 FROM users u JOIN roles r ON r.id=u.role_id WHERE r.name='admin' AND u.status='active' AND u.id<>? LIMIT 1")){ps.setLong(1,userId);try(ResultSet rs=ps.executeQuery()){return rs.next();}}
    }
    public void updateAdminStatus(Connection c,long userId,String status) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE users SET status=? WHERE id=?")){ps.setString(1,status);ps.setLong(2,userId);if(ps.executeUpdate()==0)throw new SQLException("User not found");}
    }
    public void updateAdminRole(Connection c,long userId,long roleId) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE users SET role_id=? WHERE id=?")){ps.setLong(1,roleId);ps.setLong(2,userId);if(ps.executeUpdate()==0)throw new SQLException("User not found");}
    }

    /**
     * QUY TAC KHOA: moi transaction co dong vao xu / enrollment / tien do phai goi ham nay DAU TIEN.
     * Neu khong, INSERT vao bang con (FK) tu lay khoa S tren dong users, roi CoinService xin khoa X -> deadlock
     * khi hai request cua cung mot nguoi chay song song. Khoa dong user truoc cung tuan tu hoa cac request cua nguoi do.
     * @return false neu khong co user.
     */
    public boolean lockForUpdate(Connection c, long userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT id FROM users WHERE id = ? FOR UPDATE")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User findById(Connection c, long id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE u.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Dang nhap bang email HOAC username (so khop khong phan biet hoa thuong theo collation cua cot). */
    public User findByLogin(Connection c, String identifier) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE u.email = ? OR u.username = ? LIMIT 1")) {
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean emailExists(Connection c, String email) throws SQLException {
        return exists(c, "SELECT 1 FROM users WHERE email = ?", email);
    }

    public boolean usernameExists(Connection c, String username) throws SQLException {
        return exists(c, "SELECT 1 FROM users WHERE username = ?", username);
    }

    private boolean exists(Connection c, String sql, String v) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static final class DuplicateException extends SQLException {
        private final String field;
        DuplicateException(String field, SQLException cause) { super(cause.getMessage(), cause); this.field = field; }
        public String getField() { return field; }
    }

    /** Them user; vi pham UNIQUE nem DuplicateException(field = "email" | "username") de Service bao loi dung o. */
    public long insert(Connection c, long roleId, String username, String fullName, String email, String passwordHash, String gender) throws SQLException {
        String sql = "INSERT INTO users (role_id, username, full_name, email, password_hash, gender, status) VALUES (?, ?, ?, ?, ?, ?, 'active')";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, roleId);
            ps.setString(2, username);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, passwordHash);
            ps.setString(6, gender);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == 1062) {
                String m = String.valueOf(e.getMessage());
                throw new DuplicateException(m.contains("username") ? "username" : "email", e);
            }
            throw e;
        }
    }

    public void updatePassword(Connection c, long userId, String hash) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")) {
            ps.setString(1, hash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void touchLastLogin(Connection c, long userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE users SET last_login_at = NOW() WHERE id = ?")) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    /** F08: chi cap nhat cac cot cho phep (KHONG role, status, xu). avatarFile = null thi giu anh cu. */
    public void updateProfile(Connection c, long userId, String fullName, Long targetLevelId, String avatarFile) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, target_level_id = ?" + (avatarFile != null ? ", avatar_path = ?" : "") + " WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, fullName);
            if (targetLevelId == null) ps.setNull(i++, Types.BIGINT); else ps.setLong(i++, targetLevelId);
            if (avatarFile != null) ps.setString(i++, avatarFile);
            ps.setLong(i, userId);
            ps.executeUpdate();
        }
    }

    public void updateGender(Connection c, long userId, String gender) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE users SET gender = ? WHERE id = ?")) {
            ps.setString(1, gender);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setRoleId(rs.getLong("role_id"));
        u.setRoleName(rs.getString("role_name"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setAvatarPath(rs.getString("avatar_path"));
        u.setGender(rs.getString("gender"));
        u.setStatus(rs.getString("status"));
        long t = rs.getLong("target_level_id");
        u.setTargetLevelId(rs.wasNull() ? null : Long.valueOf(t));
        u.setCoinBalance(rs.getLong("coin_balance"));
        return u;
    }
}
