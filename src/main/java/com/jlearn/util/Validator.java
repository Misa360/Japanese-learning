package com.jlearn.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Gom loi theo tung o (field -> thong bao) de view hien duoi tung o nhap. */
public final class Validator {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+\\-]{1,64}@[A-Za-z0-9.\\-]{1,190}\\.[A-Za-z]{2,24}$");

    private final Map<String, String> errors = new LinkedHashMap<>();

    public Validator error(String field, String message) {
        if (!errors.containsKey(field)) errors.put(field, message);
        return this;
    }

    public boolean ok() { return errors.isEmpty(); }
    public Map<String, String> errors() { return errors; }

    public Validator username(String field, String v) {
        if (v == null || !USERNAME.matcher(v).matches()) error(field, "Tên đăng nhập gồm 3–30 ký tự: chữ, số hoặc gạch dưới");
        return this;
    }

    public Validator email(String field, String v) {
        if (v == null || v.length() > 255 || !EMAIL.matcher(v).matches()) error(field, "Email không hợp lệ");
        return this;
    }

    /** Tu 8 ky tu, co chu va so, khong qua 72 byte (gioi han BCrypt). */
    public Validator newPassword(String field, String v, String... forbiddenEquals) {
        if (v == null || v.length() < 8) { error(field, "Mật khẩu cần ít nhất 8 ký tự"); return this; }
        if (PasswordHasher.tooLong(v)) { error(field, "Mật khẩu quá dài (tối đa 72 byte)"); return this; }
        boolean letter = false, digit = false;
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (Character.isLetter(c)) letter = true;
            if (Character.isDigit(c)) digit = true;
        }
        if (!letter || !digit) { error(field, "Mật khẩu cần có cả chữ và số"); return this; }
        for (String f : forbiddenEquals) {
            if (f != null && !f.isEmpty() && v.equalsIgnoreCase(f)) { error(field, "Mật khẩu quá dễ đoán"); break; }
        }
        return this;
    }

    public Validator matches(String field, String a, String b, String message) {
        if (a == null || !a.equals(b)) error(field, message);
        return this;
    }

    /** Ten hien thi: 1-100 ky tu, khong co ky tu dieu khien. */
    public Validator displayName(String field, String v) {
        if (v == null || v.trim().isEmpty() || v.trim().length() > 100) { error(field, "Tên hiển thị từ 1 đến 100 ký tự"); return this; }
        for (int i = 0; i < v.length(); i++) {
            if (Character.isISOControl(v.charAt(i))) { error(field, "Tên hiển thị chứa ký tự không hợp lệ"); break; }
        }
        return this;
    }
}
