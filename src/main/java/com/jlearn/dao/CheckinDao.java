package com.jlearn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TOI THIEU cho dashboard (F06): chi DOC chuoi ngay diem danh. Ghi diem danh + cong xu la F28 (CheckinService) - nguoi phu trach F28 mo rong.
 * Ngay tinh theo mui gio cua session DB (Asia/Ho_Chi_Minh): CURDATE().
 */
public class CheckinDao {
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
