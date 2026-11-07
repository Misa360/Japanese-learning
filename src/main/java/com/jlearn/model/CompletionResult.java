package com.jlearn.model;

public class CompletionResult {
    private boolean alreadyCompleted;
    private long coinsAwarded;
    private CourseProgress progress;

    public boolean isAlreadyCompleted() { return alreadyCompleted; }
    public void setAlreadyCompleted(boolean alreadyCompleted) { this.alreadyCompleted = alreadyCompleted; }
    public long getCoinsAwarded() { return coinsAwarded; }
    public void setCoinsAwarded(long coinsAwarded) { this.coinsAwarded = coinsAwarded; }
    public CourseProgress getProgress() { return progress; }
    public void setProgress(CourseProgress progress) { this.progress = progress; }
}
