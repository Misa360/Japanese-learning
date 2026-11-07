package com.jlearn.dao;

import com.jlearn.model.FlashcardDeck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Truy van bo the cong khai va bo the cua hoc vien. */
public class FlashcardDeckDao {
    public List<FlashcardDeck> findAvailable(Connection c, long userId, String search) throws SQLException {
        String sql = "SELECT d.id,d.user_id,d.title,d.description,d.jlpt_level_id,d.visibility,l.code AS level_code,"
                + "COUNT(f.id) AS card_count,"
                + "COALESCE(SUM(CASE WHEN f.id IS NOT NULL AND (s.flashcard_id IS NULL OR s.next_review_at <= NOW()) "
                + "THEN 1 ELSE 0 END),0) AS due_count "
                + "FROM flashcard_decks d LEFT JOIN levels l ON l.id=d.jlpt_level_id "
                + "LEFT JOIN flashcards f ON f.deck_id=d.id "
                + "LEFT JOIN user_flashcard_states s ON s.flashcard_id=f.id AND s.user_id=? "
                + "WHERE (d.visibility='public' OR d.user_id=?) "
                + "AND (?='' OR d.title LIKE ? OR COALESCE(d.description,'') LIKE ?) "
                + "GROUP BY d.id,d.user_id,d.title,d.description,d.jlpt_level_id,d.visibility,l.code "
                + "ORDER BY CASE WHEN d.user_id=? THEN 0 ELSE 1 END,d.title";
        List<FlashcardDeck> decks = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            String term = search == null ? "" : search.trim();
            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setString(3, term);
            ps.setString(4, "%" + term + "%");
            ps.setString(5, "%" + term + "%");
            ps.setLong(6, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) decks.add(readDeck(rs));
            }
        }
        return decks;
    }

    public FlashcardDeck findAvailableById(Connection c, long userId, long deckId) throws SQLException {
        String sql = "SELECT d.id,d.user_id,d.title,d.description,d.jlpt_level_id,d.visibility,l.code AS level_code "
                + "FROM flashcard_decks d LEFT JOIN levels l ON l.id=d.jlpt_level_id "
                + "WHERE d.id=? AND (d.visibility='public' OR d.user_id=?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, deckId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readDeck(rs) : null;
            }
        }
    }

    public FlashcardDeck findOwned(Connection c, long userId, long deckId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id,user_id,title,description,jlpt_level_id,visibility FROM flashcard_decks WHERE id=? AND user_id=?")) {
            ps.setLong(1, deckId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readDeck(rs) : null;
            }
        }
    }

    public long create(Connection c, long userId, String title, String description) throws SQLException {
        String sql = "INSERT INTO flashcard_decks (user_id,title,description,visibility) VALUES (?, ?, ?, 'private')";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, title);
            if (description == null || description.isEmpty()) ps.setNull(3, java.sql.Types.VARCHAR);
            else ps.setString(3, description);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Khong tao duoc bo flashcard");
                return keys.getLong(1);
            }
        }
    }

    public int nextSortOrder(Connection c, long deckId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COALESCE(MAX(sort_order),0)+1 FROM flashcards WHERE deck_id=?")) {
            ps.setLong(1, deckId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static FlashcardDeck readDeck(ResultSet rs) throws SQLException {
        FlashcardDeck d = new FlashcardDeck();
        d.setId(rs.getLong("id"));
        long uid = rs.getLong("user_id");
        d.setUserId(rs.wasNull() ? null : uid);
        d.setTitle(rs.getString("title"));
        d.setDescription(rs.getString("description"));
        long levelId = rs.getLong("jlpt_level_id");
        d.setJlptLevelId(rs.wasNull() ? null : levelId);
        try { d.setLevelCode(rs.getString("level_code")); } catch (SQLException ignored) { }
        d.setVisibility(rs.getString("visibility"));
        try { d.setCardCount(rs.getInt("card_count")); } catch (SQLException ignored) { }
        try { d.setDueCount(rs.getInt("due_count")); } catch (SQLException ignored) { }
        return d;
    }
}
