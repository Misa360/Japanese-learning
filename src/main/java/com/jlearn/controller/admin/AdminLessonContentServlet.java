package com.jlearn.controller.admin;

import com.jlearn.model.Material;
import com.jlearn.service.AdminService;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F35 /admin/lesson-content - Noi dung ben trong mot bai hoc (?lesson_id= bat buoc, ?tab=vocabulary|kanji|grammar|materials).
 * Tu vung/Kanji/Ngu phap: chon tu kho dung chung roi gan vao bai (lesson_vocabulary / lesson_kanji / lesson_grammar, co sort_order) hoac go ra.
 * Tai lieu (materials): them/sua/xoa video (YouTube/URL), document, text cua bai.
 * GET /admin/lesson-content?lesson_id=&tab= ; GET /admin/lesson-content/materials/new?lesson_id= ; GET /admin/lesson-content/materials/{id}
 * POST /admin/lesson-content/attach ; POST /admin/lesson-content/detach ; POST /admin/lesson-content/materials ; POST /admin/lesson-content/materials/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38). Hien chi la khung.
 */
@WebServlet(name = "AdminLessonContentServlet", urlPatterns = {"/admin/lesson-content", "/admin/lesson-content/*"})
public class AdminLessonContentServlet extends AdminBaseServlet {
    private final AdminService adminService = new AdminService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path != null && path.startsWith("/materials")) {
            String lessonId=req.getParameter("lesson_id");
            if(path.startsWith("/materials/new")) {
                Material material=new Material();
                try { material.setLessonId(Long.parseLong(lessonId)); }
                catch(Exception ex) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
                req.setAttribute("material",material); req.setAttribute("lesson",adminService.lessonContext(material.getLessonId()));
            } else {
                try {
                    long materialId=Long.parseLong(path.substring("/materials/".length()));
                    Material material=adminService.material(materialId);
                    if(lessonId!=null && Long.parseLong(lessonId)!=material.getLessonId()) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                    req.setAttribute("material",material); req.setAttribute("lesson",adminService.lessonContext(material.getLessonId()));
                } catch(NumberFormatException ex) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            }
            render(req, resp, "Biểu mẫu tài liệu", "admin-courses", "admin/material_form.jsp");
            return;
        }
        try {
            long lessonId=Long.parseLong(req.getParameter("lesson_id"));
            req.setAttribute("lesson",adminService.lessonContext(lessonId));
            req.setAttribute("materials",adminService.lessonMaterials(lessonId));
            req.setAttribute("quizzes",adminService.lessonQuizzes(lessonId));
            render(req,resp,"Nội dung bài học","admin-courses","admin/lesson_detail.jsp");
        } catch(NumberFormatException ex) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Material material=new Material();
        try {
            String id=req.getParameter("id"); if(id!=null && !id.isEmpty()) material.setId(Long.parseLong(id));
            material.setLessonId(Long.parseLong(req.getParameter("lesson_id")));
            material.setType(req.getParameter("material_type")); material.setTitle(req.getParameter("title"));
            material.setMediaUrl(req.getParameter("media_url")); material.setContentText(req.getParameter("content_text"));
            String sort=req.getParameter("sort_order"); material.setSortOrder(sort==null || sort.isEmpty()?0:Integer.parseInt(sort));
            adminService.saveMaterial(material,WebUtil.userId(req));
            resp.sendRedirect(req.getContextPath()+"/admin/lessons/"+material.getLessonId());
        } catch(NumberFormatException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Dữ liệu số không hợp lệ");
        }
    }
}
