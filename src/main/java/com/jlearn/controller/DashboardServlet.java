package com.jlearn.controller;

import com.jlearn.service.UserService;
import com.jlearn.service.CourseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F06: GET /dashboard. Khong nhan id tu URL, chi dung user trong session. */
@WebServlet(name = "DashboardServlet", urlPatterns = "/dashboard")
public class DashboardServlet extends BaseServlet {
    private final UserService userService = new UserService();
    private final CourseService courseService = new CourseService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dashboard", userService.dashboard(requireUserId(req)));
        req.setAttribute("suggestedCourses", courseService.publishedCourses());
        req.setAttribute("activeNav", "dashboard");
        forward(req, resp, "user/dashboard.jsp");
    }
}
