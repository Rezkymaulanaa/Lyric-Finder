package com.example.lyricfinder.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lyricfinder.API.ApiConfig;
import com.example.lyricfinder.API.ApiService;
import com.example.lyricfinder.Adapter.SongAdapter;
import com.example.lyricfinder.ChartItem;
import com.example.lyricfinder.Data.SongResponse;
import com.example.lyricfinder.Models.Song;
import com.example.lyricfinder.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private LottieAnimationView progressBar;
    private LottieAnimationView progressBarBottom;
    private TextView errorTextView;
    private Button retryButton;
    private SearchView searchView;
    private LinearLayout errorView;

    private List<Song> allSongs = new ArrayList<>();
    private List<Song> displayedSongs = new ArrayList<>();
    private int songIndex = 0;
    private boolean isLoading = false;
    private Handler handler;
    private Executor executor;
    private View overlayView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        recyclerView = view.findViewById(R.id.rvSong);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(songAdapter);

        progressBar = view.findViewById(R.id.progressBar);
        progressBarBottom = view.findViewById(R.id.progressBarBottom);
        errorView = view.findViewById(R.id.errorView);
        errorTextView = view.findViewById(R.id.errorTextView);
        retryButton = view.findViewById(R.id.retryButton);
        searchView = view.findViewById(R.id.searchView);
        overlayView = view.findViewById(R.id.overlayView);

        retryButton.setOnClickListener(v -> fetchChartSongs());

        setupSearchView();

        fetchChartSongs();

        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        setupScrollListener();

        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    private void fetchChartSongs() {
        showProgressBar();
        hideError();
        recyclerView.setVisibility(View.GONE);

        ApiService apiService = ApiConfig.getApiService();
        Call<SongResponse> call = apiService.getChartSongs(50, 1);

        call.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                hideProgressBar();
                if (response.isSuccessful() && response.body() != null) {
                    List<ChartItem> chartItems = response.body().getChartItems();
                    allSongs.clear();
                    for (ChartItem item : chartItems) {
                        allSongs.add(item.getItem());
                    }
                    songIndex = 0;
                    displayedSongs.clear();
                    addInitialSongs();
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    showError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                hideProgressBar();
                showError(t.getMessage());
            }
        });
    }

    private void addInitialSongs() {
        int endIndex = Math.min(songIndex + 20, allSongs.size());
        displayedSongs.addAll(allSongs.subList(songIndex, endIndex));
        songIndex = endIndex;
        songAdapter.setSongs(displayedSongs);
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == displayedSongs.size() - 1) {
                    if (!isLoading && songIndex < allSongs.size()) {
                        loadMoreSongs();
                    }
                }
            }
        });
    }

    private void loadMoreSongs() {
        isLoading = true;
        showBottomProgressBar();

        executor.execute(() -> {
            try {
                Thread.sleep(2000); // Simulate network delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                int endIndex = Math.min(songIndex + 20, allSongs.size());
                displayedSongs.addAll(allSongs.subList(songIndex, endIndex));
                songIndex = endIndex;
                songAdapter.setSongs(displayedSongs);
                hideBottomProgressBar();
                isLoading = false;
            });
        });
    }

    private void performSearch(String query) {
        List<Song> filteredSongs = new ArrayList<>();
        if (!query.isEmpty()) {
            for (Song song : allSongs) {
                if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        song.getArtistNames().toLowerCase().contains(query.toLowerCase())) {
                    filteredSongs.add(song);
                }
            }
        } else {
            filteredSongs.addAll(displayedSongs);
        }
        songAdapter.setSearching(!query.isEmpty());
        songAdapter.setSongs(filteredSongs);
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

    private void showBottomProgressBar() {
        progressBarBottom.setVisibility(View.VISIBLE);
        progressBarBottom.playAnimation();
    }

    private void hideBottomProgressBar() {
        progressBarBottom.setVisibility(View.GONE);
        progressBarBottom.cancelAnimation();
    }
}
