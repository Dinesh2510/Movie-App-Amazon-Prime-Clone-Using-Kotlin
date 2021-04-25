package com.movie.app.NoteApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.movie.app.R


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codingwithme.notesapp.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        replaceFragment(HomeFragment.newInstance(),false)

    }


    fun replaceFragment(fragment:Fragment, istransition:Boolean){
        val fragmentTransition = supportFragmentManager.beginTransaction()

        if (istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.add(R.id.frame_layout,fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragments = supportFragmentManager.fragments
        if (fragments.size == 0){
            finish()
        }
    }
}