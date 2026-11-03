package com.jlearn.controller;

import com.jlearn.exception.NotFoundException;
import com.jlearn.exception.ValidationException;
import com.jlearn.model.CompletionResult;
import com.jlearn.model.LessonPage;
import com.jlearn.service.LessonService;
import com.jlearn.service.ProgressService;
import com.jlearn.util.Flash;
import com.jlearn.util.RateGate;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * F14: GET  /learn/{lessonId}
 * F15: POST /learn/{lessonId}/complete   (JSON neu la AJAX, nguoc lai chuyen trang kem flash)
 * F16: POST /learn/{lessonId}/position   (JS goi moi 15-30 giay)
 */
@WebServlet(name = "LearnServlet", urlPatterns = "/learn/*")
public class LearnServlet extends BaseServlet {
    private static final Pattern ROUTE = Pattern.compile("^/(\\d{1,18})(?:/(complete|position))?$");
    private static final Pattern SECONDS = Pattern.compile("^\\d{1,9}$");
    private static final long MIN_POSITION_INTERVAL_MS = 3000L;

    private final LessonService lessonService = new LessonService();
    private final ProgressService progressService = new ProgressService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Matcher m = ROUTE.matcher(req.getPathInfo() == null ? "" : req.getPathInfo());
        if (!m.matches()) throw new NotFoundException();
        if (m.group(2) != null) {
            resp.setHeader("Allow", "POST");
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        long lessonId = Long.parseLong(m.group(1));
        LessonPage page = lessonService.loadLessonPage(requireUserId(req), WebUtil.isAdmin(req), lessonId);
        resp.setHeader("Cache-Control", "private, no-store");
        req.setAttribute("lessonPage", page);
        forward(req, resp, "learning/lesson.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Matcher m = ROUTE.matcher(req.getPathInfo() == null ? "" : req.getPathInfo());
        if (!m.matches() || m.group(2) == null) throw new NotFoundException();
        long lessonId = Long.parseLong(m.group(1));
        if ("complete".equals(m.group(2))) complete(req, resp, lessonId);
        else savePosition(req, resp, lessonId);
    }

    private void complete(HttpServletRequest req, HttpServletResponse resp, long lessonId) throws IOException {
        CompletionResult r = progressService.completeLesson(requireUserId(req), lessonId);
        if (WebUtil.wantsJson(req)) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("ok", true);
            body.put("alreadyCompleted", r.isAlreadyCompleted());
            body.put("coinsAwarded", r.getCoinsAwarded());
            body.put("percent", r.getProgress().getPercent());
            body.put("completedLessons", r.getProgress().getCompletedLessons());
            body.put("totalLessons", r.getProgress().getTotalLessons());
            body.put("courseCompleted", r.getProgress().isCompleted());
            WebUtil.writeJson(resp, 200, body);
            return;
        }
        if (r.isAlreadyCompleted()) {
            Flash.info(req, "Bạn đã hoàn thành bài này rồi");
        } else {
            String msg = "Hoàn thành bài học! Tiến độ khóa: " + r.getProgress().getPercent() + "%";
            if (r.getCoinsAwarded() > 0) msg += " (+" + r.getCoinsAwarded() + " xu)";
            Flash.success(req, msg);
        }
        WebUtil.redirect(req, resp, "/learn/" + lessonId);
    }

    private void savePosition(HttpServletRequest req, HttpServletResponse resp, long lessonId) throws IOException {
        long uid = requireUserId(req);
        String raw = req.getParameter("position_seconds");
        if (raw == null || !SECONDS.matcher(raw.trim()).matches()) {
            throw new ValidationException("position_seconds phải là số nguyên không âm");
        }
        if (!RateGate.allow(req.getSession(), "pos:" + lessonId, MIN_POSITION_INTERVAL_MS)) {
            WebUtil.writeJson(resp, 429, WebUtil.error("Gửi quá nhanh"));
            return;
        }
        int saved = lessonService.savePosition(uid, WebUtil.isAdmin(req), lessonId, Integer.parseInt(raw.trim()));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("saved", saved);
        WebUtil.writeJson(resp, 200, body);
    }
}
