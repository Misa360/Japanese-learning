package com.jlearn.controller;

import com.jlearn.service.AuthService;
import com.jlearn.util.ClientIp;
import com.jlearn.util.CsrfTokens;
import com.jlearn.util.Flash;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F04: GET/POST /account/password (can dang nhap - AuthenticationFilter). */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = "/account/password")
public class ChangePasswordServlet extends BaseServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("activeNav", "password");
        forward(req, resp, "auth/change_password.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long uid = requireUserId(req);
        AuthService.ChangeResult r = authService.changePassword(uid, req.getParameter("old_password"),
                req.getParameter("new_password"), req.getParameter("new_password_confirm"), ClientIp.bytes(ClientIp.of(req)));
        if (r.ok()) {
            req.changeSessionId();                               // doi session ID + token CSRF sau khi doi mat khau
            CsrfTokens.rotate(req.getSession());
            Flash.success(req, "Đổi mật khẩu thành công");
            WebUtil.redirect(req, resp, "/account/password");
            return;
        }
        if (r.tooManyAttempts) {
            long minutes = Math.max(1, (r.retryAfterSeconds + 59) / 60);
            req.setAttribute("error", "Bạn đã nhập sai mật khẩu hiện tại quá nhiều lần. Thử lại sau " + minutes + " phút.");
        }
        req.setAttribute("errors", r.errors);
        req.setAttribute("activeNav", "password");
        forward(req, resp, "auth/change_password.jsp");
    }
}
