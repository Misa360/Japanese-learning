package com.jlearn.model;

public class MyCourseItem {
    private long courseId;
    private String title;
    private String levelCode;
    private String thumbnailPath;
    private CourseProgress progress;
    private java.util.Date enrolledAt;

    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLevelCode() { return levelCode; }
    public void setLevelCode(String levelCode) { this.levelCode = levelCode; }
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
    public CourseProgress getProgress() { return progress; }
    public void setProgress(CourseProgress progress) { this.progress = progress; }
    public java.util.Date getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(java.util.Date enrolledAt) { this.enrolledAt = enrolledAt; }
}
