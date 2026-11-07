package com.jlearn.model;

public class Course {
    private long id;
    private long levelId;
    private String levelCode;
    private String title;
    private String slug;
    private String description;
    private String thumbnailPath;
    private String status;
    private boolean free;
    private Long requiredCourseId;
    private long unlockCoins;
    private String requiredCourseTitle;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getLevelId() { return levelId; }
    public void setLevelId(long levelId) { this.levelId = levelId; }
    public String getLevelCode() { return levelCode; }
    public void setLevelCode(String levelCode) { this.levelCode = levelCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isFree() { return free; }
    public void setFree(boolean free) { this.free = free; }
    public Long getRequiredCourseId() { return requiredCourseId; }
    public void setRequiredCourseId(Long requiredCourseId) { this.requiredCourseId = requiredCourseId; }
    public long getUnlockCoins() { return unlockCoins; }
    public void setUnlockCoins(long unlockCoins) { this.unlockCoins = unlockCoins; }
    public String getRequiredCourseTitle() { return requiredCourseTitle; }
    public void setRequiredCourseTitle(String requiredCourseTitle) { this.requiredCourseTitle = requiredCourseTitle; }

    public boolean isPublished() { return "published".equals(status); }

    /** So xu phai tra khi dang ky: khoa mien phi thi 0, nguoc lai la unlock_coins. */
    public long getCost() { return free ? 0L : unlockCoins; }
}
