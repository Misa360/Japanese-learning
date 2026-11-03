package com.jlearn.filter;

import com.jlearn.util.CsrfTokens;
import com.jlearn.util.SessionKeys;
import com.jlearn.util.WebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * F05 - CsrfMiddleware.
 *  - Moi request: dam bao session co token va dat request attribute "csrfToken" cho JSP.
 *  - POST/PUT/DELETE/PATCH: token lay tu tham so csrf_token HOAC header X-CSRF-Token; sai/thieu -> 403.
 * LUU Y: EncodingFilter phai chay TRUOC filter nay (doc tham so truoc khi dat UTF-8 se hong tieng Viet).
 * Form multipart (upload anh) can servlet co @MultipartConfig thi tham so csrf_token moi doc duoc; hoac gui kem header.
 */
public class CsrfFilter implements Filter {
    private static final Set<String> UNSAFE = new HashSet<>(Arrays.asList("POST", "PUT", "DELETE", "PATCH"));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(true);
        req.setAttribute(SessionKeys.CSRF, CsrfTokens.getOrCreate(session));

        if (UNSAFE.contains(req.getMethod().toUpperCase())) {
            if (req.getCharacterEncoding() == null) req.setCharacterEncoding("UTF-8");
            String submitted = req.getHeader(CsrfTokens.HEADER);
            if (submitted == null || submitted.isEmpty()) submitted = req.getParameter(CsrfTokens.PARAM);
            if (!CsrfTokens.matches(session, submitted)) {
                WebUtil.reject(req, resp, HttpServletResponse.SC_FORBIDDEN,
                        "Phiên làm việc không hợp lệ hoặc đã hết hạn. Hãy tải lại trang và thử lại.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
