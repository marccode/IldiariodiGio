package com.polimi.deib.ildiariodigio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AudioGridActivity extends AppCompatActivity {

    private MediaManager song_manager;
    private ArrayList<HashMap<String, String>> all_songs = new ArrayList<HashMap<String, String>>();

    private String title_selected;
    private String duration_selected;
    private String path_selected;
    private  boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_grid);

        selected = false;

        TextView tv = (TextView) findViewById(R.id.textview_activity_title);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto/Roboto-Bold.ttf");
        tv.setTypeface(tf);
        tv.setTextColor(getResources().getColor(R.color.title_grey));

        ImageButton btnNext = (ImageButton) findViewById(R.id.imageButton_next);
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (selected) {
                    Intent i = new Intent(AudioGridActivity.this, ChronometerAudioActivity.class);
                    i.putExtra("title", title_selected);
                    i.putExtra("duration", duration_selected);
                    i.putExtra("path", path_selected);
                    AudioGridActivity.this.startActivity(i);
                }
            }
        });


        song_manager = new MediaManager();
        all_songs = song_manager.getAllSongs();
        if (all_songs == null) {
            Log.e("TAG", "all_songs is NULL");
        }
        else {
            Log.e("TAG", "all_songs is NOT NULL");
        }

        GridView grid = (GridView) findViewById(R.id.grid_audios);
        grid.setAdapter(new GridAdapter(this));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //do some stuff here on click
            }
        });
    }

    public class GridAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater inflater = null;

        ArrayList<String> times = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> paths = new ArrayList<String>();


        public GridAdapter(Context c) {
            mContext = c;
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            DBAdapter db = new DBAdapter(mContext);
            db.open();
            Cursor cursor = db.getAllSongs();
            if (cursor != null) {
                cursor.moveToFirst();
            }
            while (!cursor.isAfterLast()) {
                names.add(cursor.getString(0));
                paths.add(cursor.getString(1));
                times.add(miliseconds_to_string(cursor.getInt(2)));
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {

            final View rowView;

            if (position < names.size()) {
                rowView = inflater.inflate(R.layout.audio_button_layout, null);
                TextView time_textView = (TextView) rowView.findViewById(R.id.audio_button_time_textView);
                TextView name_textView = (TextView) rowView.findViewById(R.id.audio_button_name_textView);
                Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Static/Static.otf");
                time_textView.setTypeface(tf);
                name_textView.setTypeface(tf);

                time_textView.setTextColor(getResources().getColor(R.color.white));
                name_textView.setTextColor(getResources().getColor(R.color.title_grey));

                time_textView.setText(times.get(position));
                name_textView.setText(names.get(position));

                RelativeLayout rl = (RelativeLayout) rowView.findViewById(R.id.relativelayout_audio_button);
                rl.setBackground(mContext.getResources().getDrawable(R.drawable.audio_button));
                //rl.setBackground(mContext.getResources().getDrawable(R.drawable.audio_button_selected));


                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        title_selected = names.get(position);
                        duration_selected = times.get(position);
                        path_selected = paths.get(position);
                        selected = true;
                        //RelativeLayout rl2 = (RelativeLayout) rowView.findViewById(R.id.relativelayout_audio_button);
                        //rl2.setBackground(mContext.getResources().getDrawable(R.drawable.audio_button_selected));
                    }
                });


                rowView.setOnLongClickListener(new View.OnLongClickListener() {

                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(AudioGridActivity.this);
                        alert.setTitle("Eliminare canzone");
                        alert.setMessage("Vuoi eliminare la canzone \"" + names.get(position) + "\"?");
                        alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBAdapter db = new DBAdapter(mContext);
                                db.open();
                                db.deleteSong(names.get(position));
                                db.close();
                                names.remove(position);
                                times.remove(position);
                                paths.remove(position);
                                dialog.dismiss();
                                notifyDataSetChanged();

                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alert.show();
                        return true;
                    }
                });
            }
            else {

                rowView = inflater.inflate(R.layout.add_item_layout, null);
                RelativeLayout rl = (RelativeLayout) rowView.findViewById(R.id.relativelayout_audio_button);
                rl.setBackground(mContext.getResources().getDrawable(R.drawable.audio_button));
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplicationContext(), "ADD SONG", Toast.LENGTH_SHORT).show();

                        // Create Dialog:
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AudioGridActivity.this);
                        //builderSingle.setIcon(R.drawable.ic_launcher);
                        builderSingle.setTitle("Seleziona una canzone");

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                AudioGridActivity.this,
                                android.R.layout.simple_selectable_list_item);

                        for (int i = 0; i < all_songs.size(); ++i) {
                            arrayAdapter.add(all_songs.get(i).get("songTitle"));
                            //mp.setDataSource(songsList.get(songIndex).get("songPath"));
                        }

                        builderSingle.setNegativeButton(
                                "cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builderSingle.setAdapter(
                                arrayAdapter,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //String strName = arrayAdapter.getItem(which);
                                        String title = all_songs.get(which).get("songTitle");
                                        String path = all_songs.get(which).get("songPath");
                                        // Find Duration:
                                        Uri uri = Uri.parse(path);
                                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                                        mmr.setDataSource(getApplicationContext(), uri);
                                        int duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                                        // Add to Database

                                        DBAdapter db = new DBAdapter(mContext);
                                        db.open();
                                        db.addSong(title, path, duration);
                                        db.close();
                                        names.add(title);
                                        times.add(miliseconds_to_string(duration));
                                        paths.add(path);
                                        notifyDataSetChanged();
                                    }
                                });
                        builderSingle.show();
                    }
                });
            }

            rowView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 160));
            return rowView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        public final int getCount() {
            return names.size() + 1;
        }

        public final long getItemId(int position) {
            return position;
        }

        private String miliseconds_to_string(int milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int)( milliseconds / (1000*60*60));
            int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
            int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
            // Add hours if there
            if(hours > 0){
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if(seconds < 10){
                secondsString = "0" + seconds;
            }else{
                secondsString = "" + seconds;}

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }
    }
}