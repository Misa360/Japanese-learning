package com.jlearn.model;

/** Mot the trong bo flashcard kem trang thai on tap cua rieng nguoi dang hoc. */
public class Flashcard {
    private long id;
    private long deckId;
    private String frontContent;
    private String backContent;
    private String exampleText;
    private String audioUrl;
    private int sortOrder;
    private int repetitionCount;
    private int intervalDays;
    private double easeFactor = 2.5;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getDeckId() { return deckId; }
    public void setDeckId(long deckId) { this.deckId = deckId; }
    public String getFrontContent() { return frontContent; }
    public void setFrontContent(String frontContent) { this.frontContent = frontContent; }
    public String getBackContent() { return backContent; }
    public void setBackContent(String backContent) { this.backContent = backContent; }
    public String getExampleText() { return exampleText; }
    public void setExampleText(String exampleText) { this.exampleText = exampleText; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public int getRepetitionCount() { return repetitionCount; }
    public void setRepetitionCount(int repetitionCount) { this.repetitionCount = repetitionCount; }
    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }
    public double getEaseFactor() { return easeFactor; }
    public void setEaseFactor(double easeFactor) { this.easeFactor = easeFactor; }
}
