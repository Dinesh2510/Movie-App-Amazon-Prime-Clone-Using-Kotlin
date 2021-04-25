package com.movie.app.Adapter;

;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.movie.app.Model.MovieModel;
import com.movie.app.R;

import java.util.ArrayList;

public class MediaRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private ArrayList<MovieModel> mediaObjects;
  private RequestManager requestManager;

  public MediaRecyclerAdapter(ArrayList<MovieModel> mediaObjects,
                              RequestManager requestManager) {
    this.mediaObjects = mediaObjects;
    this.requestManager = requestManager;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    return new PlayerViewHolder(
        LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.item_exo_vert, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    ((PlayerViewHolder) viewHolder).onBind(mediaObjects.get(i), requestManager);
  }

  @Override
  public int getItemCount() {
    return mediaObjects.size();
  }
}
