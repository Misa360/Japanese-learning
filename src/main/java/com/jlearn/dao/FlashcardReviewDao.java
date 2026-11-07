package com.jlearn.dao;

import com.jlearn.model.FlashcardReview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Luu lich su va trang thai lap lai giong cach SRS toi gian. */
public class FlashcardReviewDao {
    public void saveState(Connection c, long userId, long cardId, int repetitions, int intervalDays, double ease)
            throws SQLException {
        String sql = "INSERT INTO user_flashcard_states "
                + "(user_id,flashcard_id,repetition_count,interval_days,ease_factor,next_review_at,last_reviewed_at) "
                + "VALUES (?, ?, ?, ?, ?, DATE_ADD(NOW(), INTERVAL ? DAY), NOW()) "
                + "ON DUPLICATE KEY UPDATE repetition_count=VALUES(repetition_count),interval_days=VALUES(interval_days),"
                + "ease_factor=VALUES(ease_factor),next_review_at=VALUES(next_review_at),last_reviewed_at=NOW()";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, cardId);
            ps.setInt(3, repetitions);
            ps.setInt(4, intervalDays);
            ps.setDouble(5, ease);
            ps.setInt(6, intervalDays);
            ps.executeUpdate();
        }
    }

    public void insert(Connection c, long userId, long cardId, int rating, int intervalDays, double ease)
            throws SQLException {
        String sql = "INSERT INTO flashcard_reviews "
                + "(user_id,flashcard_id,rating,next_review_at,interval_days,ease_factor) "
                + "VALUES (?, ?, ?, DATE_ADD(NOW(), INTERVAL ? DAY), ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, cardId);
            ps.setInt(3, rating);
            ps.setInt(4, intervalDays);
            ps.setInt(5, intervalDays);
            ps.setDouble(6, ease);
            ps.executeUpdate();
        }
    }

    public List<FlashcardReview> recentByDeck(Connection c, long userId, long deckId, int limit) throws SQLException {
        String sql = "SELECT r.id,r.user_id,r.flashcard_id,f.front_content,r.rating,r.reviewed_at,r.next_review_at,"
                + "r.interval_days,r.ease_factor FROM flashcard_reviews r "
                + "JOIN flashcards f ON f.id=r.flashcard_id WHERE r.user_id=? AND f.deck_id=? "
                + "ORDER BY r.reviewed_at DESC,r.id DESC LIMIT ?";
        List<FlashcardReview> reviews = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, deckId);
            ps.setInt(3, Math.max(1, Math.min(limit, 20)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FlashcardReview r = new FlashcardReview();
                    r.setId(rs.getLong("id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setFlashcardId(rs.getLong("flashcard_id"));
                    r.setFrontContent(rs.getString("front_content"));
                    r.setRating(rs.getInt("rating"));
                    Timestamp reviewed = rs.getTimestamp("reviewed_at");
                    if (reviewed != null) r.setReviewedAt(reviewed.toLocalDateTime());
                    Timestamp next = rs.getTimestamp("next_review_at");
                    if (next != null) r.setNextReviewAt(next.toLocalDateTime());
                    r.setIntervalDays(rs.getInt("interval_days"));
                    r.setEaseFactor(rs.getDouble("ease_factor"));
                    reviews.add(r);
                }
            }
        }
        return reviews;
    }
}
