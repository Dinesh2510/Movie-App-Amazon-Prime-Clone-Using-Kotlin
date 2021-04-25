package com.movie.app.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.movie.app.Activity.MovieDetail
import com.movie.app.Helper.API
import com.movie.app.Model.GenreModel
import com.movie.app.R

class GenreAdapter (private val postList: ArrayList<GenreModel>, private val mCtx: Context) :
    RecyclerView.Adapter<GenreAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(R.layout.item_genre, null)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: GenreModel = postList[position]
        holder.txt_categ.setText(post.genre_name)

        Log.d("Post_name", "onBindViewHolder: " + post.genre_name)

      Glide.with(mCtx)
            .load(API.movie_genre + post.genre_img)
            .placeholder(R.drawable.baseline_mail)
            .into(holder.myImageView)
        holder.itemView.setOnClickListener(View.OnClickListener {
           val intent = Intent(mCtx, MovieDetail::class.java)
//            intent.putExtra("title", post.getPost_title())
//            intent.putExtra("content", post.getPost_content())
//            intent.putExtra("username", post.getPost_username())
//            intent.putExtra("date", post.getPost_date())
//            intent.putExtra("id", post.getPost_id())
//            intent.putExtra("image", post.getPost_image())
//            intent.putExtra("link", postList[position].getPost_link())
//            intent.putExtra("like", postList[position].getPost_like())
//            intent.putExtra("premium", postList[position].getPremium_flag())
//            Log.d("oo", "onClick: " + postList[position].getPremium_flag())
          mCtx.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return postList!!.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_categ: TextView
        var lyt_all: LinearLayout
        var myImageView: ImageView


        init {
            txt_categ = itemView.findViewById(R.id.txt_categ)
            lyt_all = itemView.findViewById(R.id.lyt_all)
            myImageView = itemView.findViewById(R.id.myImageView)
        }
    }
}