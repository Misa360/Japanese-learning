package com.jlearn.dao;

import com.jlearn.model.HistoryItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StudyLogDao {

    public void insert(Connection c, long userId, long lessonId, String activityType, int durationSeconds) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO study_logs (user_id, lesson_id, activity_type, duration_seconds) VALUES (?, ?, ?, ?)")) {
            ps.setLong(1, userId);
            ps.setLong(2, lessonId);
            ps.setString(3, activityType);
            ps.setInt(4, durationSeconds);
            ps.executeUpdate();
        }
    }

    /** F14: khong ghi lai neu cung bai, cung loai da duoc ghi trong N phut gan day (F5 lien tuc khong lam day lich su). */
    public void insertIfNotRecent(Connection c, long userId, long lessonId, String activityType, int minutes) throws SQLException {
        String sql = "INSERT INTO study_logs (user_id, lesson_id, activity_type, duration_seconds) "
                + "SELECT ?, ?, ?, 0 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM study_logs WHERE user_id = ? AND lesson_id = ? "
                + "AND activity_type = ? AND logged_at >= NOW() - INTERVAL ? MINUTE)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, lessonId);
            ps.setString(3, activityType);
            ps.setLong(4, userId);
            ps.setLong(5, lessonId);
            ps.setString(6, activityType);
            ps.setInt(7, minutes);
            ps.executeUpdate();
        }
    }

    /** F09: so dong lich su (chi bai con ton tai). */
    public long countHistory(Connection c, long userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM study_logs WHERE user_id = ? AND lesson_id IS NOT NULL")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public List<HistoryItem> history(Connection c, long userId, int offset, int limit) throws SQLException {
        String sql = "SELECT sl.logged_at, COALESCE(NULLIF(sl.duration_seconds, 0), l.duration_seconds) AS dur, "
                + "l.id AS lesson_id, l.title AS lesson_title, co.id AS course_id, co.title AS course_title "
                + "FROM study_logs sl JOIN lessons l ON l.id = sl.lesson_id "
                + "JOIN chapters ch ON ch.id = l.chapter_id JOIN courses co ON co.id = ch.course_id "
                + "WHERE sl.user_id = ? ORDER BY sl.logged_at DESC, sl.id DESC LIMIT ? OFFSET ?";
        List<HistoryItem> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistoryItem h = new HistoryItem();
                    h.setLessonId(rs.getLong("lesson_id"));
                    h.setLessonTitle(rs.getString("lesson_title"));
                    h.setCourseId(rs.getLong("course_id"));
                    h.setCourseTitle(rs.getString("course_title"));
                    Timestamp ts = rs.getTimestamp("logged_at");
                    h.setLoggedAt(ts == null ? null : new java.util.Date(ts.getTime()));
                    h.setDurationSeconds(rs.getInt("dur"));
                    out.add(h);
                }
            }
        }
        return out;
    }

    /** Khoa hoc nguoi dung hoc gan nhat (cho nut "Tiep tuc hoc" o dashboard). */
    public Long lastStudiedCourseId(Connection c, long userId) throws SQLException {
        String sql = "SELECT ch.course_id FROM study_logs sl JOIN lessons l ON l.id = sl.lesson_id "
                + "JOIN chapters ch ON ch.id = l.chapter_id JOIN enrollments e ON e.course_id = ch.course_id AND e.user_id = sl.user_id "
                + "JOIN courses co ON co.id = ch.course_id AND co.status = 'published' "
                + "WHERE sl.user_id = ? ORDER BY sl.logged_at DESC, sl.id DESC LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Long.valueOf(rs.getLong(1)) : null;
            }
        }
    }
}
