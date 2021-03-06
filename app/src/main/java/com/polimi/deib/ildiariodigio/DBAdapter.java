package com.polimi.deib.ildiariodigio;

/**
 * Created by marc on 26/02/16.
 */

        import java.net.URI;
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
    private static final int DATABASE_VERSION = 6; // ¿?

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
            db.execSQL("create table if not exists profiles (id integer primary key autoincrement, name VARCHAR not null, type integer, photo_path VARHCAR)");
            db.execSQL("create table if not exists diario (id integer primary key autoincrement, path VARCHAR not null, name VARCHAR, description VARCHAR, date VARCHAR)");

            // INIT TABLE PROFILES
            ContentValues initialValues = new ContentValues();
            initialValues.put("name", "Nome dil bambino");
            initialValues.put("type", 0);
            initialValues.put("photo_path", "null");
            db.insert("profiles", null, initialValues);
            initialValues = new ContentValues();
            initialValues.put("name", "Nome dil genitore");
            initialValues.put("type", 1);
            initialValues.put("photo_path", "null");
            db.insert("profiles", null, initialValues);
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
        return  db.query("songs", new String[]{"id", "title", "path", "duration"}, null, null, null, null, null);
    }

    public int addSong(String title, String path, int duration) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("title", title);
        initialValues.put("path", path);
        initialValues.put("duration", duration);
        return (int)db.insert("songs", null, initialValues);
    }

    public boolean deleteSong(int id) {
        return db.delete("songs",  "id = " + id, null) > 0;
    }

    public void changeSongTitle(int id, String new_title) {
        ContentValues newValues = new ContentValues();
        newValues.put("title", new_title);
        db.update("songs", newValues, "id = " + id, null);
    }
    // </SONGS>




    // <VIDEOS>
    public Cursor getAllVideos() {
        return  db.query("videos", new String[]{"id", "title", "path", "duration"}, null, null, null, null, null);
    }

    public int addVideo(String title, String path, int duration) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("title", title);
        initialValues.put("path", path);
        initialValues.put("duration", duration);

        return (int)db.insert("videos", null, initialValues);
    }

    public boolean deleteVideo(int id) {
        return db.delete("videos",  "id = " + id, null) > 0;
    }

    public void changeVideoTitle(int id, String new_title) {
        ContentValues newValues = new ContentValues();
        newValues.put("title", new_title);
        db.update("videos", newValues, "id = " + id, null);
    }
    // </VIDEOS>




    // <PROFILES>
    public void setChildrenName(String name) {
        ContentValues newValues = new ContentValues();
        newValues.put("name", name);
        db.update("profiles", newValues, "type = 0", null);
    }

    public void setParentName(String name) {
        ContentValues newValues = new ContentValues();
        newValues.put("name", name);
        db.update("profiles", newValues, "type = 1", null);
    }

    public String getChildrenName() {
        String query ="SELECT name FROM profiles WHERE type = 0";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.e("TAG", "INSIDE: " + c.getString(0));
        return c.getString(0);
    }

    public String getParentName() {
        String query ="SELECT name FROM profiles WHERE type = 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return c.getString(0);
    }

    public void setProfilePhotoChildren(String photo) {
        ContentValues newValues = new ContentValues();
        newValues.put("photo_path", photo);
        db.update("profiles", newValues, "type = 0", null);
    }

    public void setProfilePhotoParent(String photo) {
        ContentValues newValues = new ContentValues();
        newValues.put("photo_path", photo);
        db.update("profiles", newValues, "type = 1", null);
    }

    public String getProfilePhotoChildren() {
        String query ="SELECT photo_path FROM profiles WHERE type = 0";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return c.getString(0);
    }

    public String getProfilePhotoParent() {
        String query ="SELECT photo_path FROM profiles WHERE type = 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return c.getString(0);
    }
    // </PROFILES>


    // <DIARIO>
    public int addPhoto(String path, String name, String description, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("path", path);
        initialValues.put("name", name);
        initialValues.put("description", description);
        initialValues.put("date", date);
        return (int)db.insert("diario", null, initialValues);
    }

    public void deletePhoto(int id) {
        db.delete("videos", "id = " + id, null);
        db.delete("diario", "id = " + id, null);
    }

    public void updatePhoto(int id, String path, String name, String description, String date) {
        ContentValues newValues = new ContentValues();
        newValues.put("path", path);
        newValues.put("name", name);
        newValues.put("description", description);
        newValues.put("date", date);
        db.update("diario", newValues, "id = " + id, null);
    }

    public Cursor getPhoto(int id) {
        String query ="SELECT name, path, description, date FROM diario WHERE id = " + Integer.toString(id);
        return db.rawQuery(query, null);
    }

    public Cursor getAllPhotos() {
        String query ="SELECT id, path, date, name FROM diario ORDER BY datetime(date)";
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    public void deleteAllPhotos() {
        //db.delete("diario", null, null);
        db.rawQuery("DELETE FROM diario", null);
    }
    // </DIARIO>
}