package com.jlearn.controller.admin;

import com.jlearn.model.Pager;
import com.jlearn.service.AdminService;
import com.jlearn.util.Flash;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F33 /admin/users
 * Quan ly nguoi dung: danh sach (tim theo ten/email, loc role/status, phan trang), khoa/mo khoa (users.status), doi vai tro.
 * GET /admin/users ; POST /admin/users/{id}/status ; POST /admin/users/{id}/role
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users", "/admin/users/*"})
public class AdminUserServlet extends AdminBaseServlet {
    private static final int PAGE_SIZE=20;
    private final AdminService adminService=new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query=req.getParameter("q");if(query!=null){query=query.trim();if(query.length()>100)query=query.substring(0,100);if(query.isEmpty())query=null;}
        String role=req.getParameter("role");if(!"user".equals(role)&&!"admin".equals(role))role=null;
        String status=req.getParameter("status");if(!"active".equals(status)&&!"locked".equals(status))status=null;
        int page=1;String rawPage=req.getParameter("page");if(rawPage!=null&&rawPage.matches("\\d{1,6}"))page=Integer.parseInt(rawPage);
        long total=adminService.adminUserCount(query,role,status);
        Pager pager=Pager.of(page,PAGE_SIZE,total);
        req.setAttribute("users",adminService.adminUsers(query,role,status,PAGE_SIZE,pager.getOffset()));
        req.setAttribute("pager",pager);req.setAttribute("query",query);req.setAttribute("roleFilter",role);req.setAttribute("statusFilter",status);
        req.setAttribute("currentUserId",WebUtil.userId(req));
        render(req, resp, "Người dùng", "admin-users", "admin/user_list.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");String[] parts=req.getPathInfo()==null?new String[0]:req.getPathInfo().split("/");
        try {
            if(parts.length!=3 || !("status".equals(parts[2])||"role".equals(parts[2]))) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            long id=Long.parseLong(parts[1]);Long actor=WebUtil.userId(req);
            if("status".equals(parts[2]))adminService.changeUserStatus(id,req.getParameter("status"),actor);
            else adminService.changeUserRole(id,req.getParameter("role"),actor);
            Flash.success(req,"Đã cập nhật tài khoản người dùng.");
            resp.sendRedirect(req.getContextPath()+buildListUrl(req));
        } catch(NumberFormatException ex){resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Mã người dùng không hợp lệ");}
    }

    private String buildListUrl(HttpServletRequest req) {
        StringBuilder url=new StringBuilder("/admin/users?");boolean has=false;
        String[] names={"q","role","status"};
        for(String name:names){String value=req.getParameter("return_"+name);if(value!=null&&!value.trim().isEmpty()){
            if(has)url.append('&');url.append(name).append('=').append(java.net.URLEncoder.encode(value,java.nio.charset.StandardCharsets.UTF_8));has=true;
        }}
        return has?url.toString():"/admin/users";
    }
}
