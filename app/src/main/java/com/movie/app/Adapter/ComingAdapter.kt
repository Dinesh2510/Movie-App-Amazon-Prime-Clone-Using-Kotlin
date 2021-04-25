package com.movie.app.Adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.movie.app.Activity.MovieDetail
import com.movie.app.Helper.API
import com.movie.app.Model.MovieModel
import com.movie.app.R

class ComingAdapter(private val postList: ArrayList<MovieModel>, private val mCtx: Context) :
    RecyclerView.Adapter<ComingAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(R.layout.item_comming, null)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: MovieModel = postList!![position]

        Log.d("Post_name", "onBindViewHolder: " + post.name)
        holder.name.text = post.name;

        Glide.with(mCtx)
            .load(API.play_image + post.moive_banner)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.place_bg)
            .into(holder.image)
        Log.d(
            "img_url",
            "onBindViewHolder: " + "http://pixeldev.in/webservices/movie_app/movie_admin/movie_thumbnail/" + post.thumbnail
        )
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(mCtx, MovieDetail::class.java)
            intent.putExtra("id", post.moive_id)
            intent.putExtra("moive_tag", post.moive_tag)
            Log.d("oo", "onClick: " + post.moive_tag)
            mCtx.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var name: TextView

        init {
            image = itemView.findViewById(R.id.image)
            name = itemView.findViewById(R.id.name)
        }
    }
}