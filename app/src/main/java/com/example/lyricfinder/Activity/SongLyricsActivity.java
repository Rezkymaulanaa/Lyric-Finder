package com.example.lyricfinder.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lyricfinder.API.ApiConfig;
import com.example.lyricfinder.API.ApiService;
import com.example.lyricfinder.Data.LyricResponse;
import com.example.lyricfinder.LyricLyric;
import com.example.lyricfinder.Lyrics;
import com.example.lyricfinder.Models.Lyric;
import com.example.lyricfinder.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongLyricsActivity extends AppCompatActivity {

    private TextView lyricsTextView;
    private TextView titleTextView;
    private TextView artistTextView;
    private String songId;
    private ImageView btnBack;
    private LottieAnimationView progressBar;
    private ExecutorService executorService;
    private Handler mainHandler;
    private View overlayView;
    private LinearLayout errorView;
    private TextView errorTextView;
    private Button retryButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_lyrics);

        lyricsTextView = findViewById(R.id.lyricsTextView);
        titleTextView = findViewById(R.id.titleTextView);
        artistTextView = findViewById(R.id.artistTextView);
        btnBack = findViewById(R.id.btnBack);
        errorView = findViewById(R.id.errorView);
        errorTextView = findViewById(R.id.errorTextView);
        retryButton = findViewById(R.id.retryButton);
        progressBar = findViewById(R.id.progressBar);
        overlayView = findViewById(R.id.overlayView);
        songId = getIntent().getStringExtra("SONG_ID");

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        fetchSongLyrics(songId);

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void fetchSongLyrics(String id) {
        ApiService apiService = ApiConfig.getApiService();
        Call<LyricResponse> call = apiService.getLyrics(id);

        // Show the progress bar when starting to fetch data
        mainHandler.post(this::showProgressBar);
        hideError();

        call.enqueue(new Callback<LyricResponse>() {
            @Override
            public void onResponse(Call<LyricResponse> call, Response<LyricResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        LyricResponse lyricResponse = response.body();
                        Log.d("LyricResponse", "Received response: " + lyricResponse.toString());
                        Lyrics lyrics = lyricResponse.getLyrics();
                        if (lyrics != null) {
                            Lyric trackingData = lyrics.getTrackingData();
                            LyricLyric lyricLyric = lyrics.getLyricLyric();
                            if (trackingData != null && lyricLyric != null) {
                                Lyric body = lyricLyric.getBody();
                                if (body != null) {
                                    String title = trackingData.getTitle();
                                    String primaryArtist = trackingData.getPrimaryArtist();
                                    String lyricsHtml = body.getHtml();

                                    // Menghapus tag <a> dan konversi HTML menjadi teks yang dapat ditampilkan di TextView
                                    Document document = Jsoup.parse(lyricsHtml);
                                    for (Element element : document.select("a")) {
                                        element.unwrap();
                                    }
                                    String cleanHtml = document.html();
                                    CharSequence formattedLyrics = Html.fromHtml(cleanHtml, Html.FROM_HTML_MODE_LEGACY);

                                    // Update UI on the main thread
                                    mainHandler.post(() -> {
                                        titleTextView.setText(title);
                                        artistTextView.setText(primaryArtist);
                                        lyricsTextView.setText(formattedLyrics);
                                        hideProgressBar(); // Hide progress bar
                                        hideError(); //Hide Error
                                    });
                                } else {
                                    mainHandler.post(() -> {
                                        lyricsTextView.setText("Error: No lyric body available");
                                        hideProgressBar(); // Hide progress bar
                                    });
                                }
                            } else {
                                mainHandler.post(() -> {
                                    lyricsTextView.setText("Error: No tracking data or lyric available");
                                    hideProgressBar(); // Hide progress bar
                                });
                            }
                        } else {
                            mainHandler.post(() -> {
                                lyricsTextView.setText("Error: No lyrics data available");
                                hideProgressBar(); // Hide progress bar
                            });
                        }
                    });
                } else {
                    mainHandler.post(() -> {
                        lyricsTextView.setText("Error: " + response.message());
                        hideProgressBar(); // Hide progress bar
                    });
                }
            }

            @Override
            public void onFailure(Call<LyricResponse> call, Throwable t) {
                mainHandler.post(() -> {
                    showError(t.getMessage());
                    hideProgressBar(); // Hide progress bar
                });
            }
        });
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }
    private void hideError() {
        errorView.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        overlayView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.playAnimation();
    }

    private void hideProgressBar() {
        overlayView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressBar.cancelAnimation();
    }
}
