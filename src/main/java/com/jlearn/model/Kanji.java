package com.jlearn.model;

public class Kanji {
    private long id;
    private String character;
    private String onyomi;
    private String kunyomi;
    private String meaningVi;
    private Integer strokeCount;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCharacter() { return character; }
    public void setCharacter(String character) { this.character = character; }
    public String getOnyomi() { return onyomi; }
    public void setOnyomi(String onyomi) { this.onyomi = onyomi; }
    public String getKunyomi() { return kunyomi; }
    public void setKunyomi(String kunyomi) { this.kunyomi = kunyomi; }
    public String getMeaningVi() { return meaningVi; }
    public void setMeaningVi(String meaningVi) { this.meaningVi = meaningVi; }
    public Integer getStrokeCount() { return strokeCount; }
    public void setStrokeCount(Integer strokeCount) { this.strokeCount = strokeCount; }
}
