package com.jlearn.model;

public class Quiz {
    private long id; private Long lessonId; private Long jlptLevelId; private String levelCode; private String title; private String description; private String quizType; private String status; private java.sql.Date examPeriod; private Integer timeLimitSeconds; private java.math.BigDecimal passingScore; private Integer maxAttempts; private int sortOrder; private String showAnswerMode; private boolean shuffleQuestions; private int questionCount; private int sectionCount; private long attemptCount;
    public long getId(){return id;} public void setId(long v){id=v;}
    public Long getLessonId(){return lessonId;} public void setLessonId(Long v){lessonId=v;}
    public Long getJlptLevelId(){return jlptLevelId;} public void setJlptLevelId(Long v){jlptLevelId=v;}
    public String getLevelCode(){return levelCode;} public void setLevelCode(String v){levelCode=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getQuizType(){return quizType;} public void setQuizType(String v){quizType=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public java.sql.Date getExamPeriod(){return examPeriod;} public void setExamPeriod(java.sql.Date v){examPeriod=v;}
    public Integer getTimeLimitSeconds(){return timeLimitSeconds;} public void setTimeLimitSeconds(Integer v){timeLimitSeconds=v;}
    public java.math.BigDecimal getPassingScore(){return passingScore;} public void setPassingScore(java.math.BigDecimal v){passingScore=v;}
    public Integer getMaxAttempts(){return maxAttempts;} public void setMaxAttempts(Integer v){maxAttempts=v;}
    public int getSortOrder(){return sortOrder;} public void setSortOrder(int v){sortOrder=v;}
    public String getShowAnswerMode(){return showAnswerMode;} public void setShowAnswerMode(String v){showAnswerMode=v;}
    public boolean isShuffleQuestions(){return shuffleQuestions;} public void setShuffleQuestions(boolean v){shuffleQuestions=v;}
    public int getQuestionCount(){return questionCount;} public void setQuestionCount(int v){questionCount=v;}
    public int getSectionCount(){return sectionCount;} public void setSectionCount(int v){sectionCount=v;}
    public long getAttemptCount(){return attemptCount;} public void setAttemptCount(long v){attemptCount=v;}
}
