package com.jlearn.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Header bao mat co ban cho moi phan hoi. CSP cho phep YouTube (iframe + API) dung voi F14/F16. */
public class SecurityHeadersFilter implements Filter {
    private static final String CSP = "default-src 'self'; "
            + "script-src 'self' https://www.youtube.com https://s.ytimg.com; "
            + "frame-src https://www.youtube-nocookie.com https://www.youtube.com; "
            + "img-src 'self' data: https://i.ytimg.com; media-src 'self'; style-src 'self' 'unsafe-inline'; "
            + "object-src 'none'; base-uri 'self'; form-action 'self'; frame-ancestors 'self'";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse r = (HttpServletResponse) resp;
        r.setHeader("X-Content-Type-Options", "nosniff");
        r.setHeader("X-Frame-Options", "SAMEORIGIN");
        r.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        r.setHeader("Content-Security-Policy", CSP);
        chain.doFilter(req, resp);
    }
}
