package com.jlearn.controller;

import com.jlearn.util.Flash;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** F03: chi POST (kem CSRF do CsrfFilter) de nguoi khac khong ep dang xuat bang duong link. GET -> 405. */
@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();                      // huy toan bo du lieu phien cu
        Flash.success(req, "Bạn đã đăng xuất");                 // tao phien moi (ID moi, cookie moi ghi de cookie cu)
        WebUtil.redirect(req, resp, "/login");
    }
}
