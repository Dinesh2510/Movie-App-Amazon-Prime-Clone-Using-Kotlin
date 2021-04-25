package com.movie.app.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.movie.app.Activity.MovieDetail
import com.movie.app.Model.MovieModel
import com.movie.app.R

class PopularAdapter(private val postList: ArrayList<MovieModel>, private val mCtx: Context) :
    RecyclerView.Adapter<PopularAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(R.layout.item_movie_vert, null)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: MovieModel = postList[position]

        Log.d("Post_name", "onBindViewHolder: " + post.name)

        Glide.with(mCtx)
            .load("http://pixeldev.in/webservices/movie_app/movie_admin/" + post.thumbnail)
            .placeholder(R.drawable.cinema)
            .into(holder.myImageView)
        Log.d(
            "img_url",
            "onBindViewHolder: " + "http://pixeldev.in/webservices/movie_app/movie_admin/movie_thumbnail/" + post.thumbnail
        )
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(mCtx, MovieDetail::class.java)
            intent.putExtra("id", post.moive_id)
           Log.d("oo", "onClick: " + post.moive_id)
            mCtx.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var myImageView: ImageView

        init {
            myImageView = itemView.findViewById(R.id.img_list_background)
        }
    }
}