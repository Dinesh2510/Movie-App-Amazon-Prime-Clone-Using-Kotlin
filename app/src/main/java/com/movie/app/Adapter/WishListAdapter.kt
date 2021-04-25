package com.movie.app.Adapter


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.movie.app.Activity.MovieDetail
import com.movie.app.Forms.Login
import com.movie.app.Helper.API
import com.movie.app.Model.MovieModel
import com.movie.app.R
import kotlinx.android.synthetic.main.ui_bottom_sheet_basic_bottom_sheet_list.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import kotlin.jvm.Throws

class WishListAdapter(private val postList: ArrayList<MovieModel>, private val mCtx: Context) :
    RecyclerView.Adapter<WishListAdapter.PostViewHolder>() {
    internal lateinit var dialog: BottomSheetDialog
    var sharedPreferences: SharedPreferences? = null
    var str_userid: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(R.layout.item_comming, null)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: MovieModel = postList[position]

        Log.d("Post_name", "onBindViewHolder: " + post.name)
        holder.name.text = post.name;
        sharedPreferences =
            mCtx.getSharedPreferences(Login.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        str_userid = sharedPreferences?.getString(Login.USER_ID, "")
        Glide.with(mCtx)
            .load("http://pixeldev.in/webservices/movie_app/movie_admin/" + post.thumbnail)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.cinema)
            .into(holder.image)
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
        // (mCtx as MyStuffFragment).onClickLONGCalled(holder.adapterPosition, postList[position].moive_id)

        holder.itemView.setOnLongClickListener { // Do your stuff
            MyCustomerBottomSheetDialog(postList[position].moive_id, str_userid, position)
            false
        }
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

    fun MyCustomerBottomSheetDialog(
        moiveId: String?,
        strUserid: Any?,
        param: Any?
    ) {
        dialog = BottomSheetDialog(mCtx)

        dialog.setContentView(R.layout.ui_bottom_sheet_basic_bottom_sheet_list)

        val shareTextView = dialog.shareUiBottomSheet
        shareTextView.setOnClickListener {
            Toast.makeText(
                mCtx,
                "Clicked Shared.",
                Toast.LENGTH_SHORT
            ).show()
        }

        val getLinkTextView = dialog.getLinkUiBottomSheet
        getLinkTextView.setOnClickListener {
            RemovedBookMark(moiveId, str_userid, param)
            dialog.dismiss()
        }
        dialog.show()

        val downloadTextView = dialog.downloadUiBottomSheet
        downloadTextView.setOnClickListener {
            Toast.makeText(
                mCtx,
                "Clicked Download.",
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.show()

        val viewDetailTextView = dialog.viewDetailUiBottomSheet
        viewDetailTextView.setOnClickListener {
            Toast.makeText(
                mCtx,
                "Clicked View Detail.",
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.show()
    }

    fun removeItem(position: Int) {
        postList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    private fun RemovedBookMark(
        moiveId: String?,
        strUserid: String?,
        param: Any?
    ) {
        val progressDialog = ProgressDialog(mCtx)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show();
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            API.Remove_WISHLIST,
            Response.Listener { response ->
                Log.d("quick_2", "onResponse: $response")
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    val code = jsonObject.getString("code")
                    if (code == "200") {
                        removeItem(param as Int)

                        if (getItemCount() == 0) {
                            Toast.makeText(mCtx, "Wishlist Is Empty", Toast.LENGTH_SHORT).show()

                        }
                        Toast.makeText(mCtx, message, Toast.LENGTH_SHORT).show()

                    } else {

                        progressDialog.dismiss()
                        Toast.makeText(mCtx, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    progressDialog.dismiss()
                }
                progressDialog.dismiss()
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                progressDialog.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String?, String?> {
                val params: MutableMap<String?, String?> =
                    HashMap()
                params["user_id"] = strUserid
                params["movie_id"] = moiveId
                Log.d("tag_pa", "getParams: $params")
                return params
            }

        }
        val requestQueue = Volley.newRequestQueue(mCtx)
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy
        requestQueue.add(stringRequest)

    }

}