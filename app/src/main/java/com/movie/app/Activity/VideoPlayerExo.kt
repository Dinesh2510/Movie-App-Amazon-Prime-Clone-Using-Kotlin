package com.movie.app.Activity

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.movie.app.R

class VideoPlayerExo : AppCompatActivity() {
    var btFullScreen: ImageView? = null
    var pipBtn: ImageView? = null
    var flag = false
    var playerView: PlayerView? = null
    var progressBar: ProgressBar? = null
    var MediaUrl: String? = null
    var str_tag: String? = null
    var simpleExoPlayer: SimpleExoPlayer? = null
    private val TAG: String = "PIP_TAG"
    var isPIPModeeEnabled: Boolean = true //Has the user disabled PIP mode in AppOpps?
    private var pictureInPictureParamsBuilder: PictureInPictureParams.Builder? = null

    private var mContext: Context? = null

    private var mRelativeLayout: RelativeLayout? = null
    private var mSeekBar: SeekBar? = null
    private var mTextView: TextView? = null
    private var tv_vol: TextView? = null
    var mediaPlayer: SeekBar? = null
    var audioManager: AudioManager? = null
    var maxVolume = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_video_player_exo)
        mContext = applicationContext

        MediaUrl = intent.getStringExtra("url")
        str_tag = intent.getStringExtra("tag")

        val context = applicationContext
        println("MediaUrlMediaUrl   :- $MediaUrl")

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
        tv_vol = findViewById<View>(R.id.tv_vol) as TextView
        mediaPlayer = findViewById(R.id.seekBar2);
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?;
        try {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            //set max progress according to volume
            mediaPlayer?.setMax(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
            //get current volume
            mediaPlayer?.setProgress(audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC))
            //Set the seek bar progress to 1
            mediaPlayer?.setKeyProgressIncrement(1)
            //get max volume
            maxVolume = mediaPlayer?.getMax()!!
            mediaPlayer?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int,
                    fromUser: Boolean
                ) {
                    audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    //Calculate the brightness percentage
                    val perc = progress / maxVolume.toFloat() * 100
                    //Set the brightness percentage
                    Log.d(TAG, "onProgressChanged: " + perc.toInt())
                    tv_vol?.setText("Volume: " + perc.toInt() + "%")
                }
            })
        } catch (e: Exception) {
        }
        /* audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.let { mediaPlayer?.setMax(it) };
         mediaPlayer?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
             override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                 audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0)
             }

             override fun onStartTrackingTouch(seekBar: SeekBar?) {}
             override fun onStopTrackingTouch(seekBar: SeekBar?) {}
         })
 */
        // Set the SeekBar initial progress from screen current brightness

        // Set the SeekBar initial progress from screen current brightness
        val brightness: Int = getScreenBrightness()
        mSeekBar?.setProgress(brightness)
        mTextView?.setText("Brightness : $brightness")

        // Set a SeekBar change listener

        // Set a SeekBar change listener
        mSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                mTextView?.setText("Brightness : $i")

                // Change the screen brightness
                setScreenBrightness(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        playerView = findViewById<View>(R.id.player_view) as PlayerView
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        btFullScreen = playerView!!.findViewById<View>(R.id.bt_fullscreen) as ImageView
        pipBtn = playerView!!.findViewById<View>(R.id.pipBtn) as ImageView

        //init PictureInPictureParams, requires Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPictureParamsBuilder = PictureInPictureParams.Builder()
        }

        //handle click, enter PIP
        pipBtn?.setOnClickListener {
            pictureInPictureMode()
        }
        var videoUrl: Uri? = null

        if (str_tag.equals("0")) {
            videoUrl =
                Uri.parse("http://pixeldev.in/webservices/movie_app/movie_admin/" + MediaUrl)
        } else {
            videoUrl =
                Uri.parse(MediaUrl)
        }

// val videoUrl =
//            Uri.parse("http://pixeldev.in/webservices/movie_app/movie_admin/movie_video/endgame.mp4")

        val loadControl: LoadControl = DefaultLoadControl()

        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

        val trackSelector: TrackSelector =
            DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))

        simpleExoPlayer =
            ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)

        val defaultHttpDataSourceFactory =
            DefaultHttpDataSourceFactory("exoplayer_video")
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        val mediaSource: MediaSource = ExtractorMediaSource(
            videoUrl,
            defaultHttpDataSourceFactory,
            extractorsFactory,
            null,
            null
        )
        playerView!!.setPlayer(simpleExoPlayer)
        playerView!!.keepScreenOn = true
        simpleExoPlayer?.prepare(mediaSource)
        simpleExoPlayer?.setPlayWhenReady(true)
        simpleExoPlayer?.addListener(object : Player.EventListener {
            override fun onTimelineChanged(
                timeline: Timeline,
                manifest: Any?,
                reason: Int
            ) {
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar!!.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    progressBar!!.visibility = View.GONE
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlayerError(error: ExoPlaybackException) {}
            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {}
        })
        btFullScreen!!.setOnClickListener(View.OnClickListener {
            if (flag) {
                btFullScreen!!.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen))
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                flag = false
                return@OnClickListener
            }
            playerView?.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            btFullScreen!!.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen_exit))
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            flag = true
        })
    }

    /* access modifiers changed from: protected */
    override fun onPause() {
        super.onPause()
        simpleExoPlayer!!.playWhenReady = false
        simpleExoPlayer!!.playbackState
    }

    /* access modifiers changed from: protected */
    override fun onRestart() {
        super.onRestart()
        simpleExoPlayer!!.playWhenReady = true
        simpleExoPlayer!!.playbackState
    }

    override fun onResume() {
        super.onResume()
        simpleExoPlayer!!.playWhenReady = true
        simpleExoPlayer!!.playbackState
        //Makes sure that the media controls pop up on resuming and when going between PIP and non-PIP states.
        playerView?.useController = true
    }

    override fun onStop() {
        super.onStop()
        playerView?.player = null
        simpleExoPlayer?.release()
        //PIPmode activity.finish() does not remove the activity from the recents stack.
        //Only finishAndRemoveTask does this.
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) {
            finishAndRemoveTask()
        }
    }

    private fun pictureInPictureMode() {
        //Requires Android O and higher
        Log.d(TAG, "pictureInPictureMode: Try to enter in PIP mode")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "pictureInPictureMode: Supports PIP")
            //setup PIP height width
            val aspectRatio = playerView?.height?.let { Rational(playerView!!.width, it) }
            pictureInPictureParamsBuilder!!.setAspectRatio(aspectRatio).build()
            enterPictureInPictureMode(pictureInPictureParamsBuilder!!.build())

        } else {
            Log.d(TAG, "pictureInPictureMode: Doesn't supports PIP")
            Toast.makeText(this, "Your device doesn't supports PIP", Toast.LENGTH_LONG).show()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        //when user presses home button, if not in PIP mode, enter in PIP, requires Android N and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "onUserLeaveHint: was not in PIP")
            pictureInPictureMode()
        } else {
            Log.d(TAG, "onUserLeaveHint: Already in PIP")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            Log.d(TAG, "onPictureInPictureModeChanged: Entered PIP")
            //hid pip button and actionbar
            pipBtn?.visibility = View.GONE
            Handler().postDelayed({ checkPIPPermission() }, 30)
            simpleExoPlayer!!.playWhenReady = true
            simpleExoPlayer!!.playbackState

        } else {
            Log.d(TAG, "onPictureInPictureModeChanged: Exited PIP")
            pipBtn?.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkPIPPermission() {
        isPIPModeeEnabled = isInPictureInPictureMode
        if (!isInPictureInPictureMode) {
            onBackPressed()
        }
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