package com.jlearn.controller.admin;

import com.jlearn.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * F33 /admin
 * Trang tong quan quan tri: so hoc vien, khoa hoc, bai hoc, de, luot lam bai 7 ngay, yeu cau doi thuong dang cho.
 * GET /admin  (chi doc, khong co doPost)
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin", "/admin/"})
public class AdminDashboardServlet extends AdminBaseServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("stats", adminService.dashboardCounts());
        render(req, resp, "Tổng quan quản trị", "admin-dashboard", "admin/dashboard.jsp");
    }
}
