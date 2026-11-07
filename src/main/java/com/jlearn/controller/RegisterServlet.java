package com.jlearn.controller;

import com.jlearn.service.AuthService;
import com.jlearn.util.Flash;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** F01: GET/POST /register (RateLimitFilter gioi han theo IP; GuestFilter chan nguoi da dang nhap). */
@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends BaseServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, "auth/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Chi doc cac truong dang ky duoc phep: khong bao gio nhan role/status tu client.
        String username = req.getParameter("username");
        String fullName = req.getParameter("full_name");
        String email = req.getParameter("email");
        String gender = req.getParameter("gender");
        Map<String, String> errors = authService.register(username, fullName, email, req.getParameter("password"), req.getParameter("password_confirm"), gender);
        if (errors.isEmpty()) {
            Flash.success(req, "Đăng ký thành công! Hãy đăng nhập để bắt đầu học.");
            WebUtil.redirect(req, resp, "/login");
            return;
        }
        Map<String, String> form = new LinkedHashMap<>();       // giu lai du lieu da nhap, tru mat khau
        form.put("username", username == null ? "" : username.trim());
        form.put("full_name", fullName == null ? "" : fullName.trim());
        form.put("email", email == null ? "" : email.trim());
        form.put("gender", gender == null ? "" : gender.trim());
        req.setAttribute("errors", errors);
        req.setAttribute("form", form);
        forward(req, resp, "auth/register.jsp");
    }
}
