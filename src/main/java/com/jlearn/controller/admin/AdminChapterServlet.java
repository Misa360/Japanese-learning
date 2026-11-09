package com.jlearn.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F34 /admin/chapters
 * Quan ly chuong cua mot khoa hoc (chapters, ?course_id= bat buoc): danh sach, them, sua, doi thu tu, an/hien (chapters.status).
 * Khong xoa cung chuong vi se keo theo xoa bai hoc (ON DELETE CASCADE).
 * GET /admin/chapters?course_id= ; GET /admin/chapters/new?course_id= ; POST /admin/chapters ; POST /admin/chapters/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminChapterServlet", urlPatterns = {"/admin/chapters", "/admin/chapters/*"})
public class AdminChapterServlet extends AdminBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, isForm(req) ? "Biểu mẫu chương" : "Chương của khóa học", "admin-courses", isForm(req) ? "admin/chapter_form.jsp" : "admin/chapter_list.jsp");
    }
}
