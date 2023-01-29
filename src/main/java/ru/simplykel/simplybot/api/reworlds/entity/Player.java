package ru.simplykel.simplybot.api.reworlds.entity;

import org.json.JSONObject;

public class Player {
    private String username;
    private String worldType;
    private boolean online;
    private long playTime;
    private long lastPlaying;
    public Player(JSONObject json){
        username = json.getString("name");
        online = json.getJSONObject("status").getBoolean("online");
        playTime = json.getLong("play_time");
        lastPlaying = json.getLong("last_seen");
        if(online) worldType = json.getString("server");
    }

    public String getUsername() {
        return username;
    }

    public boolean isOnline() {
        return online;
    }

    public long getPlayTime() {
        return playTime;
    }

    public long getLastPlaying() {
        return lastPlaying;
    }

    public String getWorldType() {
        return worldType;
    }
}
