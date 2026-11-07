package com.jlearn.model;

import java.util.ArrayList;
import java.util.List;

public class CourseDetail {
    private Course course;
    private Level level;
    private List<Chapter> chapters = new ArrayList<>();
    private List<MaterialSummary> materials = new ArrayList<>();
    private boolean loggedIn;
    private boolean enrolled;
    private CourseProgress progress;
    private EligibilityResult eligibility;
    private int lessonCount;

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
    public List<Chapter> getChapters() { return chapters; }
    public void setChapters(List<Chapter> chapters) { this.chapters = chapters; }
    public List<MaterialSummary> getMaterials() { return materials; }
    public void setMaterials(List<MaterialSummary> materials) { this.materials = materials; }
    public boolean isLoggedIn() { return loggedIn; }
    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }
    public CourseProgress getProgress() { return progress; }
    public void setProgress(CourseProgress progress) { this.progress = progress; }
    public EligibilityResult getEligibility() { return eligibility; }
    public void setEligibility(EligibilityResult eligibility) { this.eligibility = eligibility; }
    public int getLessonCount() { return lessonCount; }
    public void setLessonCount(int lessonCount) { this.lessonCount = lessonCount; }
}
