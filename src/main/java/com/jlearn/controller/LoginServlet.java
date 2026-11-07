package com.jlearn.controller;

import com.jlearn.model.LoginResult;
import com.jlearn.model.User;
import com.jlearn.service.AuthService;
import com.jlearn.util.ClientIp;
import com.jlearn.util.CsrfTokens;
import com.jlearn.util.SafeRedirect;
import com.jlearn.util.SessionKeys;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** F02: GET/POST /login */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends BaseServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("next", SafeRedirect.internalPath(req.getParameter("next"), ""));
        forward(req, resp, "auth/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String identifier = req.getParameter("identifier");
        String password = req.getParameter("password");
        String nextParam = SafeRedirect.internalPath(req.getParameter("next"), "");

        LoginResult r = authService.login(identifier, password, ClientIp.bytes(ClientIp.of(req)));
        if (!LoginResult.OK.equals(r.getStatus())) {
            String msg;
            if (LoginResult.TEMP_LOCKED.equals(r.getStatus())) {
                long minutes = Math.max(1, (r.getRetryAfterSeconds() + 59) / 60);
                msg = "Bạn đã thử quá nhiều lần. Vui lòng thử lại sau " + minutes + " phút.";
            } else if (LoginResult.ACCOUNT_LOCKED.equals(r.getStatus())) {
                msg = "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.";
            } else {
                msg = "Sai tài khoản hoặc mật khẩu";           // khong cho biet sai o nao
            }
            req.setAttribute("error", msg);
            req.setAttribute("identifier", identifier == null ? "" : identifier.trim());
            req.setAttribute("next", nextParam);
            forward(req, resp, "auth/login.jsp");
            return;
        }

        User u = r.getUser();
        HttpSession session = req.getSession(true);
        String sessionNext = session.getAttribute(SessionKeys.NEXT_URL) instanceof String
                ? (String) session.getAttribute(SessionKeys.NEXT_URL) : null;

        req.changeSessionId();                                    // chong session fixation: doi ID, giu thuoc tinh
        session.setAttribute(SessionKeys.USER_ID, Long.valueOf(u.getId()));
        session.setAttribute(SessionKeys.ROLE, u.getRoleName());
        session.setAttribute("fullName", u.getFullName());
        session.setAttribute("username", u.getUsername());
        session.setAttribute("avatarPath", u.getAvatarPath());
        session.setAttribute("gender", u.getGender());
        session.removeAttribute(SessionKeys.NEXT_URL);
        CsrfTokens.rotate(session);

        String fallback = u.isAdmin() ? "/admin" : "/dashboard";
        String target = SafeRedirect.internalPath(nextParam, null);
        if (target == null) target = SafeRedirect.internalPath(sessionNext, fallback);
        WebUtil.redirect(req, resp, target);
    }
}
