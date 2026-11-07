package com.jlearn.service;

import com.jlearn.exception.BusinessRuleException;
import com.jlearn.exception.NotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jlearn.util.DbUtil;

/**
 * ===== BAN TAM cua F24 de F13/F15 chay duoc. Nguoi phu trach F24 thay bang ban cua minh. =====
 * Hop dong ma F13/F15 dung (giu nguyen chu ky khi thay):
 *   getBalance(conn, userId)
 *   award(conn, userId, amount, type, refType, refId, idempotencyKey, desc)  -> so xu thuc cong (0 neu key da dung)
 *   spend(conn, userId, amount, type, refType, refId, idempotencyKey, desc)  -> nem BusinessRuleException neu khong du xu
 * Ca hai dung CHUNG Connection cua transaction goi no (khong tu commit), nen cong/tru xu cung nhau voi enrollment / tien do.
 * Chong cong trung: UNIQUE (user_id, idempotency_key) cua coin_transactions + kiem tra truoc duoi khoa dong user.
 */
public class CoinService {

    /** Các giao dịch coin mới nhất của đúng tài khoản hiện tại. */
    public List<Map<String, Object>> recentTransactions(long userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return DbUtil.read(c -> {
            List<Map<String, Object>> rows = new ArrayList<>();
            String sql = "SELECT amount, balance_after, transaction_type, description, created_at "
                    + "FROM coin_transactions WHERE user_id = ? ORDER BY created_at DESC, id DESC LIMIT ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, userId);
                ps.setInt(2, safeLimit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("amount", rs.getLong("amount"));
                        row.put("balanceAfter", rs.getLong("balance_after"));
                        row.put("type", rs.getString("transaction_type"));
                        row.put("description", rs.getString("description"));
                        row.put("createdAt", rs.getTimestamp("created_at"));
                        rows.add(row);
                    }
                }
            }
            return rows;
        });
    }

    public long getBalance(Connection c, long userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT coin_balance FROM users WHERE id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public long award(Connection c, long userId, long amount, String type, String refType, Long refId,
                      String idempotencyKey, String description) throws SQLException {
        if (amount <= 0) return 0L;
        return apply(c, userId, amount, type, refType, refId, idempotencyKey, description) ? amount : 0L;
    }

    public void spend(Connection c, long userId, long amount, String type, String refType, Long refId,
                      String idempotencyKey, String description) throws SQLException {
        if (amount <= 0) return;
        apply(c, userId, -amount, type, refType, refId, idempotencyKey, description);
    }

    /** @return false neu idempotencyKey da duoc dung (khong lam gi them). */
    private boolean apply(Connection c, long userId, long signed, String type, String refType, Long refId,
                          String key, String description) throws SQLException {
        long balance;
        try (PreparedStatement ps = c.prepareStatement("SELECT coin_balance FROM users WHERE id = ? FOR UPDATE")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new NotFoundException("Không tìm thấy tài khoản");
                balance = rs.getLong(1);
            }
        }
        if (key != null) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT 1 FROM coin_transactions WHERE user_id = ? AND idempotency_key = ?")) {
                ps.setLong(1, userId);
                ps.setString(2, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return false;
                }
            }
        }
        long after = balance + signed;
        if (after < 0) {
            throw new BusinessRuleException("Không đủ xu: cần " + (-signed) + " xu, bạn đang có " + balance + " xu");
        }
        try (PreparedStatement ps = c.prepareStatement("UPDATE users SET coin_balance = ? WHERE id = ?")) {
            ps.setLong(1, after);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO coin_transactions (user_id, amount, balance_after, transaction_type, reference_type, "
              + "reference_id, idempotency_key, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setLong(1, userId);
            ps.setLong(2, signed);
            ps.setLong(3, after);
            ps.setString(4, type);
            ps.setString(5, refType);
            if (refId == null) ps.setNull(6, java.sql.Types.BIGINT); else ps.setLong(6, refId);
            ps.setString(7, key);
            ps.setString(8, description);
            ps.executeUpdate();
        }
        return true;
    }
}
