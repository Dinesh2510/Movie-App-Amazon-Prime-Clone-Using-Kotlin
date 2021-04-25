package com.movie.app.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.movie.app.Adapter.VideosAdapter
import com.movie.app.Model.VideoItem
import com.movie.app.R
import java.util.*


class ShortVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_short_video)
        val videosViewPager = findViewById<ViewPager2>(R.id.viewPagerVideos)
        val videoItems: MutableList<VideoItem> = ArrayList()
        val item = VideoItem()
        item.videoURL = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_shorts/production%20ID_4812203.mp4"
        item.videoTitle = "Women In Tech"
        item.videoDesc = "International Women's Day 2019"
        videoItems.add(item)
        val item2 = VideoItem()
        item2.videoURL = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_shorts/production%20ID_4444485.mp4"
        item2.videoTitle = "Sasha Solomon"
        item2.videoDesc = "How Sasha Solomon Became a Software Developer at Twitter"
        videoItems.add(item2)
        val item3 = VideoItem()
        item3.videoURL = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_shorts/production%20ID_4434286.mp4"
        item3.videoTitle = "Happy Hour Wednesday"
        item3.videoDesc = " Depth-First Search Algorithm"
        videoItems.add(item3)
        val item4 = VideoItem()
        item4.videoURL = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_shorts/production%20ID_4812203.mp4"
        item4.videoTitle = "Happy Hour Wednesday"
        item4.videoDesc = " Depth-First Search Algorithm"
        videoItems.add(item4)
        videosViewPager.adapter = VideosAdapter(videoItems)
    }
}