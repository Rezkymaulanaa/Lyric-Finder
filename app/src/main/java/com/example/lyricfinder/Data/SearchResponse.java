package com.example.lyricfinder.Data;

import com.example.lyricfinder.SearchResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    private List<SearchResult> hits;

    public List<SearchResult> getHits() {
        return hits;
    }

    public void setHits(List<SearchResult> hits) {
        this.hits = hits;
    }
}
