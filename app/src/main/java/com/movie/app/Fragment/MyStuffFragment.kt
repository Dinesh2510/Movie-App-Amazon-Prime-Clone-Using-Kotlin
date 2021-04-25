package com.movie.app.Fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.movie.app.Adapter.WishListAdapter
import com.movie.app.Forms.Login
import com.movie.app.Helper.API
import com.movie.app.Helper.Tools
import com.movie.app.Model.MovieModel
import com.movie.app.R
import kotlinx.android.synthetic.main.ui_bottom_sheet_basic_bottom_sheet_list.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MyStuffFragment : Fragment() {
    var recyclerView_popular: RecyclerView? = null
    var no_item: LinearLayout? = null
    var no_internet: LinearLayout? = null
    private lateinit var movieModel: ArrayList<MovieModel>
    var sharedPreferences: SharedPreferences? = null
    var str_userid: String? = null
    internal lateinit var dialog: BottomSheetDialog
    var aubl: MyStuffFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_my_stuff, container, false)
        aubl = this;
        sharedPreferences =
            activity?.getSharedPreferences(Login.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        str_userid = sharedPreferences?.getString(Login.USER_ID, "")
        movieModel = java.util.ArrayList<MovieModel>()

        no_item = view.findViewById(R.id.no_item)
        no_internet = view.findViewById(R.id.no_internet)
        recyclerView_popular = view.findViewById(R.id.recyclerView_wishlist)
        recyclerView_popular?.setLayoutManager(GridLayoutManager(context, 2))

//        recyclerView_popular?.setLayoutManager(
//            LinearLayoutManager(
//                context,
//                LinearLayoutManager.HORIZONTAL,
//                false
//            )
//        )

        return view

    }

    override fun onResume() {
        super.onResume()
        if (Tools.isNetworkAvailable(activity)) {
            getWishList()
        } else {
            Toast.makeText(activity, "Internet Connection Not Available", Toast.LENGTH_SHORT)
                .show()
            no_internet?.setVisibility(View.VISIBLE)
        }
    }


    private fun getWishList() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show()

        movieModel.clear()
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, API.WISHLIST_DATA,
                com.android.volley.Response.Listener { response ->
                    val code: String
                    val message: String
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("response", response)
                        code = jsonObject.getString("code")
                        message = jsonObject.getString("message")
                        if (code == "200") {

                            val jsonArray_popular = jsonObject.getJSONArray("list")


                            Log.d("jsonArray_langauge", "" + jsonArray_popular)


                            /*Popular LIST START*/
                            for (k in 0 until jsonArray_popular.length()) {
                                val listpObject = jsonArray_popular.getJSONObject(k)
                                val model = MovieModel()
                                model.thumbnail = (listpObject.getString("thumbnail"))
                                model.name = (listpObject.getString("name"))
                                model.moive_id = (listpObject.getString("moive_id"))
                                movieModel.add(model)
                            }
                            val adaptor_movie = activity?.let { WishListAdapter(movieModel, it) }
                            recyclerView_popular?.adapter = adaptor_movie


                        } else {
                            no_item?.setVisibility(View.VISIBLE)

/*
                            Toast.makeText(
                                activity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            ).show()
*/
                        }
                    } catch (e: JSONException) {
                        no_item?.setVisibility(View.VISIBLE)
/*
                        Toast.makeText(
                            activity,
                            "" + e.printStackTrace().toString(),
                            Toast.LENGTH_LONG
                        ).show()
*/
                    }
                    progressDialog.dismiss()
                }, com.android.volley.Response.ErrorListener {
                    progressDialog.dismiss()
/*
                    Toast.makeText(
                        activity,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
*/
                    no_item?.setVisibility(View.VISIBLE)
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): MutableMap<String?, String?> {
                    val params: MutableMap<String?, String?> =
                        HashMap()
                    params["user_id"] = str_userid
                    Log.d("tag_pa", "getParams: $params")
                    return params
                }

            }
        Volley.newRequestQueue(activity).add(stringRequest)
    }

    fun onClickLONGCalled(i: Int, moiveId: String?) {

    }


}