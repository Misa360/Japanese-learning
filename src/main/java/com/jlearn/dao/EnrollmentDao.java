package com.jlearn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

public class EnrollmentDao {

    public boolean exists(Connection c, long userId, long courseId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM enrollments WHERE user_id = ? AND course_id = ?")) {
            ps.setLong(1, userId);
            ps.setLong(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Set<Long> enrolledCourseIds(Connection c, long userId) throws SQLException {
        Set<Long> ids = new HashSet<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT course_id FROM enrollments WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong(1));
            }
        }
        return ids;
    }

    /**
     * Them ban ghi dang ky. Tra false neu da ton tai (vi pham UNIQUE user_id + course_id), ke ca khi hai request
     * den cung luc: DB la nguoi quyet dinh cuoi cung, khong dua vao viec "kiem tra truoc roi them".
     */
    public boolean insert(Connection c, long userId, long courseId) throws SQLException {
        String sql = "INSERT INTO enrollments (user_id, course_id, status) VALUES (?, ?, 'active')";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, courseId);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == 1062) return false;   // duplicate entry
            throw e;
        }
    }

    public Long lastLessonId(Connection c, long userId, long courseId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT last_lesson_id FROM enrollments WHERE user_id = ? AND course_id = ?")) {
            ps.setLong(1, userId);
            ps.setLong(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                long v = rs.getLong(1);
                return rs.wasNull() ? null : Long.valueOf(v);
            }
        }
    }

    public void updateLastLesson(Connection c, long userId, long courseId, long lessonId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE enrollments SET last_lesson_id = ? WHERE user_id = ? AND course_id = ?")) {
            ps.setLong(1, lessonId);
            ps.setLong(2, userId);
            ps.setLong(3, courseId);
            ps.executeUpdate();
        }
    }

    /** Ghi moc hoan thanh khoa lan dau (khong ghi de). Trang thai hien thi van tinh dong theo F18. */
    public void markCompletedIfNeeded(Connection c, long userId, long courseId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE enrollments SET completed_at = NOW() WHERE user_id = ? AND course_id = ? AND completed_at IS NULL")) {
            ps.setLong(1, userId);
            ps.setLong(2, courseId);
            ps.executeUpdate();
        }
    }
}
