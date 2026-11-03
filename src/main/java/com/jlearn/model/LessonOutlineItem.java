package com.jlearn.model;

public class LessonOutlineItem {
    private long id;
    private long chapterId;
    private String chapterTitle;
    private int chapterOrder;
    private String title;
    private int durationSeconds;
    private boolean preview;
    private String status;
    private int lastPositionSeconds;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getChapterId() { return chapterId; }
    public void setChapterId(long chapterId) { this.chapterId = chapterId; }
    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
    public int getChapterOrder() { return chapterOrder; }
    public void setChapterOrder(int chapterOrder) { this.chapterOrder = chapterOrder; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public boolean isPreview() { return preview; }
    public void setPreview(boolean preview) { this.preview = preview; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getLastPositionSeconds() { return lastPositionSeconds; }
    public void setLastPositionSeconds(int lastPositionSeconds) { this.lastPositionSeconds = lastPositionSeconds; }

    public boolean isCompleted() { return CourseProgress.LESSON_COMPLETED.equals(status); }
}
