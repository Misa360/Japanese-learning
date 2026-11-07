package com.jlearn.dao;

import com.jlearn.model.Chapter;
import com.jlearn.model.CourseProgress;
import com.jlearn.model.LessonContext;
import com.jlearn.model.LessonOutlineItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LessonDao {

    /** Bai hoc + chuong + khoa chua no (de kiem tra quyen o Service). */
    public LessonContext findContext(Connection c, long lessonId) throws SQLException {
        String sql = "SELECT l.id, l.title, l.duration_seconds, l.is_preview, l.status AS lesson_status, "
                + "ch.id AS chapter_id, ch.title AS chapter_title, co.id AS course_id, co.title AS course_title, "
                + "co.status AS course_status "
                + "FROM lessons l JOIN chapters ch ON ch.id = l.chapter_id JOIN courses co ON co.id = ch.course_id "
                + "WHERE l.id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                LessonContext lc = new LessonContext();
                lc.setLessonId(rs.getLong("id"));
                lc.setTitle(rs.getString("title"));
                lc.setDurationSeconds(rs.getInt("duration_seconds"));
                lc.setPreview(rs.getBoolean("is_preview"));
                lc.setLessonStatus(rs.getString("lesson_status"));
                lc.setChapterId(rs.getLong("chapter_id"));
                lc.setChapterTitle(rs.getString("chapter_title"));
                lc.setCourseId(rs.getLong("course_id"));
                lc.setCourseTitle(rs.getString("course_title"));
                lc.setCourseStatus(rs.getString("course_status"));
                return lc;
            }
        }
    }

    public List<Chapter> chapters(Connection c, long courseId) throws SQLException {
        List<Chapter> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, course_id, title, description, sort_order FROM chapters WHERE course_id = ? ORDER BY sort_order")) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Chapter ch = new Chapter();
                    ch.setId(rs.getLong("id"));
                    ch.setCourseId(rs.getLong("course_id"));
                    ch.setTitle(rs.getString("title"));
                    ch.setDescription(rs.getString("description"));
                    ch.setSortOrder(rs.getInt("sort_order"));
                    out.add(ch);
                }
            }
        }
        return out;
    }

    /**
     * Danh sach bai dang mo cua khoa theo thu tu hoc (chuong -> bai), kem trang thai cua userId.
     * userId = 0 (khach) thi moi bai la "chua hoc". Dung cho F11, F14 (thanh ben), F17, F18.
     */
    public List<LessonOutlineItem> outline(Connection c, long userId, long courseId) throws SQLException {
        String sql = "SELECT l.id, l.chapter_id, ch.title AS chapter_title, ch.sort_order AS chapter_order, l.title, "
                + "l.duration_seconds, l.is_preview, lp.status AS p_status, COALESCE(lp.last_position_seconds, 0) AS last_pos "
                + "FROM chapters ch "
                + "JOIN lessons l ON l.chapter_id = ch.id AND l.status = 'published' "
                + "LEFT JOIN lesson_progress lp ON lp.lesson_id = l.id AND lp.user_id = ? "
                + "WHERE ch.course_id = ? ORDER BY ch.sort_order, l.sort_order";
        List<LessonOutlineItem> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LessonOutlineItem it = new LessonOutlineItem();
                    it.setId(rs.getLong("id"));
                    it.setChapterId(rs.getLong("chapter_id"));
                    it.setChapterTitle(rs.getString("chapter_title"));
                    it.setChapterOrder(rs.getInt("chapter_order"));
                    it.setTitle(rs.getString("title"));
                    it.setDurationSeconds(rs.getInt("duration_seconds"));
                    it.setPreview(rs.getBoolean("is_preview"));
                    int pos = rs.getInt("last_pos");
                    it.setLastPositionSeconds(pos);
                    it.setStatus(CourseProgress.lessonStatus(rs.getString("p_status"), pos));
                    out.add(it);
                }
            }
        }
        return out;
    }
}
