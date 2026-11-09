package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F38 /admin/audit
 * Nhat ky he thong (audit_logs): danh sach co phan trang, loc theo nguoi thuc hien / hanh dong / doi tuong / khoang ngay. Chi doc.
 * GET /admin/audit
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminAuditServlet", urlPatterns = {"/admin/audit", "/admin/audit/*"})
public class AdminAuditServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, "Nhật ký hệ thống", "admin-audit", "admin/audit_log.jsp");
    }
}
