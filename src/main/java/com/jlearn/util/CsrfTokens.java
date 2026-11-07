package com.jlearn.util;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfTokens {
    public static final String PARAM = "csrf_token";
    public static final String HEADER = "X-CSRF-Token";
    private static final SecureRandom RNG = new SecureRandom();

    private CsrfTokens() { }

    private static String generate() {
        byte[] b = new byte[32];
        RNG.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    public static String getOrCreate(HttpSession session) {
        Object t = session.getAttribute(SessionKeys.CSRF);
        if (t instanceof String) return (String) t;
        synchronized (CsrfTokens.class) {
            t = session.getAttribute(SessionKeys.CSRF);
            if (t instanceof String) return (String) t;
            String fresh = generate();
            session.setAttribute(SessionKeys.CSRF, fresh);
            return fresh;
        }
    }

    /** Goi sau khi dang nhap / doi mat khau de doi token. */
    public static String rotate(HttpSession session) {
        String fresh = generate();
        session.setAttribute(SessionKeys.CSRF, fresh);
        return fresh;
    }

    /** So sanh thoi gian khong doi (chong timing attack). */
    public static boolean matches(HttpSession session, String submitted) {
        if (session == null || submitted == null || submitted.isEmpty()) return false;
        Object expected = session.getAttribute(SessionKeys.CSRF);
        if (!(expected instanceof String)) return false;
        return MessageDigest.isEqual(((String) expected).getBytes(StandardCharsets.UTF_8),
                submitted.getBytes(StandardCharsets.UTF_8));
    }
}
