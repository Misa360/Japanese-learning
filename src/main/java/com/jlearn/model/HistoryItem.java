package com.jlearn.model;

public class HistoryItem {
    private long lessonId;
    private String lessonTitle;
    private long courseId;
    private String courseTitle;
    private java.util.Date loggedAt;
    private int durationSeconds;

    public long getLessonId() { return lessonId; }
    public void setLessonId(long lessonId) { this.lessonId = lessonId; }
    public String getLessonTitle() { return lessonTitle; }
    public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }
    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public java.util.Date getLoggedAt() { return loggedAt; }
    public void setLoggedAt(java.util.Date loggedAt) { this.loggedAt = loggedAt; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
}
