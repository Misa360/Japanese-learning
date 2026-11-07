package com.jlearn.util;

/** Ten thuoc tinh session dung chung. Nguoi A (dang nhap) phai luu dung 2 khoa USER_ID va ROLE nay. */
public final class SessionKeys {
    public static final String USER_ID = "userId";      // Long
    public static final String ROLE = "role";           // String: "admin" hoac "user" (khong phan biet hoa thuong)
    public static final String CSRF = "csrfToken";      // String
    public static final String FLASH = "flash";         // Map<String,String> loai -> noi dung
    public static final String NEXT_URL = "nextUrl";    // duong dan noi bo de quay lai sau khi dang nhap

    private SessionKeys() { }
}
