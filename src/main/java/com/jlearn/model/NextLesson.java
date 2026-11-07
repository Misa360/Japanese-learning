package com.jlearn.model;

public class NextLesson {
    private String kind;
    private Long lessonId;

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public static final String NOT_ENROLLED = "NOT_ENROLLED";
    public static final String NO_LESSONS = "NO_LESSONS";
    public static final String CONTINUE = "CONTINUE";
    public static final String ALL_DONE = "ALL_DONE";

    public static NextLesson of(String kind, Long lessonId) {
        NextLesson n = new NextLesson();
        n.kind = kind; n.lessonId = lessonId;
        return n;
    }
}
