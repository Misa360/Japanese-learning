package com.jlearn.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F23: route thi thu JLPT. Noi dung bai thi se duoc mo rong tu cac quiz da publish. */
@WebServlet("/practice/jlpt")
public class JlptExamServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, javax.servlet.ServletException {
        requireUserId(req);
        forward(req, resp, "practice/jlpt_exam.jsp");
    }
}
