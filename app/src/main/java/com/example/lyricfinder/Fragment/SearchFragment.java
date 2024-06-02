package com.example.lyricfinder.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.lyricfinder.Adapter.SearchAdapter;
import com.example.lyricfinder.Data.SearchResponse;
import com.example.lyricfinder.Models.Song;
import com.example.lyricfinder.R;
import com.example.lyricfinder.SearchResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private LottieAnimationView progressBar;
    private LinearLayout errorView;
    private TextView errorTextView;
    private Button retryButton;
    private SearchView searchView;

    private List<Song> searchResults = new ArrayList<>();
    private String savedQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.rvSong);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(searchAdapter);

        progressBar = view.findViewById(R.id.progressBar);
        errorView = view.findViewById(R.id.errorView);
        errorTextView = view.findViewById(R.id.errorTextView);
        retryButton = view.findViewById(R.id.retryButton);
        searchView = view.findViewById(R.id.searchView);

        progressBar.playAnimation();

        retryButton.setOnClickListener(v -> fetchChartSongs(""));

        setupSearchView();

        if (savedInstanceState != null) {
            savedQuery = savedInstanceState.getString("SAVED_QUERY", "");
            if (!TextUtils.isEmpty(savedQuery)) {
                searchView.setQuery(savedQuery, false);
                performSearch(savedQuery);
            }
        }

        fetchChartSongs("");
        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                savedQuery = newText;
                performSearch(newText);
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchView.setQuery(savedQuery, false);
            }
        });
    }

    private void fetchChartSongs(String query) {
        progressBar.setVisibility(View.VISIBLE);
        hideError();
        recyclerView.setVisibility(View.GONE);

        ApiService apiService = ApiConfig.getApiService();
        Call<SearchResponse> call = apiService.searchSongs(query);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResult> searchResults1 = response.body().getHits();
                    searchResults.clear();
                    for (SearchResult result : searchResults1) {
                        Song song = result.getResult();
                        if (song.getArtistNames().toLowerCase().contains(query.toLowerCase()) ||
                                song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            searchResults.add(song);
                        }
                    }
                    searchAdapter.setSearchResults(searchResults);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    String errorMsg = "Error: " + response.message();
                    Log.e("SearchFragment", errorMsg);
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                String errorMsg = "Failure: " + t.getMessage();
                Log.e("SearchFragment", errorMsg);
                showError(errorMsg);
            }
        });
    }

    private void performSearch(String query) {
        fetchChartSongs(query);
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SAVED_QUERY", savedQuery);
    }
}
