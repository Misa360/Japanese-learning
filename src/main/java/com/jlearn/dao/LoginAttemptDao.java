package com.jlearn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/** F02: nhat ky dang nhap sai de khoa tam. Dung gio cua DB (NOW()) de khong lech dong ho giua cac node. */
public class LoginAttemptDao {

    public static final class Lock {
        public final int failures;
        public final long secondsLeft;     // > 0 va failures >= max thi dang khoa
        Lock(int failures, long secondsLeft) { this.failures = failures; this.secondsLeft = secondsLeft; }
    }

    public void recordFailure(Connection c, Long userId, String identifier, byte[] ip) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO login_attempts (user_id, identifier, ip_address, success) VALUES (?, ?, ?, 0)")) {
            if (userId == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, userId);
            ps.setString(2, identifier.length() > 255 ? identifier.substring(0, 255) : identifier);
            if (ip == null) ps.setNull(3, Types.VARBINARY); else ps.setBytes(3, ip);
            ps.executeUpdate();
        }
    }

    /** So lan sai trong cua so va so giay con lai cua dot khoa (tinh tu lan sai gan nhat + cua so). */
    public Lock byIdentifier(Connection c, String identifier, int windowMinutes) throws SQLException {
        return query(c, "identifier = ?", identifier, null, windowMinutes);
    }

    public Lock byIp(Connection c, byte[] ip, int windowMinutes) throws SQLException {
        return query(c, "ip_address = ?", null, ip, windowMinutes);
    }

    private Lock query(Connection c, String where, String str, byte[] bin, int windowMinutes) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt, "
                + "COALESCE(GREATEST(TIMESTAMPDIFF(SECOND, NOW(), MAX(attempted_at) + INTERVAL ? MINUTE), 0), 0) AS left_s "
                + "FROM login_attempts WHERE success = 0 AND " + where + " AND attempted_at >= NOW() - INTERVAL ? MINUTE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, windowMinutes);
            if (str != null) ps.setString(2, str); else ps.setBytes(2, bin);
            ps.setInt(3, windowMinutes);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Lock(rs.getInt("cnt"), rs.getLong("left_s"));
            }
        }
    }

    /** Dang nhap dung: xoa cac lan sai cua dinh danh nay. */
    public void clear(Connection c, String identifier) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM login_attempts WHERE identifier = ?")) {
            ps.setString(1, identifier);
            ps.executeUpdate();
        }
    }

    /** Don rac dinh ky (goi tu job/admin): bo ban ghi cu hon N ngay. */
    public int purgeOlderThanDays(Connection c, int days) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM login_attempts WHERE attempted_at < NOW() - INTERVAL ? DAY")) {
            ps.setInt(1, days);
            return ps.executeUpdate();
        }
    }
}
