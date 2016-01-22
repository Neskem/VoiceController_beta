package com.example.shihjie_sun.voicecontroller_beta.Database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by ShihJie_Sun on 2015/12/23.
 */
public class BackupDAO {

    public static final String KEY_ID  = "_id";
    public static final String DATETIME_COLUMN = "datetime";
    public static final String COLOR_COLUMN = "color";
    public static final String TITLE_COLUMN = "title";
    public static final String CONTENT_COLUMN = "content";

    public static final String TABLE_NAME = "Backup";
    private static final String LOG_TAG = "Backup";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                    KEY_ID + " integer PRIMARY KEY autoincrement," +
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +
                    COLOR_COLUMN + " INTEGER NOT NULL, " +
                    TITLE_COLUMN + " TEXT NOT NULL, " +
                    CONTENT_COLUMN + " TEXT NOT NULL)";

    public static void onCreate (SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}


