package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F39 /admin/flashcard-decks - Bo flashcard HE THONG (flashcard_decks voi user_id = NULL, visibility = 'public', created_by = admin).
 * Danh sach (loc cap do N5-N1, status), them, sua, luu tru (status = 'archived', khong xoa cung vi hoc vien co lich su on tap).
 * Bo rieng cua hoc vien (user_id khac NULL) khong hien o day. Moi bo co lien ket "The" -> /admin/flashcards?deck_id=.
 * Luu y: FlashcardDeckDao.findAvailable (phia hoc vien) phai them dieu kien d.status = 'active'.
 * GET /admin/flashcard-decks ; GET /admin/flashcard-decks/new ; GET /admin/flashcard-decks/{id} ; POST /admin/flashcard-decks ; POST /admin/flashcard-decks/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38). Hien chi la khung.
 */
@WebServlet(name = "AdminFlashcardDeckServlet", urlPatterns = {"/admin/flashcard-decks", "/admin/flashcard-decks/*"})
public class AdminFlashcardDeckServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, isForm(req) ? "Biểu mẫu bộ thẻ" : "Bộ flashcard hệ thống", "admin-flashcards",
                isForm(req) ? "admin/flashcard_deck_form.jsp" : "admin/flashcard_deck_list.jsp");
    }
}
