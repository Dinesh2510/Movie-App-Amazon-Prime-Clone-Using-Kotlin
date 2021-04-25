package com.movie.app.Adapter;


import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.movie.app.Helper.API;
import com.movie.app.Model.MovieModel;
import com.movie.app.R;

/**
 * Created on : May 24, 2019
 * Author     : AndroidWave
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder {

  /**
   * below view have public modifier because
   * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
   */
  public CardView mediaContainer;
  public ImageView mediaCoverImage, volumeControl;
  public ProgressBar progressBar;
  public RequestManager requestManager;
  private TextView title, userHandle;
  private View parent;

  public PlayerViewHolder(@NonNull View itemView) {
    super(itemView);
    parent = itemView;
    mediaContainer = itemView.findViewById(R.id.mediaContainer);
    mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
    title = itemView.findViewById(R.id.tvTitle);
    userHandle = itemView.findViewById(R.id.tvUserHandle);
    progressBar = itemView.findViewById(R.id.progressBar);
    volumeControl = itemView.findViewById(R.id.ivVolumeControl);
  }

  void onBind(MovieModel mediaObject, RequestManager requestManager) {
    this.requestManager = requestManager;
    parent.setTag(this);
    title.setText(mediaObject.getName());
    userHandle.setText(mediaObject.getMoive_id());
    this.requestManager
        .load(API.play_image+mediaObject.getMoive_banner())
        .into(mediaCoverImage);
  }
}

