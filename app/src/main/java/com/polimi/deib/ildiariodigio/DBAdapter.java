package com.polimi.deib.ildiariodigio;

/**
 * Created by marc on 26/02/16.
 */

        import java.sql.SQLException;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;
//import android.util.Log;

public class DBAdapter {
    public static final String KEY_ROWID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EXP_DATE = "exp_date";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_PRICE = "price";
    public static final String KEY_NOTES = "notes";

    private static final String DATABASE_NAME = "IlDiarioDiGioDB";
    //private static final String DATABASE_TABLE = "songs";
    private static final int DATABASE_VERSION = 4; // ¿?

    //private static final String DATABASE_CREATE =
    //        "create table if not exists songs (id integer primary key autoincrement, title VARCHAR not null, path VARCHAR, duration integer);"
    //                + "create table if not exists videos (id integer primary key autoincrement, title VARCHAR not null, path VARCHAR, duration integer);";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context c) {
        context = c;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //db.execSQL(DATABASE_CREATE);
            db.execSQL("create table if not exists songs (id integer primary key autoincrement, title VARCHAR not null, path VARCHAR, duration integer)");
            db.execSQL("create table if not exists videos (id integer primary key autoincrement, title VARCHAR not null, path VARCHAR, duration integer)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS videos");
            db.execSQL("DROP TABLE IF EXISTS songs");
            onCreate(db);
        }
    }


    public DBAdapter open(){ //throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }


    /*

    public long insert(String name, String exp_date, String string, String category, String notes) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EXP_DATE, exp_date);
        initialValues.put(KEY_PRICE, string);
        initialValues.put(KEY_CATEGORY, category);
        initialValues.put(KEY_NOTES, notes);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean delete(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean update(long rowId, String name, String exp_date, float price, String category, String notes) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_EXP_DATE, exp_date);
        newValues.put(KEY_PRICE, price);
        newValues.put(KEY_CATEGORY, category);
        newValues.put(KEY_NOTES, notes);

        return db.update(DATABASE_TABLE, newValues, KEY_ROWID + "=" + rowId, null) >0;
    }

    public Cursor get(long rowId) //throws SQLException
    {
        Cursor c =
                db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_EXP_DATE, KEY_PRICE, KEY_CATEGORY, KEY_NOTES},
                        KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllRecords() {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_EXP_DATE, KEY_PRICE, KEY_CATEGORY, KEY_NOTES}, null, null, null, null, null);
    }

    */

    // <SONGS>
    public Cursor getAllSongs() {
        return  db.query("songs", new String[] {"title", "path", "duration"}, null, null, null, null, null);
    }

    public long addSong(String title, String path, int duration) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("title", title);
        initialValues.put("path", path);
        initialValues.put("duration", duration);

        return db.insert("songs", null, initialValues);
    }

    public boolean deleteSong(String title) {
        return db.delete("songs",  "title = \"" + title + "\"", null) > 0;
    }
    // </SONGS>

    // <VIDEOS>
    public Cursor getAllVideos() {
        return  db.query("videos", new String[] {"title", "path", "duration"}, null, null, null, null, null);
    }

    public long addAudio(String title, String path, int duration) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("title", title);
        initialValues.put("path", path);
        initialValues.put("duration", duration);

        return db.insert("videos", null, initialValues);
    }

    public boolean deleteVideo(String title) {
        return db.delete("videos",  "title = \"" + title + "\"", null) > 0;
    }
    // </VIDEOS>
}