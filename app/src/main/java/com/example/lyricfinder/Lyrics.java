package com.example.lyricfinder;

import com.example.lyricfinder.Models.Lyric;
import com.google.gson.annotations.SerializedName;

public class Lyrics {
    @SerializedName("lyrics")
    private LyricLyric lyricLyric;

    @SerializedName("tracking_data")
    private Lyric trackingData;

    public LyricLyric getLyricLyric() {
        return lyricLyric;
    }

    public void setLyricLyric(LyricLyric lyricLyric) {
        this.lyricLyric = lyricLyric;
    }

    public Lyric getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(Lyric trackingData) {
        this.trackingData = trackingData;
    }
}
