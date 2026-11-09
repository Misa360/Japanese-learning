package com.jlearn.controller.admin;

import com.jlearn.controller.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Lop co so cho cac servlet khu quan tri (F33-F39). Moi duong dan /admin/* da duoc AdminFilter chan (403 neu khong phai admin),
 * nen servlet con khong can kiem tra lai role. Helper o day chi dat pageTitle/activeNav roi forward toi JSP.
 */
public abstract class AdminBaseServlet extends BaseServlet {

    protected void render(HttpServletRequest req, HttpServletResponse resp, String pageTitle, String activeNav, String view)
            throws ServletException, IOException {
        req.setAttribute("pageTitle", pageTitle);
        req.setAttribute("activeNav", activeNav);
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);   // khong qua BaseServlet.forward: admin khong can sidebarSummary (xu/chuoi) cua hoc vien
    }

    /** true neu URL co phan sau servlet path, vi du /admin/courses/new hoac /admin/courses/5 (trang form); false neu la trang danh sach. */
    protected static boolean isForm(HttpServletRequest req) {
        String p = req.getPathInfo();
        return p != null && !p.isEmpty() && !"/".equals(p);
    }
}
