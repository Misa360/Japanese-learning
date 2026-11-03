package com.jlearn.model;

public class CourseProgress {
    private int totalLessons;
    private int completedLessons;
    private int percent;
    private String status;

    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
    public int getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }
    public int getPercent() { return percent; }
    public void setPercent(int percent) { this.percent = percent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static final String COURSE_NOT_STARTED = "not_started";
    public static final String COURSE_IN_PROGRESS = "in_progress";
    public static final String COURSE_COMPLETED = "completed";
    public static final String LESSON_NOT_STARTED = "not_started";
    public static final String LESSON_IN_PROGRESS = "in_progress";
    public static final String LESSON_COMPLETED = "completed";

    public static CourseProgress of(int total, int done) {
        CourseProgress p = new CourseProgress();
        p.totalLessons = total;
        p.completedLessons = done;
        p.percent = percent(total, done);
        p.status = courseStatus(total, done);
        return p;
    }

    /** F18: % = bai hoan thanh / tong bai dang mo * 100, lam tron xuong. */
    public static int percent(int total, int done) {
        if (total <= 0 || done <= 0) return 0;
        if (done >= total) return 100;
        return (int) Math.floor(done * 100.0 / total);
    }

    /** F18: chua hoc = 0 bai; hoan thanh = 100% (co bai); con lai dang hoc. */
    public static String courseStatus(int total, int done) {
        if (total > 0 && done >= total) return COURSE_COMPLETED;
        return done > 0 ? COURSE_IN_PROGRESS : COURSE_NOT_STARTED;
    }

    /** F18: bai hoan thanh / dang hoc (last_position > 0) / chua hoc. */
    public static String lessonStatus(String dbStatus, int lastPositionSeconds) {
        if (LESSON_COMPLETED.equals(dbStatus)) return LESSON_COMPLETED;
        return lastPositionSeconds > 0 ? LESSON_IN_PROGRESS : LESSON_NOT_STARTED;
    }

    public boolean isCompleted() { return COURSE_COMPLETED.equals(status); }
}
