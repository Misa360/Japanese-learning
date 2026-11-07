package com.jlearn.controller;

import com.jlearn.exception.ValidationException;
import com.jlearn.model.FlashcardDeck;
import com.jlearn.service.FlashcardService;
import com.jlearn.util.Flash;
import com.jlearn.util.WebUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** F22: danh sach bo the, tao bo rieng, them the va on tap co lich su SRS. */
@WebServlet(urlPatterns = {"/practice/flashcards", "/practice/flashcards/*"})
public class FlashcardServlet extends BaseServlet {
    private final FlashcardService flashcards = new FlashcardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, javax.servlet.ServletException {
        long userId = requireUserId(req);
        String path = req.getPathInfo();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            String search = req.getParameter("q");
            req.setAttribute("search", search == null ? "" : search.trim());
            req.setAttribute("decks", flashcards.decks(userId, search));
            forward(req, resp, "practice/flashcard_decks.jsp");
            return;
        }
        String[] segments = path.split("/");
        if (segments.length != 2 || segments[1].isEmpty()) throw new ValidationException("Đường dẫn flashcard không hợp lệ");
        long deckId = parseId(segments[1]);
        FlashcardDeck deck = flashcards.studyDeck(userId, deckId);
        req.setAttribute("deck", deck);
        forward(req, resp, "practice/flashcard_study.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, javax.servlet.ServletException {
        long userId = requireUserId(req);
        String path = req.getPathInfo();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            long deckId = flashcards.createDeck(userId, req.getParameter("title"), req.getParameter("description"));
            Flash.success(req, "Đã tạo bộ flashcard. Thêm thẻ đầu tiên bên trong bộ thẻ.");
            WebUtil.redirect(req, resp, "/practice/flashcards/" + deckId);
            return;
        }
        String[] segments = path.split("/");
        if (segments.length == 3 && "cards".equals(segments[2])) {
            long deckId = parseId(segments[1]);
            flashcards.addCard(userId, deckId, req.getParameter("front_content"),
                    req.getParameter("back_content"), req.getParameter("example_text"));
            Flash.success(req, "Đã thêm thẻ vào bộ flashcard.");
            WebUtil.redirect(req, resp, "/practice/flashcards/" + deckId);
            return;
        }
        if (segments.length == 3 && "review".equals(segments[2])) {
            long deckId = parseId(segments[1]);
            long cardId = parseId(req.getParameter("card_id"));
            int rating;
            try { rating = Integer.parseInt(req.getParameter("rating")); }
            catch (RuntimeException e) { throw new ValidationException("Mức đánh giá không hợp lệ"); }
            int interval = flashcards.review(userId, deckId, cardId, rating);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("ok", true);
            body.put("intervalDays", interval);
            body.put("message", interval == 0 ? "Thẻ sẽ được ôn lại ngay" : "Lần ôn tiếp theo sau " + interval + " ngày");
            WebUtil.writeJson(resp, HttpServletResponse.SC_OK, body);
            return;
        }
        throw new ValidationException("Đường dẫn flashcard không hợp lệ");
    }

    private static long parseId(String raw) {
        try {
            long id = Long.parseLong(raw);
            if (id > 0) return id;
        } catch (RuntimeException ignored) { }
        throw new ValidationException("Mã flashcard không hợp lệ");
    }
}
