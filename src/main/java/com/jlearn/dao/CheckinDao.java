package com.jlearn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * TOI THIEU cho dashboard (F06): chi DOC chuoi ngay diem danh. Ghi diem danh + cong xu la F28 (CheckinService) - nguoi phu trach F28 mo rong.
 * Ngay tinh theo mui gio cua session DB (Asia/Ho_Chi_Minh): CURDATE().
 */
public class CheckinDao {
    /** Ngày người học đã điểm danh trong khoảng 7 ngày gần nhất (theo ngày DB). */
    public Set<LocalDate> recentDates(Connection c, long userId) throws SQLException {
        String sql = "SELECT checkin_date FROM checkins WHERE user_id = ? "
                + "AND checkin_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND CURDATE()";
        Set<LocalDate> dates = new LinkedHashSet<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) dates.add(rs.getDate("checkin_date").toLocalDate());
            }
        }
        return dates;
    }

    /** @return {streakDays, checkedInToday(0/1)}. Chuoi con "song" neu lan diem danh cuoi la hom nay hoac hom qua. */
    public int[] streak(Connection c, long userId) throws SQLException {
        String sql = "SELECT streak_day, DATEDIFF(CURDATE(), checkin_date) AS days_ago FROM checkins "
                + "WHERE user_id = ? ORDER BY checkin_date DESC LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new int[]{0, 0};
                int daysAgo = rs.getInt("days_ago");
                if (daysAgo == 0) return new int[]{rs.getInt("streak_day"), 1};
                if (daysAgo == 1) return new int[]{rs.getInt("streak_day"), 0};
                return new int[]{0, 0};
            }
        }
    }
}
