// ApiService.java
package com.example.lyricfinder.API;

import com.example.lyricfinder.Data.ArtistResponse;
import com.example.lyricfinder.Data.LyricResponse;
import com.example.lyricfinder.Data.SearchResponse;
import com.example.lyricfinder.Data.SongResponse;
import com.example.lyricfinder.Models.Lyric;
import com.example.lyricfinder.Models.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("chart/songs/")
    Call<SongResponse> getChartSongs(
            @Query("per_page") int perPage,
            @Query("page") int page
    );

    @GET("search")
    Call<SearchResponse> searchSongs(@Query("q") String query);

    @GET("chart/artists/")
    Call<ArtistResponse> getChartArtists(
            @Query("per_page") int per_page,
            @Query("page") int page
    );

    @GET("song/lyrics/")
    Call<LyricResponse> getLyrics(@Query("id") String id);
}
