package com.jlearn.model;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private long id;
    private long courseId;
    private String title;
    private String description;
    private int sortOrder;
    private List<LessonOutlineItem> lessons = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public List<LessonOutlineItem> getLessons() { return lessons; }
    public void setLessons(List<LessonOutlineItem> lessons) { this.lessons = lessons; }
}
