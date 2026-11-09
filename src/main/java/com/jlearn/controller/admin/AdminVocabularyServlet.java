package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F35 /admin/vocabulary
 * Kho tu vung dung chung (vocabulary): danh sach (tim, loc cap do N5-N1), them, sua. Chi xoa khi chua gan vao bai hoc nao (lesson_vocabulary).
 * GET /admin/vocabulary ; GET /admin/vocabulary/new ; GET /admin/vocabulary/{id} ; POST /admin/vocabulary ; POST /admin/vocabulary/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminVocabularyServlet", urlPatterns = {"/admin/vocabulary", "/admin/vocabulary/*"})
public class AdminVocabularyServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, isForm(req) ? "Biểu mẫu từ vựng" : "Kho từ vựng", "admin-vocabulary", isForm(req) ? "admin/vocabulary_form.jsp" : "admin/vocabulary_list.jsp");
    }
}
