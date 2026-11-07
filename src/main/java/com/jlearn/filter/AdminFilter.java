package com.jlearn.filter;

import com.jlearn.util.WebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F05 - AdminMiddleware. Role khac admin vao /admin/* thi 403. Map: /admin/*  (khop ca "/admin"). */
public class AdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (WebUtil.userId(req) == null) {            // phong khi filter nay duoc map rieng, khong qua AuthenticationFilter
            WebUtil.rejectUnauthenticated(req, resp);
            return;
        }
        if (!WebUtil.isAdmin(req)) {
            WebUtil.reject(req, resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực quản trị");
            return;
        }
        chain.doFilter(request, response);
    }
}
