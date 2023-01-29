package ru.simplykel.simplybot.api.youtube.entity;

import org.json.JSONObject;

public class Video {
    private String title;
    private String description;
    private String thumbnails;
    private Boolean isLive;
    private String id;
    private String URL;
    private Channel owner;
    private String likeCount;
    private String viewCount;

    public Video(JSONObject video) {
        title = video.getString("title");
        description = video.getString("description");
        thumbnails = video.getJSONObject("thumbnails").getString("url");
        isLive = video.getString("liveBroadcastContent").equals("live");
        owner = new Channel(video.getJSONObject("channel"));
        id = video.getString("id");
        URL = "https://youtube.com/watch?v=" + id;
        likeCount = video.getJSONObject("statistics").getString("likeCount");
        viewCount = video.getJSONObject("statistics").getString("viewCount");
    }

    public Boolean getLive() {
        return isLive;
    }
    public String getDescription() {
        return description;
    }
    public String getThumbnails() {
        return thumbnails;
    }
    public String getTitle() {
        return title;
    }
    public Channel getOwner() {
        return owner;
    }
    public String getId() {
        return id;
    }
    public String getURL() {
        return URL;
    }
    public String getLikeCount() { return likeCount; }
    public String getViewCount() { return viewCount; }
}
