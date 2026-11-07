package com.jlearn.model;

import java.util.ArrayList;
import java.util.List;

/** Bo the cong khai cua he thong hoac bo rieng cua mot hoc vien. */
public class FlashcardDeck {
    private long id;
    private Long userId;
    private String title;
    private String description;
    private Long jlptLevelId;
    private String levelCode;
    private String visibility;
    private int cardCount;
    private int dueCount;
    private List<Flashcard> cards = new ArrayList<>();
    private List<FlashcardReview> recentReviews = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getJlptLevelId() { return jlptLevelId; }
    public void setJlptLevelId(Long jlptLevelId) { this.jlptLevelId = jlptLevelId; }
    public String getLevelCode() { return levelCode; }
    public void setLevelCode(String levelCode) { this.levelCode = levelCode; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public int getCardCount() { return cardCount; }
    public void setCardCount(int cardCount) { this.cardCount = cardCount; }
    public int getDueCount() { return dueCount; }
    public void setDueCount(int dueCount) { this.dueCount = dueCount; }
    public List<Flashcard> getCards() { return cards; }
    public void setCards(List<Flashcard> cards) { this.cards = cards; }
    public List<FlashcardReview> getRecentReviews() { return recentReviews; }
    public void setRecentReviews(List<FlashcardReview> recentReviews) { this.recentReviews = recentReviews; }
    public boolean isOwnedBy(long uid) { return userId != null && userId.longValue() == uid; }
}
