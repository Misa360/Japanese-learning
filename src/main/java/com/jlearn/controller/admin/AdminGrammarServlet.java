package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F35 /admin/grammar
 * Kho ngu phap dung chung (grammar_points): danh sach, them, sua. Chi xoa khi chua gan vao bai hoc nao (lesson_grammar).
 * GET /admin/grammar ; GET /admin/grammar/new ; GET /admin/grammar/{id} ; POST /admin/grammar ; POST /admin/grammar/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminGrammarServlet", urlPatterns = {"/admin/grammar", "/admin/grammar/*"})
public class AdminGrammarServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, isForm(req) ? "Biểu mẫu ngữ pháp" : "Kho ngữ pháp", "admin-grammar", isForm(req) ? "admin/grammar_form.jsp" : "admin/grammar_list.jsp");
    }
}
