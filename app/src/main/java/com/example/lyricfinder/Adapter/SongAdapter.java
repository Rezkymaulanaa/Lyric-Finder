package com.example.lyricfinder.Adapter;

// SongAdapter.java

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songs;
    private boolean isSearching = false;
    private Context context;
    private ExecutorService executorService;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
        this.executorService = Executors.newSingleThreadExecutor();
        this.sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.editor = sharedPreferences.edit();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position, isSearching);

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

    class SongViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView artistTextView;
        private final TextView tvNumber;
        private final ImageView headerImageView;
        private final LinearLayout lyricsButton;
        private final ImageView btnFav;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            titleTextView = itemView.findViewById(R.id.songTitle);
            artistTextView = itemView.findViewById(R.id.songArtist);
            headerImageView = itemView.findViewById(R.id.songImage);
            lyricsButton = itemView.findViewById(R.id.btnLyrics);
            btnFav = itemView.findViewById(R.id.btnFav);
        }

        public void bind(Song song, int position, boolean isSearching) {
            if (isSearching) {
                tvNumber.setVisibility(View.GONE);
            } else {
                tvNumber.setVisibility(View.VISIBLE);
                tvNumber.setText(String.valueOf(position + 1));
            }
            titleTextView.setText(song.getTitle());
            artistTextView.setText(song.getArtistNames());
            Glide.with(itemView.getContext())
                    .load(song.getHeaderImageUrl())
                    .into(headerImageView);

            lyricsButton.setOnClickListener(v -> {
                executorService.execute(() -> {
                    Intent intent = new Intent(context, SongLyricsActivity.class);
                    intent.putExtra("SONG_ID", String.valueOf(song.getId()));

                    new Handler(Looper.getMainLooper()).post(() -> {
                        context.startActivity(intent);
                    });
                });
            });
        }


    }
}
