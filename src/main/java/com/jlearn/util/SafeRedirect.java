package com.jlearn.util;

/** Chong open redirect (F02): chi nhan duong dan noi bo bat dau bang "/" ; khong nhan "//", "/\\", scheme, ky tu dieu khien. */
public final class SafeRedirect {
    private SafeRedirect() { }

    /** @return duong dan da kiem tra (khong gom context path) hoac fallback. */
    public static String internalPath(String candidate, String fallback) {
        if (candidate == null) return fallback;
        String s = candidate.trim();
        if (s.isEmpty() || s.length() > 500) return fallback;
        if (s.charAt(0) != '/') return fallback;
        if (s.length() > 1 && (s.charAt(1) == '/' || s.charAt(1) == '\\')) return fallback;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < 0x20 || ch == 0x7f || ch == '\\') return fallback;
        }
        String lower = s.toLowerCase();
        if (lower.contains("://") || lower.contains("%2f%2f") || lower.contains("%5c")) return fallback;
        // khong quay lai chinh cac trang xac thuc (vong lap)
        String path = lower.split("[?#]", 2)[0];
        if (path.equals("/login") || path.equals("/register") || path.equals("/logout")) return fallback;
        return s;
    }
}
