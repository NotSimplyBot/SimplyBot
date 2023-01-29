package ru.simplykel.simplybot.api.youtube.entity;

import org.json.JSONObject;

public class Channel {
    private String title;
    private String description;
    private String customURL;
    private String thumbnails;
    private String id;
    private String defaultLanguage;
    private String URL;

    public Channel(JSONObject channel) {
        title = channel.getString("title");
        description = channel.getString("description");
        thumbnails = channel.getJSONObject("thumbnails").getString("url");
        if (!channel.isNull("customURL")) customURL = channel.getString("customURL");
        id = channel.getString("id");
        if (customURL == null) {
            URL = "https://www.youtube.com/c/" + id;
        } else if (customURL.startsWith("@")) {
            URL = "https://www.youtube.com/" + customURL;
        } else {
            URL = "https://www.youtube.com/c/" + customURL;
        }
        if(!channel.isNull(defaultLanguage)) defaultLanguage = channel.getString("defaultLanguage");
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

    public String getCustomURL() {
        return customURL;
    }

    public String getURL() {
        return URL;
    }

    public String getId() {
        return id;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }
}
