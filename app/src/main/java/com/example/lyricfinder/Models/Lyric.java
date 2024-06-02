package com.example.lyricfinder.Models;

import com.google.gson.annotations.SerializedName;

public class Lyric {
    @SerializedName("title")
    private String title;

    @SerializedName("primary_artist")
    private String primaryArtist;

    @SerializedName("html")
    private String html;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrimaryArtist() {
        return primaryArtist;
    }

    public void setPrimaryArtist(String primaryArtist) {
        this.primaryArtist = primaryArtist;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
