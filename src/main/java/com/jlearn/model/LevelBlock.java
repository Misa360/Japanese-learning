package com.jlearn.model;

import java.util.ArrayList;
import java.util.List;

public class LevelBlock {
    private Level level;
    private List<CourseCard> courses = new ArrayList<>();

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
    public List<CourseCard> getCourses() { return courses; }
    public void setCourses(List<CourseCard> courses) { this.courses = courses; }
}
