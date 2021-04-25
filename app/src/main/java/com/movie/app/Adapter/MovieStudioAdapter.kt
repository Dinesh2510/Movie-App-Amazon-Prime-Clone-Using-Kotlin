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
import com.movie.app.Activity.MovieDetail
import com.movie.app.Activity.StudioMovieList
import com.movie.app.Helper.API
import com.movie.app.Model.StudioModel
import com.movie.app.R


class MovieStudioAdapter(private val postList: ArrayList<StudioModel>, private val mCtx: Context) :
    RecyclerView.Adapter<MovieStudioAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(R.layout.item_movie_studio, null)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: StudioModel = postList[position]

        Log.d("Post_name", "onBindViewHolder: " + post.studio_name)
        holder.name.text = post.studio_name

        Glide.with(mCtx)
            .load(API.studio_image + post.studio_img)
            .into(holder.myImageView)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(mCtx, StudioMovieList::class.java)
           intent.putExtra("id", post.studio_id)
           intent.putExtra("name", post.studio_name)
            Log.d("oo", "onClick: " + post.studio_id)
            mCtx.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var myImageView: ImageView
        var name: TextView

        init {
            myImageView = itemView.findViewById(R.id.avatar)
            name = itemView.findViewById(R.id.name)
        }
    }
}