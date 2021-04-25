package com.movie.app.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.movie.app.Activity.LangaugeDetails
import com.movie.app.Activity.MovieDetail
import com.movie.app.Model.LanguageModel
import com.movie.app.R

class LanguageAdapter(private val postList: ArrayList<LanguageModel>, private val mCtx: Context) :
    RecyclerView.Adapter<LanguageAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(R.layout.item_langauge, null)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: LanguageModel = postList!![position]
        holder.txt_class_name.setText(post.lang_name)

        Log.d("Post_name", "onBindViewHolder: " + post.lang_name)

        Glide.with(mCtx)
            .load("http://pixeldev.in/webservices/movie_app/movie_admin/language_img/" + post.lang_img)
            .placeholder(R.drawable.cinema)
            .into(holder.myImageView)
        Log.d(
            "img_url",
            "onBindViewHolder: " + "http://pixeldev.in/webservices/digital_reader/admin/" + post.lang_img
        )
        holder.itemView.setOnClickListener(View.OnClickListener {
           val intent = Intent(mCtx, LangaugeDetails::class.java)
            intent.putExtra("id", post.lang_id)
            intent.putExtra("name", post.lang_name)
            intent.putExtra("image", post.banner_img)
            Log.d("oo", "onClick: " + post.lang_name)
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
            Toast.makeText(mCtx, "" + post.lang_name, Toast.LENGTH_LONG).show()
        })
    }

    override fun getItemCount(): Int {
        return postList!!.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_class_name: TextView
        var myImageView: ImageView

        init {
            txt_class_name = itemView.findViewById(R.id.myImageViewText)
            myImageView = itemView.findViewById(R.id.myImageView)
        }
    }
}