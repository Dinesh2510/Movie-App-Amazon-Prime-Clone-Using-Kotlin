package com.movie.app.Fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.movie.app.R


class NewsDetails : AppCompatActivity() {

    private var mContext: Context? = null

    private var mRelativeLayout: RelativeLayout? = null
    private var mSeekBar: SeekBar? = null
    private var mTextView: TextView? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_news_details)
        mContext = applicationContext


        val context = applicationContext

        // Check whether has the write settings permission or not.

        // Check whether has the write settings permission or not.
        val settingsCanWrite = Settings.System.canWrite(context)

        if (!settingsCanWrite) {
            // If do not have write settings permission then open the Can modify system settings panel.
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            startActivity(intent)
        } else {
            // If has permission then show an alert dialog with message.
            val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
            alertDialog.setMessage("You have system write settings permission now.")
            alertDialog.show()
        }
        // Get the widgets reference from XML layout
        mRelativeLayout = findViewById<View>(R.id.rl) as RelativeLayout
        mSeekBar = findViewById<View>(R.id.seek_bar) as SeekBar
        mTextView = findViewById<View>(R.id.tv) as TextView

        // Set the SeekBar initial progress from screen current brightness

        // Set the SeekBar initial progress from screen current brightness
        val brightness: Int = getScreenBrightness()
        mSeekBar?.setProgress(brightness)
        mTextView?.setText("Screen Brightness : $brightness")

        // Set a SeekBar change listener

        // Set a SeekBar change listener
        mSeekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                mTextView?.setText("Screen Brightness : $i")

                // Change the screen brightness
                setScreenBrightness(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

    }

    fun setScreenBrightness(brightnessValue: Int) {
        /*
            public abstract ContentResolver getContentResolver ()
                Return a ContentResolver instance for your application's package.
        */
        /*
            Settings
                The Settings provider contains global system-level device preferences.

            Settings.System
                System settings, containing miscellaneous system preferences. This table holds
                simple name/value pairs. There are convenience functions for accessing
                individual settings entries.
        */
        /*
            public static final String SCREEN_BRIGHTNESS
                The screen backlight brightness between 0 and 255.
                Constant Value: "screen_brightness"
        */
        /*
            public static boolean putInt (ContentResolver cr, String name, int value)
                Convenience function for updating a single settings value as an integer. This will
                either create a new entry in the table if the given name does not exist, or modify
                the value of the existing row with that name. Note that internally setting values
                are always stored as strings, so this function converts the given value to a
                string before storing it.

            Parameters
                cr : The ContentResolver to access.
                name : The name of the setting to modify.
                value : The new value for the setting.
            Returns
                true : if the value was set, false on database errors
        */

        // Make sure brightness value between 0 to 255
        if (brightnessValue >= 0 && brightnessValue <= 255) {
            Settings.System.putInt(
                mContext!!.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                brightnessValue
            )
        }
    }

    // Get the screen current brightness
    protected fun getScreenBrightness(): Int {
        /*
            public static int getInt (ContentResolver cr, String name, int def)
                Convenience function for retrieving a single system settings value as an integer.
                Note that internally setting values are always stored as strings; this function
                converts the string to an integer for you. The default value will be returned
                if the setting is not defined or not an integer.

            Parameters
                cr : The ContentResolver to access.
                name : The name of the setting to retrieve.
                def : Value to return if the setting is not defined.
            Returns
                The setting's current value, or 'def' if it is not defined or not a valid integer.
        */
        return Settings.System.getInt(
            mContext!!.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            0
        )
    }
}