package com.jlearn.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private long id; private long quizId; private Long sectionId; private String sectionTitle; private String questionType; private String content; private String explanation; private String audioUrl; private String imageUrl; private BigDecimal points=BigDecimal.ONE; private int sortOrder; private List<Answer> answers=new ArrayList<>();
    public long getId(){return id;} public void setId(long v){id=v;}
    public long getQuizId(){return quizId;} public void setQuizId(long v){quizId=v;}
    public Long getSectionId(){return sectionId;} public void setSectionId(Long v){sectionId=v;}
    public String getSectionTitle(){return sectionTitle;} public void setSectionTitle(String v){sectionTitle=v;}
    public String getQuestionType(){return questionType;} public void setQuestionType(String v){questionType=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public String getExplanation(){return explanation;} public void setExplanation(String v){explanation=v;}
    public String getAudioUrl(){return audioUrl;} public void setAudioUrl(String v){audioUrl=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
    public BigDecimal getPoints(){return points;} public void setPoints(BigDecimal v){points=v;}
    public int getSortOrder(){return sortOrder;} public void setSortOrder(int v){sortOrder=v;}
    public List<Answer> getAnswers(){return answers;} public void setAnswers(List<Answer> v){answers=v;}
}
