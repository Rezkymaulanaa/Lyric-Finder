package com.example.lyricfinder.Data;

import com.example.lyricfinder.Lyrics;
import com.google.gson.annotations.SerializedName;

public class LyricResponse {
    @SerializedName("lyrics")
    private Lyrics lyrics;

    public Lyrics getLyrics() {
        return lyrics;
    }

    public void setLyrics(Lyrics lyrics) {
        this.lyrics = lyrics;
    }
}
