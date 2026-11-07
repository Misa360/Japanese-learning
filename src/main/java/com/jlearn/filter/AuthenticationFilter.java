package com.jlearn.filter;

import com.jlearn.util.WebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * F05 - AuthMiddleware. Mac dinh TU CHOI: moi duong dan phai dang nhap, tru danh sach cong khai duoi day.
 * Khi them trang cong khai moi (vi du /forgot-password) thi them vao PUBLIC_* o day.
 * Map: /*  (xem web.xml.fragment.xml, thu tu: Encoding -> Authentication -> Admin -> Csrf).
 */
public class AuthenticationFilter implements Filter {

    private static final Set<String> PUBLIC_EXACT = new HashSet<>(Arrays.asList(
            "/", "/index.jsp", "/login", "/register", "/roadmap", "/favicon.ico"));
    private static final List<String> PUBLIC_PREFIX = Arrays.asList("/assets/", "/media/avatar/");
    /** Chi cho phep GET: chi tiet khoa hoc la cong khai, con /courses/{id}/enroll va /continue thi khong. */
    private static final Pattern PUBLIC_GET_REGEX = Pattern.compile("^/courses/\\d+$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (isPublic(req.getMethod(), WebUtil.path(req)) || WebUtil.userId(req) != null) {
            chain.doFilter(request, response);
            return;
        }
        WebUtil.rejectUnauthenticated(req, resp);
    }

    static boolean isPublic(String method, String path) {
        if (PUBLIC_EXACT.contains(path)) return true;
        for (String prefix : PUBLIC_PREFIX) {
            if (path.startsWith(prefix)) return true;
        }
        return "GET".equalsIgnoreCase(method) && PUBLIC_GET_REGEX.matcher(path).matches();
    }
}
