package com.example.lyricfinder;

import com.example.lyricfinder.Models.Lyric;
import com.google.gson.annotations.SerializedName;

public class LyricLyric {
    @SerializedName("body")
    private Lyric body;

    public Lyric getBody() {
        return body;
    }

    public void setBody(Lyric body) {
        this.body = body;
    }
}
