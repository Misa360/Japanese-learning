package com.jlearn.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class WebUtil {
    private WebUtil() { }

    /** Duong dan da giai ma, da chuan hoa, khong co context path va khong co ;jsessionid (dung de so khop quy tac). */
    public static String path(HttpServletRequest req) {
        String sp = req.getServletPath() == null ? "" : req.getServletPath();
        String pi = req.getPathInfo() == null ? "" : req.getPathInfo();
        String p = sp + pi;
        return p.isEmpty() ? "/" : p;
    }

    public static Long userId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return null;
        Object v = s.getAttribute(SessionKeys.USER_ID);
        return (v instanceof Long) ? (Long) v : (v instanceof Number ? Long.valueOf(((Number) v).longValue()) : null);
    }

    public static boolean isAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return false;
        Object r = s.getAttribute(SessionKeys.ROLE);
        return r instanceof String && "admin".equalsIgnoreCase((String) r);
    }

    /** Request do JavaScript (fetch/XHR) goi: tra JSON thay vi chuyen trang. */
    public static boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        return "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (accept != null && accept.contains("application/json"));
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String appPath) throws IOException {
        resp.sendRedirect(req.getContextPath() + appPath);
    }

    public static void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write(JsonUtil.toJson(body));
    }

    public static Map<String, Object> error(String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", false);
        m.put("error", message);
        return m;
    }

    /** Tu choi request: JSON cho AJAX, trang loi (errors/xxx.jsp qua web.xml) cho request thuong. */
    public static void reject(HttpServletRequest req, HttpServletResponse resp, int status, String message) throws IOException {
        if (wantsJson(req)) writeJson(resp, status, error(message));
        else resp.sendError(status, message);
    }

    /** Chua dang nhap: AJAX nhan 401 JSON; trang thuong nho URL dang xem (chi GET) roi chuyen ve /login. */
    public static void rejectUnauthenticated(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (wantsJson(req)) {
            writeJson(resp, 401, error("Bạn cần đăng nhập"));
            return;
        }
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String next = path(req);
            if (req.getQueryString() != null) next += "?" + req.getQueryString();
            req.getSession(true).setAttribute(SessionKeys.NEXT_URL, next);   // luon la duong dan noi bo (bat dau bang /)
        }
        redirect(req, resp, "/login");
    }
}
