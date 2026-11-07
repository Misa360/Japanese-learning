package com.jlearn.controller;

import com.jlearn.service.AvatarService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * GET /media/avatar/{32 hex}.png|jpg : phuc vu anh tu thu muc upload NGOAI webapp.
 * Ten file phai khop dung mau do he thong tao (chong ../ va doan ten), Content-Type co dinh theo duoi, nosniff.
 */
@WebServlet(name = "AvatarServlet", urlPatterns = "/media/avatar/*")
public class AvatarServlet extends HttpServlet {
    private final AvatarService avatarService = new AvatarService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        String name = (info != null && info.length() > 1) ? info.substring(1) : null;
        Path p = avatarService.resolve(name);
        if (p == null || !Files.isRegularFile(p)) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        resp.setContentType(name.endsWith(".png") ? "image/png" : "image/jpeg");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Content-Disposition", "inline");
        resp.setHeader("Cache-Control", "public, max-age=86400, immutable");
        resp.setContentLengthLong(Files.size(p));
        Files.copy(p, resp.getOutputStream());
    }
}
