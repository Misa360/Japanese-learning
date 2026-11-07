package com.jlearn.model;

import java.time.LocalDateTime;

/** Mot lan danh gia nho trong lich su on tap. */
public class FlashcardReview {
    private long id;
    private long userId;
    private long flashcardId;
    private String frontContent;
    private int rating;
    private LocalDateTime reviewedAt;
    private LocalDateTime nextReviewAt;
    private int intervalDays;
    private double easeFactor;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getFlashcardId() { return flashcardId; }
    public void setFlashcardId(long flashcardId) { this.flashcardId = flashcardId; }
    public String getFrontContent() { return frontContent; }
    public void setFrontContent(String frontContent) { this.frontContent = frontContent; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public LocalDateTime getNextReviewAt() { return nextReviewAt; }
    public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }
    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }
    public double getEaseFactor() { return easeFactor; }
    public void setEaseFactor(double easeFactor) { this.easeFactor = easeFactor; }
    public String getRatingLabel() {
        switch (rating) {
            case 1: return "Học lại";
            case 2: return "Khó";
            case 3: return "Tốt";
            case 4: return "Dễ";
            default: return "—";
        }
    }
}
