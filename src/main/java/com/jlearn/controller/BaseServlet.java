package com.jlearn.controller;

import com.jlearn.exception.AppException;
import com.jlearn.exception.DataAccessException;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Lop co so cho servlet cua F10-F18: bat AppException -> trang loi / JSON, loi DB -> 500 (khong lo chi tiet SQL). */
public abstract class BaseServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (AppException e) {
            if (e.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) WebUtil.rejectUnauthenticated(req, resp);
            else WebUtil.reject(req, resp, e.getStatus(), e.getMessage());
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Loi co so du lieu", e);
            WebUtil.reject(req, resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Hệ thống đang gặp sự cố, vui lòng thử lại sau");
        }
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }

    protected long requireUserId(HttpServletRequest req) {
        Long uid = WebUtil.userId(req);       // user_id luon lay tu session, khong bao gio tu form/URL
        if (uid == null) throw new com.jlearn.exception.UnauthorizedException();
        return uid;
    }
}
