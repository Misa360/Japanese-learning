package com.jlearn.model;

public class GrammarPoint {
    private long id;
    private String title;
    private String pattern;
    private String explanationVi;
    private String exampleJa;
    private String exampleVi;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getExplanationVi() { return explanationVi; }
    public void setExplanationVi(String explanationVi) { this.explanationVi = explanationVi; }
    public String getExampleJa() { return exampleJa; }
    public void setExampleJa(String exampleJa) { this.exampleJa = exampleJa; }
    public String getExampleVi() { return exampleVi; }
    public void setExampleVi(String exampleVi) { this.exampleVi = exampleVi; }
}
