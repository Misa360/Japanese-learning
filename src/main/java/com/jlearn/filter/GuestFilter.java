package com.jlearn.filter;

import com.jlearn.util.WebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F05 - GuestMiddleware. Da dang nhap ma vao /login, /register thi chuyen /dashboard (admin thi /admin). Map: /login, /register. */
public class GuestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (WebUtil.userId(req) != null) {
            WebUtil.redirect(req, resp, WebUtil.isAdmin(req) ? "/admin" : "/dashboard");
            return;
        }
        chain.doFilter(request, response);
    }
}
