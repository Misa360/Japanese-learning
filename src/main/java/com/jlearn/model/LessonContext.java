package com.jlearn.model;

public class LessonContext {
    private long lessonId;
    private String title;
    private int durationSeconds;
    private boolean preview;
    private String lessonStatus;
    private long chapterId;
    private String chapterTitle;
    private long courseId;
    private String courseTitle;
    private String courseStatus;

    public long getLessonId() { return lessonId; }
    public void setLessonId(long lessonId) { this.lessonId = lessonId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public boolean isPreview() { return preview; }
    public void setPreview(boolean preview) { this.preview = preview; }
    public String getLessonStatus() { return lessonStatus; }
    public void setLessonStatus(String lessonStatus) { this.lessonStatus = lessonStatus; }
    public long getChapterId() { return chapterId; }
    public void setChapterId(long chapterId) { this.chapterId = chapterId; }
    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public String getCourseStatus() { return courseStatus; }
    public void setCourseStatus(String courseStatus) { this.courseStatus = courseStatus; }

    public boolean isPublished() { return "published".equals(lessonStatus) && "published".equals(courseStatus); }
}
