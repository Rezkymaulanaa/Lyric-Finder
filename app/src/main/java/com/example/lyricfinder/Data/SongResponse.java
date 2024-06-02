// SongResponse.java
package com.example.lyricfinder.Data;

import com.example.lyricfinder.ChartItem;

import java.util.List;

public class SongResponse {
    private List<ChartItem> chart_items;

    public List<ChartItem> getChartItems() {
        return chart_items;
    }

    public void setChartItems(List<ChartItem> chart_items) {
        this.chart_items = chart_items;
    }
}
