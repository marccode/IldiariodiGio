package com.polimi.deib.ildiariodigio;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.polimi.deib.ildiariodigio.R;


public class ChronometerVideoActivity extends Activity implements SurfaceHolder.Callback {

    private ImageButton btnPlay;
    private ImageButton btnBack;

    private TextView videoTitle;
    private TextView countndown_textview;
    private TextView min_textview;

    private SurfaceView surface;
    private SurfaceHolder holder;
    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private SongsManager songManager;
    private Utilities utils;


    String video_title;
    String video_duration;
    String video_path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer_video);


        Intent i = getIntent(); // gets the previously created intent
        video_title = i.getStringExtra("title");
        video_duration= i.getStringExtra("duration");
        video_path = i.getStringExtra("path");

        /*
        video_title = "Julia Balla";
        video_duration = "10";
        video_path = "/storage/sdcard/video.webm";
        */

        // All player buttons
        btnPlay = (ImageButton) findViewById(R.id.imageButton_play);
        btnBack = (ImageButton) findViewById(R.id.imageButton_back);

        // Title
        videoTitle = (TextView) findViewById(R.id.textView_video_title);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto/Roboto-Bold.ttf");
        videoTitle.setTypeface(tf);
        videoTitle.setTextColor(getResources().getColor(R.color.title_grey));
        videoTitle.setText(video_title);

        // Time
        countndown_textview = (TextView) findViewById(R.id.textView_countdown);
        tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Static/Static-Bold.otf");
        countndown_textview.setTypeface(tf);
        countndown_textview.setTextColor(getResources().getColor(R.color.orange));

        // MIN
        min_textview = (TextView) findViewById(R.id.textView_min);
        //tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Static/Static.otf");
        min_textview.setTextColor(getResources().getColor(R.color.light_grey));
        min_textview.setTypeface(tf);

        surface = (SurfaceView) findViewById(R.id.surface);
        holder = surface.getHolder();
        holder.addCallback(this);

        //holder.setFixedSize(400, 300);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Mediaplayer
        mp = new MediaPlayer();
        songManager = new SongsManager();
        utils = new Utilities();

        // Listeners
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                //Toast.makeText(getApplicationContext(), "VIDEO ACABADA", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ChronometerVideoActivity.this, BravoActivity.class);
                i.putExtra("chronometer_type", "video");
                i.putExtra("title", video_title);
                i.putExtra("duration", video_duration);
                i.putExtra("path", video_path);
                ChronometerVideoActivity.this.startActivity(i);
            }
        });

        // Important

        // Getting all songs list
        //songsList = songManager.getPlayList();

        // By default play first song
        //playVideo();

        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.button_play);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.button_pause);
                    }
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mp.release();
                Intent i = new Intent(ChronometerVideoActivity.this, VideoGridActivity.class);
                ChronometerVideoActivity.this.startActivity(i);
                //finish();
            }
        });


        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        /*
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });
        */

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        /*
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });
        */

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        /*
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });
        */

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        /*
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });
        */

        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        /*
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });
        */

        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        /*
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });
        */

        /**
         * Button Click event for Play list click event
         * Launches list activity which displays list of songs
         * */
        /*
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });
        */

    }

    /**
     * Receiving song index from playlist view
     * and play the song
     * */
    /*
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }
    */

    public void playVideo() {

    }

    /**
     * Update timer on seekbar
     */
    /*
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    */

    public void updateTimer() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            //songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            countndown_textview.setText("" + utils.milliSecondsToTimer(totalDuration - currentDuration));

            // Updating progress bar
            //int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            //songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    /*
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }
    */

    /**
     * When user starts moving the progress handler
     */
    /*
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
    */

    /**
     * When user stops moving the progress hanlder
     */
    /*
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        //updateProgressBar();
    }
    */
    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    /*
    public void onCompletion(MediaPlayer arg0) {
        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }
    */

    public void preparePlayer() {
        try {
            mp.reset();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();
                    float videoProportion = (float) videoWidth / (float) videoHeight;


                    // Get the width of the screen
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    float screenProportion = (float) screenWidth / (float) screenHeight;

                    Log.e("E", "Video Proportion: " + Float.toString(videoProportion));
                    Log.e("E", "Screen Proportion: " + Float.toString(screenProportion));


                    // Get the SurfaceView layout parameters
                    android.view.ViewGroup.LayoutParams lp = surface.getLayoutParams();
                    if (videoProportion > screenProportion) {
                        lp.width = screenWidth;
                        lp.height = (int) ((float) screenWidth / videoProportion);
                    } else {
                        lp.width = (int) (videoProportion * (float) screenHeight);
                        lp.height = screenHeight;
                    }

                    // Commit the layout parameters
                    surface.setLayoutParams(lp);

                    btnPlay.setImageResource(R.drawable.button_pause);
                    updateTimer();
                    mp.start();
                }
            });
            mp.setDataSource(video_path);



            //holder.setFixedSize(lp.width, lp.height);
            //holder.setFixedSize(400, 300);
            mp.setDisplay(holder);

            // ----

            mp.prepareAsync();
            // Displaying Song title
            //String songTitle = songsList.get(songIndex).get("songTitle");
            //songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image

            // set Progress bar values


            // Updating progress bar
            //);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        preparePlayer();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

}