package com.jlearn.model;

public class Material {
    private long id;
    private long lessonId;
    private String type;
    private String title;
    private String contentText;
    private String mediaUrl;
    private int sortOrder;
    private String youtubeId;
    private String directVideoUrl;
    private String safeUrl;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getLessonId() { return lessonId; }
    public void setLessonId(long lessonId) { this.lessonId = lessonId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getYoutubeId() { return youtubeId; }
    public void setYoutubeId(String youtubeId) { this.youtubeId = youtubeId; }
    public String getDirectVideoUrl() { return directVideoUrl; }
    public void setDirectVideoUrl(String directVideoUrl) { this.directVideoUrl = directVideoUrl; }
    public String getSafeUrl() { return safeUrl; }
    public void setSafeUrl(String safeUrl) { this.safeUrl = safeUrl; }
}
