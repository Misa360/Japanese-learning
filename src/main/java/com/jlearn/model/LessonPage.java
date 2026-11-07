package com.jlearn.model;

import java.util.ArrayList;
import java.util.List;

public class LessonPage {
    private LessonContext context;
    private boolean enrolled;
    private String lessonProgressStatus;
    private int resumeSeconds;
    private List<Material> videos = new ArrayList<>();
    private List<Material> transcripts = new ArrayList<>();
    private List<Material> texts = new ArrayList<>();
    private List<Material> documents = new ArrayList<>();
    private List<Vocabulary> vocabulary = new ArrayList<>();
    private List<GrammarPoint> grammar = new ArrayList<>();
    private List<Kanji> kanji = new ArrayList<>();
    private List<LessonOutlineItem> outline = new ArrayList<>();
    private CourseProgress courseProgress;
    private Long prevLessonId;
    private Long nextLessonId;

    public LessonContext getContext() { return context; }
    public void setContext(LessonContext context) { this.context = context; }
    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }
    public String getLessonProgressStatus() { return lessonProgressStatus; }
    public void setLessonProgressStatus(String lessonProgressStatus) { this.lessonProgressStatus = lessonProgressStatus; }
    public int getResumeSeconds() { return resumeSeconds; }
    public void setResumeSeconds(int resumeSeconds) { this.resumeSeconds = resumeSeconds; }
    public List<Material> getVideos() { return videos; }
    public void setVideos(List<Material> videos) { this.videos = videos; }
    public List<Material> getTranscripts() { return transcripts; }
    public void setTranscripts(List<Material> transcripts) { this.transcripts = transcripts; }
    public List<Material> getTexts() { return texts; }
    public void setTexts(List<Material> texts) { this.texts = texts; }
    public List<Material> getDocuments() { return documents; }
    public void setDocuments(List<Material> documents) { this.documents = documents; }
    public List<Vocabulary> getVocabulary() { return vocabulary; }
    public void setVocabulary(List<Vocabulary> vocabulary) { this.vocabulary = vocabulary; }
    public List<GrammarPoint> getGrammar() { return grammar; }
    public void setGrammar(List<GrammarPoint> grammar) { this.grammar = grammar; }
    public List<Kanji> getKanji() { return kanji; }
    public void setKanji(List<Kanji> kanji) { this.kanji = kanji; }
    public List<LessonOutlineItem> getOutline() { return outline; }
    public void setOutline(List<LessonOutlineItem> outline) { this.outline = outline; }
    public CourseProgress getCourseProgress() { return courseProgress; }
    public void setCourseProgress(CourseProgress courseProgress) { this.courseProgress = courseProgress; }
    public Long getPrevLessonId() { return prevLessonId; }
    public void setPrevLessonId(Long prevLessonId) { this.prevLessonId = prevLessonId; }
    public Long getNextLessonId() { return nextLessonId; }
    public void setNextLessonId(Long nextLessonId) { this.nextLessonId = nextLessonId; }

    public boolean isCompleted() { return CourseProgress.LESSON_COMPLETED.equals(lessonProgressStatus); }
}
