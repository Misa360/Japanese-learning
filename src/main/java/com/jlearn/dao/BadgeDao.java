package com.jlearn.dao;

import com.jlearn.model.BadgeSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** TOI THIEU cho dashboard (F06): doc huy hieu moi nhat. Cap huy hieu (BadgeService.check) la F30. */
public class BadgeDao {
    public BadgeSummary latest(Connection c, long userId) throws SQLException {
        String sql = "SELECT b.id, b.name, b.description, b.icon_path, ub.earned_at FROM user_badges ub "
                + "JOIN badges b ON b.id = ub.badge_id WHERE ub.user_id = ? ORDER BY ub.earned_at DESC LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                BadgeSummary b = new BadgeSummary();
                b.setId(rs.getLong("id"));
                b.setName(rs.getString("name"));
                b.setDescription(rs.getString("description"));
                b.setIconPath(rs.getString("icon_path"));
                Timestamp ts = rs.getTimestamp("earned_at");
                b.setEarnedAt(ts == null ? null : new java.util.Date(ts.getTime()));
                return b;
            }
        }
    }
}
