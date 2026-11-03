package com.jlearn.controller;

import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** GET / : trang chu. Da dang nhap thi vao thang dashboard. */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/index"})
public class HomeServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (WebUtil.userId(req) != null) { WebUtil.redirect(req, resp, WebUtil.isAdmin(req) ? "/admin" : "/dashboard"); return; }
        forward(req, resp, "home.jsp");
    }
}
