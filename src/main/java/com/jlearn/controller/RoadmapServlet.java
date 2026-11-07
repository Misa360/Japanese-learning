package com.jlearn.controller;

import com.jlearn.service.RoadmapService;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F10: GET /roadmap (khach cung xem duoc). */
@WebServlet(name = "RoadmapServlet", urlPatterns = "/roadmap")
public class RoadmapServlet extends BaseServlet {
    private final RoadmapService roadmapService = new RoadmapService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("activeNav", "roadmap");
        req.setAttribute("levels", roadmapService.buildRoadmap(WebUtil.userId(req)));
        forward(req, resp, "course/roadmap.jsp");
    }
}
