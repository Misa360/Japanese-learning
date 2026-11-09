package com.jlearn.model;

public class QuizSection {
    private long id; private long quizId; private String title; private String sectionType; private Integer timeLimitSeconds; private java.math.BigDecimal maxScore; private java.math.BigDecimal passingScore; private int sortOrder; private int questionCount;
    public long getId(){return id;} public void setId(long v){id=v;}
    public long getQuizId(){return quizId;} public void setQuizId(long v){quizId=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getSectionType(){return sectionType;} public void setSectionType(String v){sectionType=v;}
    public Integer getTimeLimitSeconds(){return timeLimitSeconds;} public void setTimeLimitSeconds(Integer v){timeLimitSeconds=v;}
    public java.math.BigDecimal getMaxScore(){return maxScore;} public void setMaxScore(java.math.BigDecimal v){maxScore=v;}
    public java.math.BigDecimal getPassingScore(){return passingScore;} public void setPassingScore(java.math.BigDecimal v){passingScore=v;}
    public int getSortOrder(){return sortOrder;} public void setSortOrder(int v){sortOrder=v;}
    public int getQuestionCount(){return questionCount;} public void setQuestionCount(int v){questionCount=v;}
}
