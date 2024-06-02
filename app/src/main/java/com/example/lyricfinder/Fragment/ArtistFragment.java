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
import com.example.lyricfinder.Adapter.ArtistAdapter;
import com.example.lyricfinder.ChartArtist;
import com.example.lyricfinder.Data.ArtistResponse;
import com.example.lyricfinder.Models.Artist;
import com.example.lyricfinder.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;
    private LottieAnimationView progressBar;
    private LottieAnimationView progressBarBottom;
    private TextView errorTextView;
    private Button retryButton;
    private SearchView searchView;
    private LinearLayout errorView;

    private List<Artist> allArtists = new ArrayList<>();
    private List<Artist> displayedArtists = new ArrayList<>();
    private int artistIndex = 0;
    private boolean isLoading = false;
    private Handler handler;
    private Executor executor;
    private View overlayView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        recyclerView = view.findViewById(R.id.rvArtist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artistAdapter = new ArtistAdapter(new ArrayList<>());
        recyclerView.setAdapter(artistAdapter);

        progressBar = view.findViewById(R.id.progressBar);
        progressBarBottom = view.findViewById(R.id.progressBarBottom);
        errorView = view.findViewById(R.id.errorView);
        errorTextView = view.findViewById(R.id.errorTextView);
        retryButton = view.findViewById(R.id.retryButton);
        searchView = view.findViewById(R.id.searchView);
        overlayView = view.findViewById(R.id.overlayView);

        retryButton.setOnClickListener(v -> fetchChartArtists());

        setupSearchView();

        fetchChartArtists();

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

    private void fetchChartArtists() {
        showProgressBar();
        hideError();
        recyclerView.setVisibility(View.GONE);

        ApiService apiService = ApiConfig.getApiService();
        Call<ArtistResponse> call = apiService.getChartArtists(50, 1);

        call.enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                hideProgressBar();
                if (response.isSuccessful() && response.body() != null) {
                    List<ChartArtist> chartArtists = response.body().getChartItems();
                    if (chartArtists != null) {
                        allArtists.clear();
                        for (ChartArtist chartArtist : chartArtists) {
                            Artist artist = chartArtist.getItem();
                            if (artist != null) {
                                allArtists.add(artist);
                            }
                        }
                        artistIndex = 0;
                        displayedArtists.clear();
                        addInitialArtists();
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        showError("No artists found.");
                    }
                } else {
                    showError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                hideProgressBar();
                showError(t.getMessage());
            }
        });
    }

    private void addInitialArtists() {
        int endIndex = Math.min(artistIndex + 10, allArtists.size());
        displayedArtists.addAll(allArtists.subList(artistIndex, endIndex));
        artistIndex = endIndex;
        artistAdapter.setArtists(displayedArtists);
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == displayedArtists.size() - 1) {
                    if (!isLoading && artistIndex < allArtists.size()) {
                        loadMoreArtists();
                    }
                }
            }
        });
    }

    private void loadMoreArtists() {
        isLoading = true;
        showBottomProgressBar();
        executor.execute(() -> {
            try {
                Thread.sleep(2000); // Delay 2 detik untuk simulasi load
                handler.post(() -> addInitialArtists());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                int endIndex = Math.min(artistIndex + 10, allArtists.size());
                displayedArtists.addAll(allArtists.subList(artistIndex, endIndex));
                artistIndex = endIndex;
                artistAdapter.setArtists(displayedArtists);
                hideBottomProgressBar();
                isLoading = false;
            });
        });
    }

    private void performSearch(String query) {
        List<Artist> filteredArtists = new ArrayList<>();
        if (!query.isEmpty()) {
            for (Artist artist : allArtists) {
                if (artist.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredArtists.add(artist);
                }
            }
        } else {
            filteredArtists.addAll(allArtists);
        }
        artistAdapter.setSearching(!query.isEmpty());
        artistAdapter.setArtists(filteredArtists);
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