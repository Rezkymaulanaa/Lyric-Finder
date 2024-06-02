package com.example.lyricfinder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lyricfinder.Models.Artist;
import com.example.lyricfinder.R;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<Artist> artists;
    private boolean isSearching = false;

    public ArtistAdapter(List<Artist> artists) {
        this.artists = artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.bind(artist, position, isSearching);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumber;
        private final TextView artistName;
        private final ImageView artistImage;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            artistName = itemView.findViewById(R.id.artistName);
            artistImage = itemView.findViewById(R.id.artistImage);
        }

        public void bind(Artist artist, int position, boolean isSearching) {
            if (isSearching) {
                tvNumber.setVisibility(View.GONE);
            } else {
                tvNumber.setVisibility(View.VISIBLE);
                tvNumber.setText(String.valueOf(position + 1));
            }
            artistName.setText(artist.getName());
            Glide.with(itemView.getContext())
                    .load(artist.getImageUrl())
                    .into(artistImage);
        }
    }
}
