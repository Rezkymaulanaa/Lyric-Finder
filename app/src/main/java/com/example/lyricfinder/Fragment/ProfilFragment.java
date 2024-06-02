package com.example.lyricfinder.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lyricfinder.Adapter.SongAdapter;
import com.example.lyricfinder.Models.Song;
import com.example.lyricfinder.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProfilFragment extends Fragment {
    private RecyclerView rvFavorite;
    private SongAdapter songAdapter;
    private List<Song> favoriteSong;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private ProfilFragmentListener listener;
    private LinearLayout errorView;
    private TextView errorTextView;
    private Button retryButton;
    private View overlayView;
    private LottieAnimationView progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // Initialize views
        rvFavorite = view.findViewById(R.id.rvFavorite);
        errorView = view.findViewById(R.id.errorView);
        errorTextView = view.findViewById(R.id.errorTextView);
        retryButton = view.findViewById(R.id.retryButton);
        overlayView = view.findViewById(R.id.overlayView);
        progressBar = view.findViewById(R.id.progressBar);

        sharedPreferences = getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        gson = new Gson();

        // Set up retry button
        retryButton.setOnClickListener(v -> loadData());

        // Initialize adapter with empty list to avoid null reference
        favoriteSong = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), favoriteSong);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavorite.setAdapter(songAdapter);

        // Load data
        loadData();

        return view;
    }

    private void loadData() {
        showProgressBar();
        hideError();

        // Simulate data loading (you would replace this with your actual data loading logic)
        new Handler().postDelayed(() -> {
            favoriteSong = getFavoriteSongsFromSharedPreferences();
            if (favoriteSong.isEmpty()) {
                showError("No favorite songs found.");
            } else {
                hideError();
                songAdapter.setSongs(favoriteSong);
                songAdapter.notifyDataSetChanged();  // Notify adapter about data change
            }
            hideProgressBar();
        }, 2000);  // Simulate a delay for loading data
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

    private List<Song> getFavoriteSongsFromSharedPreferences() {
        String jsonFavorites = sharedPreferences.getString("favorites", null);
        if (jsonFavorites != null) {
            Type type = new TypeToken<List<Song>>() {}.getType();
            return gson.fromJson(jsonFavorites, type);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteSong = getFavoriteSongsFromSharedPreferences();
        if (songAdapter != null) {
            songAdapter.setSongs(favoriteSong);
            songAdapter.notifyDataSetChanged();
        }
    }

    public interface ProfilFragmentListener {
        void onFavoriteUpdated();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfilFragmentListener) {
            listener = (ProfilFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfilFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onFavoriteUpdated() {
        if (listener != null) {
            favoriteSong = getFavoriteSongsFromSharedPreferences();
            if (songAdapter != null) {
                songAdapter.setSongs(favoriteSong);
                songAdapter.notifyDataSetChanged();
            }
        }
    }
}
