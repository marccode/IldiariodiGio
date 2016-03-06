package com.polimi.deib.ildiariodigio;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiarioGridActivity extends AppCompatActivity {

    ImageButton back;

    GenericModelAdapter adapter;
    ListView listView;
    private static final int NUMBER_OF_COLS = 4;
    List<Map<String, List<Object>>> items = new ArrayList<Map<String, List<Object>>>();
    Map<String, String> sectionHeaderTitles = new HashMap<String, String>();

    ArrayList<Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario_grid);

        photos = new ArrayList<Photo>();
        DBAdapter db = new DBAdapter(getApplicationContext());
        db.open();
        Cursor c = db.getAllPhotos();
        if (c != null) {
            c.moveToFirst();
        }
        while (!c.isAfterLast()) {
            Log.e("TAG", Integer.toString(c.getInt(0)) + " ,PATH: " + c.getString(1) + " ,DATE: " + c.getString(2));
            Photo p = new Photo(c.getInt(0), c.getString(1), c.getString(2));
            photos.add(p);
            c.moveToNext();
        }


        back = (ImageButton)findViewById(R.id.imageButton_back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiarioGridActivity.this, HomeActivity.class);
                DiarioGridActivity.this.startActivity(intent);
            }
        });

        adapter = new GenericModelAdapter(this,R.layout.list_item, items, sectionHeaderTitles, NUMBER_OF_COLS, mItemClickListener);
        if (adapter == null) {
            Log.e("TAG", "adapter is null");
        }
        listView = (ListView)findViewById(R.id.listView);
        if (listView == null) {
            Log.e("TAG", "listview is null");
        }
        listView.setAdapter(adapter);
    }

    View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //int position = (Integer)v.getTag(R.id.row);
            //int col = (Integer)v.getTag(R.id.col);

            //Map<String, List<Object>> map = adapter.getItem(position);
            //String selectedItemType = adapter.getItemTypeAtPosition(position);
            //List<Object> list = map.get(selectedItemType);
            //GenericModel model = (GenericModel)list.get(col);
            //Toast.makeText(getApplicationContext(), "" + model.getHeader(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "clikclicklick", Toast.LENGTH_SHORT).show();
        }
    };

    private class Photo {
        public int id;
        public String date;
        public String path;

        public Photo(int id, String path, String date) {
            this.id = id;
            this.date = date;
            this.path = path;
        }
    }

    public class GenericModelAdapter extends ArrayAdapter<Map<String, List<Object>>> {

        List<Map<String, List<Object>>> items = new ArrayList<Map<String, List<Object>>>();
        int numberOfCols;
        List<String> headerPositions = new ArrayList<String>();
        Map<String, String> itemTypePositionsMap = new LinkedHashMap<String, String>();
        Map<String, Integer> offsetForItemTypeMap = new LinkedHashMap<String, Integer>();
        LayoutInflater layoutInflater;
        View.OnClickListener mItemClickListener;
        Map<String, String> sectionHeaderTitles;

        // <JO>
        Date last_date;

        ArrayList<ArrayList<Photo>> days;
        boolean header;
        int aux;
        // </JO>

        public GenericModelAdapter(Context context, int textViewResourceId, List<Map<String, List<Object>>> items, int numberOfCols, View.OnClickListener mItemClickListener){
            this(context, textViewResourceId, items, null, numberOfCols, mItemClickListener);
        }

        public GenericModelAdapter(Context context, int textViewResourceId, List<Map<String, List<Object>>> items, Map<String, String> sectionHeaderTitles, int numberOfCols, View.OnClickListener mItemClickListener){
            super(context, textViewResourceId, items);
            this.items = items;
            this.numberOfCols = numberOfCols;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mItemClickListener = mItemClickListener;
            this.sectionHeaderTitles = sectionHeaderTitles;

            // <JO>
            aux = 1;
            header = true;
            try {
                last_date = new SimpleDateFormat("yyyy-MM-dd").parse(photos.get(0).date);
            }catch(java.text.ParseException e) {
                e.printStackTrace();
            }
            days = new ArrayList<ArrayList<Photo>>();
            days.add(new ArrayList<Photo>());
            int j = 0;
            int size = photos.size();
            for (int i = 0; i < size; ++i) {
                Date d = new Date();
                try {
                    d = new SimpleDateFormat("yyyy-MM-dd").parse(photos.get(i).date);
                } catch(java.text.ParseException e) {
                    e.printStackTrace();
                }
                if (d.after(last_date)) {
                    days.add(new ArrayList<Photo>());
                    ++j;
                }
                Photo p = photos.get(i);
                days.get(j).add(p);
            }

            for (int i = 0; i < days.size(); ++i) {
                Log.e("TAG", Integer.toString(days.get(i).size()));
            }
            // </JO>
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            // <JO>
            if (header) {
                convertView = layoutInflater.inflate(R.layout.grid_header_view, null);
                TextView headerText = (TextView)convertView.findViewById(R.id.headerText);
                String date_string = getDateString(days.get(position).get(0).date);
                headerText.setText(date_string);
                header = false;
                return convertView;
            }
            else {
                LinearLayout row = (LinearLayout)layoutInflater.inflate(R.layout.row_item, null);

                Map<String, List<Object>> map = getItem(position);
                List<Object> list = map.get(getItemTypeAtPosition(position));

                int size_day = days.get(position - aux).size();
                for (int i = 0; i < size_day; i++){
                    FrameLayout grid = (FrameLayout)layoutInflater.inflate(R.layout.grid_item, row, false);
                    ImageView imageView;
                    if (i < list.size()){
                        if (grid != null){
                            imageView = (ImageView)grid.findViewWithTag("image");
                            File imgFile = new  File(days.get(position - aux).get(i).path);

                            if(imgFile.exists()){

                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                                imageView.setImageBitmap(myBitmap);

                            }

                            TextView textView = (TextView)grid.findViewWithTag("subHeader");
                            textView.setText(Integer.toString(days.get(position - aux).get(i).id));

                            //grid.setTag(R.id.row, position);
                            //grid.setTag(R.id.col, i);
                            grid.setOnClickListener(mItemClickListener);
                        }
                    }
                    else {
                        if (grid != null){
                            grid.setVisibility(View.INVISIBLE);
                            grid.setOnClickListener(null);
                        }
                    }
                    row.addView(grid);
                }
                header = true;
                ++aux;
                return row;
            }
        }

        private String getDateString(String date) {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            String day_string = date.substring(8, 10);

            String month_string;
            switch (month) {
                case 1:
                    month_string = "gennaio";
                    break;

                case 2:
                    month_string = "febbraio";
                    break;

                case 3:
                    month_string = "marzo";
                    break;

                case 4:
                    month_string = "marzo";
                    break;

                case 5:
                    month_string = "maggio";
                    break;

                case 6:
                    month_string = "giugno";
                    break;

                case 7:
                    month_string = "luglio";
                    break;

                case 8:
                    month_string = "agosto";
                    break;

                case 9:
                    month_string = "settembre";
                    break;

                case 10:
                    month_string = "ottobre";
                    break;

                case 11:
                    month_string = "novembre";
                    break;

                case 12:
                    month_string = "dicembre";
                    break;

                default:
                    month_string = "unknown";
            }

            String year_string = "";
            Date now = new Date();
           if (now.getYear() != year) {
                year_string = " " + Integer.toString(year);
            }

            Date d = new Date();
            try {
                d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch(java.text.ParseException e) {
                e.printStackTrace();
            }
            String weekday_string = "Oggi";
            if (!now.equals(d)) {
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                int weekday = c.get(Calendar.DAY_OF_WEEK);

                switch (weekday) {
                    case 1:
                        weekday_string = "domenica";
                        break;

                    case 2:
                        weekday_string = "lunedì";
                        break;

                    case 3:
                        weekday_string = "martedì";
                        break;

                    case 4:
                        weekday_string = "mercoledì";
                        break;

                    case 5:
                        weekday_string = "giovedì";
                        break;

                    case 6:
                        weekday_string = "venerdì";
                        break;

                    case 7:
                        weekday_string = "sabato";
                        break;
                }
            }

            return weekday_string + ", " + day_string + " " + month_string + year_string;
        }

        @Override
        public int getCount() {
            int totalItems = 0;
            for (Map<String, List<Object>> map : items){
                Set<String> set = map.keySet();
                for(String key : set){
                    //calculate the number of rows each set homogeneous grid would occupy
                    List<Object> l = map.get(key);
                    int rows = l.size() % numberOfCols == 0 ? l.size() / numberOfCols : (l.size() / numberOfCols) + 1;

                    // insert the header position
                    if (rows > 0){
                        headerPositions.add(String.valueOf(totalItems));
                        offsetForItemTypeMap.put(key, totalItems);

                        itemTypePositionsMap.put(key, totalItems + "," + (totalItems + rows) );
                        totalItems += 1; // header view takes up one position
                    }
                    totalItems+= rows;
                }
            }
            return totalItems;
        }

        @Override
        public Map<String, List<Object>> getItem(int position) {
            if (!isHeaderPosition(position)){
                String itemType = getItemTypeAtPosition(position);
                List<Object> list = null;
                for (Map<String, List<Object>> map : items) {
                    if (map.containsKey(itemType)){
                        list = map.get(itemType);
                        break;
                    }
                }
                if (list != null){
                    int offset = position - getOffsetForItemType(itemType);
                    //remove header position
                    offset -= 1;
                    int low = offset * numberOfCols;
                    int high = low + numberOfCols  < list.size() ? (low + numberOfCols) : list.size();
                    List<Object> subList = list.subList(low, high);
                    Map<String, List<Object>> subListMap = new HashMap<String, List<Object>>();
                    subListMap.put(itemType, subList);
                    return subListMap;
                }
            }
            return null;
        }

        public String getItemTypeAtPosition(int position){
            String itemType = "Unknown";
            Set<String> set = itemTypePositionsMap.keySet();

            for(String key : set){
                String[] bounds = itemTypePositionsMap.get(key).split(",");
                int lowerBound = Integer.valueOf(bounds[0]);
                int upperBoundary = Integer.valueOf(bounds[1]);
                if (position >= lowerBound && position <= upperBoundary){
                    itemType = key;
                    break;
                }
            }
            return itemType;
        }

        public int getOffsetForItemType(String itemType){
            return offsetForItemTypeMap.get(itemType);
        }

        public boolean isHeaderPosition(int position){
            return headerPositions.contains(String.valueOf(position));
        }

        private String getHeaderForSection(String section){
            if (sectionHeaderTitles != null){
                return sectionHeaderTitles.get(section);
            }else{
                return section;
            }
        }

    }

}
