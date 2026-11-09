package com.jlearn.controller.admin;

import com.jlearn.model.Course;
import com.jlearn.service.AdminService;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F34 /admin/courses
 * Quan ly khoa hoc (courses): danh sach, them, sua, doi status (draft/published/archived). Day la cua vao cua cay noi dung:
 * Khoa hoc -> Chuong (/admin/chapters?course_id=) -> Bai hoc (/admin/lessons?course_id=) -> Noi dung va Bai tap cua bai.
 * GET /admin/courses ; GET /admin/courses/new ; GET /admin/courses/{id} ; POST /admin/courses ; POST /admin/courses/{id} ; POST /admin/courses/{id}/status
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminCourseServlet", urlPatterns = {"/admin/courses", "/admin/courses/*"})
public class AdminCourseServlet extends AdminBaseServlet {
    private final AdminService adminService = new AdminService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("levels", adminService.levels());
        req.setAttribute("allCourses", adminService.courses());
        String path = req.getPathInfo();
        if (path == null || path.equals("/") || path.isEmpty()) {
            req.setAttribute("courses", adminService.courses());
            render(req, resp, "Quản lý khóa học", "admin-courses", "admin/course_list.jsp");
            return;
        }
        if ("/new".equals(path)) { req.setAttribute("course", new Course()); req.setAttribute("formAction", "/admin/courses/new"); }
        else {
            boolean edit = path.endsWith("/edit");
            String idPart = path.substring(1, edit ? path.length() - 5 : path.length());
            try {
                Course course = adminService.course(Long.parseLong(idPart));
                if (!edit) {
                    req.setAttribute("course", course);
                    req.setAttribute("courseLessons", adminService.courseLessons(course.getId()));
                    render(req, resp, course.getTitle(), "admin-courses", "admin/course_detail.jsp");
                    return;
                }
                req.setAttribute("course", course);
                req.setAttribute("formAction", "/admin/courses/" + course.getId() + "/edit");
            }
            catch (NumberFormatException ex) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        }
        render(req, resp, "Biểu mẫu khóa học", "admin-courses", "admin/course_form.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        Course co = new Course();
        try {
            if (path != null && !path.equals("/") && !path.isEmpty() && !"/new".equals(path)) {
                String idPart = path.endsWith("/edit") ? path.substring(1, path.length()-5) : path.substring(1);
                co.setId(Long.parseLong(idPart));
            }
            co.setTitle(req.getParameter("title")); co.setSlug(req.getParameter("slug"));
            co.setDescription(req.getParameter("description")); co.setThumbnailPath(req.getParameter("thumbnail_path"));
            co.setLevelId(Long.parseLong(req.getParameter("level_id")));
            co.setFree("true".equals(req.getParameter("is_free")));
            String coins = req.getParameter("unlock_coins"); co.setUnlockCoins(coins == null || coins.isEmpty() ? 0 : Long.parseLong(coins));
            String required = req.getParameter("required_course_id"); co.setRequiredCourseId(required == null || required.trim().isEmpty() ? null : Long.valueOf(required));
            co.setStatus(req.getParameter("status"));
            adminService.saveCourse(co, WebUtil.userId(req));
            resp.sendRedirect(req.getContextPath() + "/admin/courses" + (co.getId() > 0 ? "/" + co.getId() : ""));
        } catch (NumberFormatException ex) {
            req.setAttribute("formError", "Vui lòng kiểm tra các trường số.");
            req.setAttribute("course", co); req.setAttribute("levels", adminService.levels()); req.setAttribute("allCourses", adminService.courses());
            req.setAttribute("formAction", co.getId() > 0 ? "/admin/courses/" + co.getId() + "/edit" : "/admin/courses/new");
            render(req, resp, "Biểu mẫu khóa học", "admin-courses", "admin/course_form.jsp");
        }
    }
}
