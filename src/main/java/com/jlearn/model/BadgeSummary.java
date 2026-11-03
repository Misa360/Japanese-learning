package com.jlearn.model;

public class BadgeSummary {
    private long id;
    private String name;
    private String description;
    private String iconPath;
    private java.util.Date earnedAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }
    public java.util.Date getEarnedAt() { return earnedAt; }
    public void setEarnedAt(java.util.Date earnedAt) { this.earnedAt = earnedAt; }
}
