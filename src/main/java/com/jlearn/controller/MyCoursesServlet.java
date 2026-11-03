package com.jlearn.controller;

import com.jlearn.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F07: GET /my-courses?status=not_started|in_progress|completed */
@WebServlet(name = "MyCoursesServlet", urlPatterns = "/my-courses")
public class MyCoursesServlet extends BaseServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = UserService.normalizeFilter(req.getParameter("status"));
        req.setAttribute("courses", userService.myCourses(requireUserId(req), status));
        req.setAttribute("filter", status);
        req.setAttribute("activeNav", "my-courses");
        forward(req, resp, "user/my_courses.jsp");
    }
}
