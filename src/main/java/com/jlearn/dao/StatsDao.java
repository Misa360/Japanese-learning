package com.jlearn.dao;

import com.jlearn.model.MyCourseItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Truy van tong hop cho dashboard (F06) va khoa hoc cua toi (F07). */
public class StatsDao {

    /** So bai da hoan thanh (chi tinh bai dang mo cua khoa dang mo). */
    public int completedLessons(Connection c, long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM lesson_progress lp JOIN lessons l ON l.id = lp.lesson_id AND l.status = 'published' "
                + "JOIN chapters ch ON ch.id = l.chapter_id JOIN courses co ON co.id = ch.course_id AND co.status = 'published' "
                + "WHERE lp.user_id = ? AND lp.status = 'completed'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** Cac khoa da dang ky va dang mo, moi dang ky gan nhat truoc. Tien do tinh them o ProgressService (F18). */
    public List<MyCourseItem> enrolledCourses(Connection c, long userId) throws SQLException {
        String sql = "SELECT co.id, co.title, co.thumbnail_path, lv.code AS level_code, e.enrolled_at "
                + "FROM enrollments e JOIN courses co ON co.id = e.course_id AND co.status = 'published' "
                + "LEFT JOIN levels lv ON lv.id = co.level_id WHERE e.user_id = ? AND e.status = 'active' "
                + "ORDER BY e.enrolled_at DESC, e.id DESC";
        List<MyCourseItem> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MyCourseItem it = new MyCourseItem();
                    it.setCourseId(rs.getLong("id"));
                    it.setTitle(rs.getString("title"));
                    it.setThumbnailPath(rs.getString("thumbnail_path"));
                    it.setLevelCode(rs.getString("level_code"));
                    Timestamp ts = rs.getTimestamp("enrolled_at");
                    it.setEnrolledAt(ts == null ? null : new java.util.Date(ts.getTime()));
                    out.add(it);
                }
            }
        }
        return out;
    }
}
