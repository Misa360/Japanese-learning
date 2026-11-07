package com.jlearn.dao;

import com.jlearn.model.Flashcard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Truy van va tao the; danh sach hoc chi tra the chua on hoac da den han. */
public class FlashcardDao {
    public List<Flashcard> findDueByDeck(Connection c, long userId, long deckId) throws SQLException {
        String sql = "SELECT f.id,f.deck_id,f.front_content,f.back_content,f.example_text,f.audio_url,f.sort_order,"
                + "COALESCE(s.repetition_count,0) AS repetition_count,COALESCE(s.interval_days,0) AS interval_days,"
                + "COALESCE(s.ease_factor,2.50) AS ease_factor "
                + "FROM flashcards f LEFT JOIN user_flashcard_states s ON s.flashcard_id=f.id AND s.user_id=? "
                + "WHERE f.deck_id=? AND (s.flashcard_id IS NULL OR s.next_review_at<=NOW()) "
                + "ORDER BY CASE WHEN s.flashcard_id IS NULL THEN 0 ELSE 1 END,f.sort_order,f.id";
        List<Flashcard> cards = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, deckId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cards.add(read(rs));
            }
        }
        return cards;
    }

    public int countByDeck(Connection c, long deckId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM flashcards WHERE deck_id=?")) {
            ps.setLong(1, deckId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    public Flashcard findById(Connection c, long userId, long cardId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT f.id,f.deck_id,f.front_content,f.back_content,f.example_text,f.audio_url,f.sort_order,"
              + "COALESCE(s.repetition_count,0) AS repetition_count,COALESCE(s.interval_days,0) AS interval_days,"
              + "COALESCE(s.ease_factor,2.50) AS ease_factor FROM flashcards f "
              + "LEFT JOIN user_flashcard_states s ON s.flashcard_id=f.id AND s.user_id=? WHERE f.id=? FOR UPDATE")) {
            ps.setLong(1, userId);
            ps.setLong(2, cardId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? read(rs) : null; }
        }
    }

    public boolean isDue(Connection c, long userId, long cardId) throws SQLException {
        String sql = "SELECT 1 FROM flashcards f LEFT JOIN user_flashcard_states s "
                + "ON s.flashcard_id=f.id AND s.user_id=? "
                + "WHERE f.id=? AND (s.flashcard_id IS NULL OR s.next_review_at<=NOW()) FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, cardId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public long create(Connection c, long deckId, String front, String back, String example, int sortOrder) throws SQLException {
        String sql = "INSERT INTO flashcards (deck_id,front_content,back_content,example_text,sort_order) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, deckId);
            ps.setString(2, front);
            ps.setString(3, back);
            if (example == null || example.isEmpty()) ps.setNull(4, java.sql.Types.VARCHAR);
            else ps.setString(4, example);
            ps.setInt(5, sortOrder);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Khong tao duoc flashcard");
                return keys.getLong(1);
            }
        }
    }

    private static Flashcard read(ResultSet rs) throws SQLException {
        Flashcard f = new Flashcard();
        f.setId(rs.getLong("id"));
        f.setDeckId(rs.getLong("deck_id"));
        f.setFrontContent(rs.getString("front_content"));
        f.setBackContent(rs.getString("back_content"));
        f.setExampleText(rs.getString("example_text"));
        f.setAudioUrl(rs.getString("audio_url"));
        f.setSortOrder(rs.getInt("sort_order"));
        try { f.setRepetitionCount(rs.getInt("repetition_count")); } catch (SQLException ignored) { }
        try { f.setIntervalDays(rs.getInt("interval_days")); } catch (SQLException ignored) { }
        try { f.setEaseFactor(rs.getDouble("ease_factor")); } catch (SQLException ignored) { }
        return f;
    }
}
