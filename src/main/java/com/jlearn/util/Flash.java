package com.jlearn.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Flash message (quy tac chung so 8). Luu trong session, partials/flash.jsp doc roi xoa.
 * Loai: "success", "error", "info".
 */
public final class Flash {
    private Flash() { }

    @SuppressWarnings("unchecked")
    public static void set(HttpServletRequest req, String type, String message) {
        HttpSession s = req.getSession(true);
        Map<String, String> m = (Map<String, String>) s.getAttribute(SessionKeys.FLASH);
        if (m == null) m = new LinkedHashMap<>();
        m.put(type, message);
        s.setAttribute(SessionKeys.FLASH, m);
    }

    public static void success(HttpServletRequest req, String message) { set(req, "success", message); }
    public static void error(HttpServletRequest req, String message) { set(req, "error", message); }
    public static void info(HttpServletRequest req, String message) { set(req, "info", message); }
}
