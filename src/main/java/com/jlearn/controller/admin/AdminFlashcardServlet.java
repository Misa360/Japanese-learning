package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F39 /admin/flashcards - The trong mot bo he thong (?deck_id= bat buoc): danh sach, them, sua, xoa, doi thu tu (flashcards.sort_order).
 * Nhap hang loat tai /admin/flashcards/import?deck_id=: dan van ban hoac upload CSV, moi dong "mat truoc ; mat sau ; vi du" (vi du khong bat buoc).
 * Dong loi bo qua va ghi vao flashcard_import_batches (total_rows, success_rows, failed_rows, error_details JSON); gioi han so dong moi lan.
 * GET /admin/flashcards?deck_id= ; GET /admin/flashcards/new?deck_id= ; GET /admin/flashcards/{id} ; GET /admin/flashcards/import?deck_id=
 * POST /admin/flashcards ; POST /admin/flashcards/{id} ; POST /admin/flashcards/{id}/delete ; POST /admin/flashcards/import
 * Moi thao tac ghi phai ghi audit_logs (F38). Hien chi la khung.
 */
@WebServlet(name = "AdminFlashcardServlet", urlPatterns = {"/admin/flashcards", "/admin/flashcards/*"})
public class AdminFlashcardServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/import".equals(path)) {
            render(req, resp, "Nhập thẻ hàng loạt", "admin-flashcards", "admin/flashcard_import.jsp");
            return;
        }
        render(req, resp, isForm(req) ? "Biểu mẫu thẻ" : "Thẻ trong bộ", "admin-flashcards",
                isForm(req) ? "admin/flashcard_form.jsp" : "admin/flashcard_list.jsp");
    }
}
