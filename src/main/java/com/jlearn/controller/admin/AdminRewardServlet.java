package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F37 /admin/rewards
 * Quan ly phan thuong (rewards): danh sach, them, sua, doi status, ton kho (stock_quantity), thoi han (starts_at/ends_at).
 * GET /admin/rewards ; GET /admin/rewards/new ; GET /admin/rewards/{id} ; POST /admin/rewards ; POST /admin/rewards/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminRewardServlet", urlPatterns = {"/admin/rewards", "/admin/rewards/*"})
public class AdminRewardServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, isForm(req) ? "Biểu mẫu phần thưởng" : "Quản lý phần thưởng", "admin-rewards", isForm(req) ? "admin/reward_form.jsp" : "admin/reward_list.jsp");
    }
}
