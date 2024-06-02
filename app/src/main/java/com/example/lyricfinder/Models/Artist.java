package com.example.lyricfinder.Models;

import com.google.gson.annotations.SerializedName;

public class Artist {
    @SerializedName("name")
    private String name;

    @SerializedName("image_url")
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
