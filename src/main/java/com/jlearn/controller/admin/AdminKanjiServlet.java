package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F35 /admin/kanji
 * Kho Kanji dung chung (kanji, character_kanji la UNIQUE): danh sach, them, sua. Chi xoa khi chua gan vao bai hoc nao (lesson_kanji).
 * GET /admin/kanji ; GET /admin/kanji/new ; GET /admin/kanji/{id} ; POST /admin/kanji ; POST /admin/kanji/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminKanjiServlet", urlPatterns = {"/admin/kanji", "/admin/kanji/*"})
public class AdminKanjiServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, isForm(req) ? "Biểu mẫu Kanji" : "Kho Kanji", "admin-kanji", isForm(req) ? "admin/kanji_form.jsp" : "admin/kanji_list.jsp");
    }
}
