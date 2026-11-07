package com.jlearn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProgressDao {

    /**
     * Dem bai dang mo va bai da hoan thanh cua tung khoa trong MOT cau truy van (tranh N+1).
     * Khoa khong co bai nao se khong co trong ket qua (Service coi la 0/0).
     * @return courseId -> {tong bai, bai hoan thanh}
     */
    public Map<Long, int[]> totalsByCourse(Connection c, long userId, Collection<Long> courseIds) throws SQLException {
        Map<Long, int[]> out = new HashMap<>();
        if (courseIds.isEmpty()) return out;
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < courseIds.size(); i++) in.append(i == 0 ? "?" : ",?");
        String sql = "SELECT ch.course_id, COUNT(l.id) AS total, "
                + "COUNT(CASE WHEN lp.status = 'completed' THEN 1 END) AS done "
                + "FROM chapters ch JOIN lessons l ON l.chapter_id = ch.id AND l.status = 'published' "
                + "LEFT JOIN lesson_progress lp ON lp.lesson_id = l.id AND lp.user_id = ? "
                + "WHERE ch.course_id IN (" + in + ") GROUP BY ch.course_id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, userId);
            for (Long id : courseIds) ps.setLong(i++, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.put(rs.getLong("course_id"), new int[]{rs.getInt("total"), rs.getInt("done")});
            }
        }
        return out;
    }

    /** Dam bao co dong tien do (khong loi neu da co) de khoa dong bang SELECT ... FOR UPDATE. */
    public void ensureRow(Connection c, long userId, long lessonId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO lesson_progress (user_id, lesson_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_id = user_id")) {
            ps.setLong(1, userId);
            ps.setLong(2, lessonId);
            ps.executeUpdate();
        }
    }

    /** Khoa dong tien do va tra trang thai hien tai: hai request hoan thanh cung luc se xep hang o day. */
    public String lockStatus(Connection c, long userId, long lessonId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT status FROM lesson_progress WHERE user_id = ? AND lesson_id = ? FOR UPDATE")) {
            ps.setLong(1, userId);
            ps.setLong(2, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public void markCompleted(Connection c, long userId, long lessonId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE lesson_progress SET status = 'completed', progress_percent = 100, completed_at = NOW(), "
              + "started_at = COALESCE(started_at, NOW()) WHERE user_id = ? AND lesson_id = ?")) {
            ps.setLong(1, userId);
            ps.setLong(2, lessonId);
            ps.executeUpdate();
        }
    }

    /** F16: luu giay dang xem. Khong doi trang thai "completed", khong cong xu. */
    public void upsertPosition(Connection c, long userId, long lessonId, int seconds) throws SQLException {
        String sql = "INSERT INTO lesson_progress (user_id, lesson_id, status, last_position_seconds, started_at) "
                + "VALUES (?, ?, ?, ?, NOW()) "
                + "ON DUPLICATE KEY UPDATE "
                + "status = CASE WHEN status = 'completed' THEN 'completed' "
                + "WHEN VALUES(last_position_seconds) > 0 THEN 'in_progress' ELSE status END, "
                + "last_position_seconds = VALUES(last_position_seconds), "
                + "started_at = COALESCE(started_at, NOW())";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, lessonId);
            ps.setString(3, seconds > 0 ? "in_progress" : "not_started");
            ps.setInt(4, seconds);
            ps.executeUpdate();
        }
    }
}
