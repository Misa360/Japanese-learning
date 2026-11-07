package com.jlearn.controller;

import com.jlearn.service.UserService;
import com.jlearn.model.User;
import com.jlearn.util.Flash;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * F08: GET/POST /profile (multipart de upload anh). Kich thuoc anh do AvatarService kiem tra (2 MB, bao loi than thien);
 * maxRequestSize chi la tran an toan cua container.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = "/profile")
@MultipartConfig(maxFileSize = -1L, maxRequestSize = 8L * 1024 * 1024, fileSizeThreshold = 256 * 1024)
public class ProfileServlet extends BaseServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        show(req, resp, null, null);
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, Map<String, String> errors, Map<String, String> form)
            throws ServletException, IOException {
        req.setAttribute("profile", userService.profile(requireUserId(req)));
        req.setAttribute("levels", userService.levels());
        req.setAttribute("errors", errors);
        req.setAttribute("form", form);
        req.setAttribute("activeNav", "profile");
        forward(req, resp, "user/profile.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long uid = requireUserId(req);
        String name = req.getParameter("full_name");
        String level = req.getParameter("target_level_id");
        String gender = req.getParameter("gender");
        Part part = req.getPart("avatar");

        Map<String, String> errors;
        if (part != null && part.getSize() > 0) {
            try (InputStream in = part.getInputStream()) {
                errors = userService.updateProfile(uid, name, level, gender, in);
            }
        } else {
            errors = userService.updateProfile(uid, name, level, gender, null);
        }
        if (errors.isEmpty()) {
            User profile = userService.profile(uid);
            req.getSession(false).setAttribute("fullName", profile.getFullName());
            req.getSession(false).setAttribute("avatarPath", profile.getAvatarPath());
            req.getSession(false).setAttribute("gender", profile.getGender());
            Flash.success(req, "Đã cập nhật hồ sơ");
            WebUtil.redirect(req, resp, "/profile");
            return;
        }
        Map<String, String> form = new LinkedHashMap<>();
        form.put("full_name", name == null ? "" : name.trim());
        form.put("target_level_id", level == null ? "" : level);
        form.put("gender", gender == null ? "" : gender);
        show(req, resp, errors, form);
    }
}
