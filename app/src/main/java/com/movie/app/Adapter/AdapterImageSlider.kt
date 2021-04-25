package com.movie.app.Adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.movie.app.Helper.API
import com.movie.app.Model.NewsInfo
import com.movie.app.R


class AdapterImageSlider // constructor
    (private val act: Activity, private var items: List<NewsInfo>) :
    PagerAdapter() {
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: NewsInfo?)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getCount(): Int {
        return items.size
    }

    fun getItem(pos: Int): NewsInfo {
        return items[pos]
    }

    fun setItems(items: List<NewsInfo>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val newsInfo = items[position]
        val inflater =
            act.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.item_slider_image, container, false)
        val image =
            v.findViewById<View>(R.id.image) as ImageView
        Log.d(
            "bet",
            "instantiateItem: https://pixeldev.in/webservices/e_commerce/admin/" + newsInfo.image
        )
        Glide.with(act)
            .load(API.ImageUrl + newsInfo.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(image)
        val lyt_parent =
            v.findViewById<View>(R.id.lyt_parent) as MaterialRippleLayout

        lyt_parent.setOnClickListener { v ->
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClick(v, newsInfo)
            }
        }
        (container as ViewPager).addView(v)
        return v
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        (container as ViewPager).removeView(`object` as RelativeLayout)
    }

}