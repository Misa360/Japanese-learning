package com.jlearn.controller;

import com.jlearn.exception.NotFoundException;
import com.jlearn.model.CourseDetail;
import com.jlearn.model.EnrollResult;
import com.jlearn.model.NextLesson;
import com.jlearn.service.CourseService;
import com.jlearn.service.EnrollmentService;
import com.jlearn.service.LessonService;
import com.jlearn.util.Flash;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * F11: GET  /courses/{id}            chi tiet khoa
 * F12: POST /courses/{id}/enroll     dang ky (CSRF do CsrfFilter kiem tra)
 * F17: GET  /courses/{id}/continue   nut "Tiep tuc hoc"
 */
@WebServlet(name = "CourseServlet", urlPatterns = "/courses/*")
public class CourseServlet extends BaseServlet {
    private static final Pattern ROUTE = Pattern.compile("^/(\\d{1,18})(?:/(enroll|continue))?$");

    private final CourseService courseService = new CourseService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final LessonService lessonService = new LessonService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if (info == null || "/".equals(info)) {
            WebUtil.redirect(req, resp, "/roadmap");
            return;
        }
        if ("/favorites".equals(info)) {
            req.setAttribute("activeNav", "favorites");
            forward(req, resp, "user/favorite_videos.jsp");
            return;
        }
        Matcher m = ROUTE.matcher(info);
        if (!m.matches()) throw new NotFoundException();
        long courseId = Long.parseLong(m.group(1));
        String action = m.group(2);

        if (action == null) {
            CourseDetail detail = courseService.getDetail(WebUtil.userId(req), WebUtil.isAdmin(req), courseId);
            req.setAttribute("detail", detail);
            forward(req, resp, "course/detail.jsp");
        } else if ("continue".equals(action)) {
            doContinue(req, resp, courseId);
        } else {
            resp.setHeader("Allow", "POST");
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Matcher m = ROUTE.matcher(req.getPathInfo() == null ? "" : req.getPathInfo());
        if (!m.matches() || !"enroll".equals(m.group(2))) throw new NotFoundException();
        long courseId = Long.parseLong(m.group(1));

        EnrollResult r = enrollmentService.enroll(requireUserId(req), courseId);
        if (EnrollResult.ENROLLED.equals(r.getStatus())) Flash.success(req, r.getMessage());
        else if (EnrollResult.ALREADY_ENROLLED.equals(r.getStatus())) Flash.info(req, r.getMessage());
        else Flash.error(req, r.getMessage());
        WebUtil.redirect(req, resp, "/courses/" + courseId);
    }

    private void doContinue(HttpServletRequest req, HttpServletResponse resp, long courseId) throws IOException {
        NextLesson next = lessonService.nextLesson(requireUserId(req), courseId);
        switch (next.getKind()) {
            case NextLesson.NOT_ENROLLED:
                Flash.error(req, "Bạn chưa đăng ký khóa học này");
                WebUtil.redirect(req, resp, "/courses/" + courseId);
                break;
            case NextLesson.NO_LESSONS:
                Flash.info(req, "Khóa học này chưa có bài học nào");
                WebUtil.redirect(req, resp, "/courses/" + courseId);
                break;
            case NextLesson.ALL_DONE:
                Flash.success(req, "Bạn đã hoàn thành khóa học");
                WebUtil.redirect(req, resp, "/learn/" + next.getLessonId());
                break;
            default:
                WebUtil.redirect(req, resp, "/learn/" + next.getLessonId());
        }
    }
}
