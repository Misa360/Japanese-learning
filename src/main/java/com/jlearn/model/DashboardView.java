package com.jlearn.model;

public class DashboardView {
    private User user;
    private long coinBalance;
    private int streakDays;
    private boolean checkedInToday;
    private int inProgressCourses;
    private int completedCourses;
    private int completedLessons;
    private BadgeSummary latestBadge;
    private Long continueCourseId;
    private String continueCourseTitle;
    private String motivation;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public long getCoinBalance() { return coinBalance; }
    public void setCoinBalance(long coinBalance) { this.coinBalance = coinBalance; }
    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }
    public boolean isCheckedInToday() { return checkedInToday; }
    public void setCheckedInToday(boolean checkedInToday) { this.checkedInToday = checkedInToday; }
    public int getInProgressCourses() { return inProgressCourses; }
    public void setInProgressCourses(int inProgressCourses) { this.inProgressCourses = inProgressCourses; }
    public int getCompletedCourses() { return completedCourses; }
    public void setCompletedCourses(int completedCourses) { this.completedCourses = completedCourses; }
    public int getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }
    public BadgeSummary getLatestBadge() { return latestBadge; }
    public void setLatestBadge(BadgeSummary latestBadge) { this.latestBadge = latestBadge; }
    public Long getContinueCourseId() { return continueCourseId; }
    public void setContinueCourseId(Long continueCourseId) { this.continueCourseId = continueCourseId; }
    public String getContinueCourseTitle() { return continueCourseTitle; }
    public void setContinueCourseTitle(String continueCourseTitle) { this.continueCourseTitle = continueCourseTitle; }
    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
}
