package com.jlearn.filter;

import javax.servlet.*;
import java.io.IOException;

/** UTF-8 cho request/response. PHAI chay dau tien (truoc moi filter doc tham so). */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (req.getCharacterEncoding() == null) req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        chain.doFilter(req, resp);
    }
}
