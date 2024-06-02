package com.example.lyricfinder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lyricfinder.Activity.SongLyricsActivity;
import com.example.lyricfinder.Fragment.ProfilFragment;
import com.example.lyricfinder.Models.Song;
import com.example.lyricfinder.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Song> searchResults;
    private static Context context;
    private static ExecutorService executorService;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;


    public SearchAdapter(Context context, List<Song> searchResults) {
        this.searchResults = searchResults;
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.editor = sharedPreferences.edit();
    }

    public void setSearchResults(List<Song> searchResults) {
        this.searchResults = searchResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Song song = searchResults.get(position);
        holder.bind(song);

        boolean isFavorite = checkIfFavorite(song.getId());
        song.setFavorite(isFavorite);

        int favoriteIcon = song.isFavorite() ? R.drawable.pressed_favorite_icon : R.drawable.baseline_favorite_border_24;
        holder.btnFav.setImageResource(favoriteIcon);

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavorite = song.isFavorite();

                if (isFavorite) {
                    removeFavorite(song);
                    showToast("Removed from favorites");
                } else {
                    addFavorite(song);
                    showToast("Added to favorites");
                }

                song.setFavorite(!isFavorite);

                int newFavoriteIcon = song.isFavorite() ? R.drawable.pressed_favorite_icon : R.drawable.baseline_favorite_border_24;
                holder.btnFav.setImageResource(newFavoriteIcon);

                if (context instanceof ProfilFragment.ProfilFragmentListener) {
                    ((ProfilFragment.ProfilFragmentListener) context).onFavoriteUpdated();
                }
            }
        });
    }

    private boolean checkIfFavorite(String songId) {
        return sharedPreferences.getBoolean(songId, false);
    }

    private void addFavorite(Song song) {
        editor.putBoolean(song.getId(), true);
        editor.apply();
        updateFavoriteSongs(song, true);
    }

    private void removeFavorite(Song song) {
        editor.putBoolean(song.getId(), false);
        editor.apply();
        updateFavoriteSongs(song, false);
    }

    private void updateFavoriteSongs(Song song, boolean add) {
        List<Song> favoriteSong = getFavoriteSongsFromSharedPreferences();
        if (favoriteSong == null) {
            favoriteSong = new ArrayList<>();
        }
        if (add) {
            favoriteSong.add(song);
        } else {
            favoriteSong.removeIf(s -> s.getId().equals(song.getId()));
        }
        String jsonFavorites = gson.toJson(favoriteSong);
        editor.putString("favorites", jsonFavorites);
        editor.apply();
    }

    private List<Song> getFavoriteSongsFromSharedPreferences() {
        String jsonFavorites = sharedPreferences.getString("favorites", null);
        if (jsonFavorites != null) {
            Type type = new TypeToken<List<Song>>() {}.getType();
            return gson.fromJson(jsonFavorites, type);
        } else {
            return null; // Mengembalikan null jika tidak ada data favorit yang tersimpan
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Metode onPause() yang disarankan untuk menghapus pesan tertunda dari thread pool
    public void onPause() {
        executorService.shutdown();
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView artistTextView;
        private final ImageView headerImageView;
        private final LinearLayout lyricsButton;
        private final ImageView btnFav;


        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.songTitle);
            artistTextView = itemView.findViewById(R.id.songArtist);
            headerImageView = itemView.findViewById(R.id.songImage);
            lyricsButton = itemView.findViewById(R.id.btnLyrics);
            btnFav = itemView.findViewById(R.id.btnFav);
        }

        public void bind(Song search) {
            titleTextView.setText(search.getTitle());
            artistTextView.setText(search.getArtistNames());
            Glide.with(itemView.getContext())
                    .load(search.getHeaderImageUrl())
                    .into(headerImageView);
            lyricsButton.setOnClickListener(v -> {
                executorService.execute(() -> {
                    Intent intent = new Intent(context, SongLyricsActivity.class);
                    intent.putExtra("SONG_ID", String.valueOf(search.getId()));

                    new Handler(Looper.getMainLooper()).post(() -> {
                        context.startActivity(intent);
                    });
                });
            });
        }
    }
}
