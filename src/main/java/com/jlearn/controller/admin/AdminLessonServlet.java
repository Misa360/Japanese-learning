package com.jlearn.controller.admin;

import com.jlearn.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F35 /admin/lessons
 * Quan ly bai hoc trong mot khoa hoc (?course_id=, nhom theo chuong): danh sach, them, sua, doi thu tu, doi status, is_preview.
 * Moi dong bai hoc co 2 lien ket: Noi dung (/admin/lesson-content?lesson_id=) va Bai tap (/admin/quizzes?type=lesson&lesson_id=).
 * GET /admin/lessons?course_id= ; GET /admin/lessons/new?course_id= ; GET /admin/lessons/{id} ; POST /admin/lessons ; POST /admin/lessons/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminLessonServlet", urlPatterns = {"/admin/lessons", "/admin/lessons/*"})
public class AdminLessonServlet extends AdminBaseServlet {
    private final AdminService adminService = new AdminService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path != null && !path.equals("/") && !path.isEmpty() && !"/new".equals(path)) {
            try {
                long id = Long.parseLong(path.substring(1));
                req.setAttribute("lesson", adminService.lessonContext(id));
                req.setAttribute("materials", adminService.lessonMaterials(id));
                req.setAttribute("quizzes", adminService.lessonQuizzes(id));
                render(req, resp, "Nội dung bài học", "admin-courses", "admin/lesson_detail.jsp");
            } catch (NumberFormatException ex) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); }
            return;
        }
        if ("/new".equals(path)) {
            render(req, resp, "Thêm bài học", "admin-courses", "admin/lesson_form.jsp");
            return;
        }
        render(req, resp, "Quản lý bài học", "admin-courses", "admin/lesson_list.jsp");
    }
}
