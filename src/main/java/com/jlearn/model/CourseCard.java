package com.jlearn.model;

public class CourseCard {
    private Course course;
    private boolean enrolled;
    private CourseProgress progress;
    private EligibilityResult eligibility;

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }
    public CourseProgress getProgress() { return progress; }
    public void setProgress(CourseProgress progress) { this.progress = progress; }
    public EligibilityResult getEligibility() { return eligibility; }
    public void setEligibility(EligibilityResult eligibility) { this.eligibility = eligibility; }
}
