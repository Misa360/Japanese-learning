package com.jlearn.controller;

import com.jlearn.service.StudyStatsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F09: GET /history?page=n (20 dong/trang). */
@WebServlet(name = "HistoryServlet", urlPatterns = "/history")
public class HistoryServlet extends BaseServlet {
    private final StudyStatsService statsService = new StudyStatsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        String p = req.getParameter("page");
        if (p != null && p.matches("^\\d{1,6}$")) page = Integer.parseInt(p);
        String tab = req.getParameter("tab");
        if (!"exercise".equals(tab) && !"practice".equals(tab) && !"jlpt".equals(tab)) tab = "lesson";
        com.jlearn.model.HistoryPage hp = statsService.history(requireUserId(req), page);
        req.setAttribute("history", hp);
        req.setAttribute("historyTab", tab);
        req.setAttribute("pager", hp.getPager());
        req.setAttribute("pagerBaseUrl", req.getContextPath() + "/history");
        req.setAttribute("activeNav", "history");
        forward(req, resp, "user/history.jsp");
    }
}
