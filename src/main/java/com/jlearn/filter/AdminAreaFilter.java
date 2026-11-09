package com.jlearn.filter;

import com.jlearn.util.WebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Tach khu quan tri khoi khu hoc vien: tai khoan admin CHI duoc vao cac duong dan trong danh sach cho phep duoi day
 * (khu /admin, dang xuat, doi mat khau, tai nguyen tinh). Moi duong dan khac (/dashboard, /my-courses, /learn/..., /practice/...)
 * deu bi chuyen ve /admin (GET) hoac tra 403 (POST/AJAX). Hoc vien khong bi anh huong.
 * Map: /*  (khai bao sau AuthenticationFilter, truoc CsrfFilter trong web.xml).
 * Muon cho admin xem them trang nao (vi du xem thu khoa hoc) thi them tien to vao ALLOWED_PREFIX.
 */
public class AdminAreaFilter implements Filter {

    private static final List<String> ALLOWED_PREFIX = Arrays.asList(
            "/admin", "/assets/", "/media/avatar/", "/account/password", "/logout", "/login", "/register", "/favicon.ico");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (WebUtil.userId(req) == null || !WebUtil.isAdmin(req) || isAllowed(WebUtil.path(req))) {
            chain.doFilter(request, response);
            return;
        }
        if ("GET".equalsIgnoreCase(req.getMethod()) && !WebUtil.wantsJson(req)) {
            WebUtil.redirect(req, resp, "/admin");
        } else {
            WebUtil.reject(req, resp, HttpServletResponse.SC_FORBIDDEN, "Tài khoản quản trị không dùng chức năng học viên");
        }
    }

    static boolean isAllowed(String path) {
        if (path == null || path.isEmpty() || "/".equals(path)) return true;   // "/" da duoc HomeServlet chuyen sang /admin
        for (String p : ALLOWED_PREFIX) {
            if (path.equals(p) || path.startsWith(p.endsWith("/") ? p : p + "/")) return true;
        }
        return false;
    }
}
