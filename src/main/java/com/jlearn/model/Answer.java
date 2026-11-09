package com.jlearn.model;

public class Answer {
    private long id; private long questionId; private String content; private boolean correct; private int sortOrder;
    public long getId(){return id;} public void setId(long v){id=v;}
    public long getQuestionId(){return questionId;} public void setQuestionId(long v){questionId=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public boolean isCorrect(){return correct;} public void setCorrect(boolean v){correct=v;}
    public int getSortOrder(){return sortOrder;} public void setSortOrder(int v){sortOrder=v;}
}
