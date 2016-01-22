package com.example.shihjie_sun.voicecontroller_beta.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by ShihJie_Sun on 2015/12/23.
 */
public class BackupContentProvider extends ContentProvider {

    private MyDBHelper dbHelper;

    private static final int ALL_USERS = 1;
    private static final int SINGLE_USER = 2;

    private static final String AUTHORITY = "com.example.shihjie_sun.voicecontroller_beta.Database.contentprovider";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/users");

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "users", ALL_USERS);
        uriMatcher.addURI(AUTHORITY, "users/#", SINGLE_USER);
    }

    @Override
    public boolean onCreate() {
        // get access to the database helper
        dbHelper = new MyDBHelper(getContext(), "mydata.db", null, 4);
        return false;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case ALL_USERS:
                return "vnd.android.cursor.dir/vnd.com.example.shihjie_sun.voicecontroller_beta.Database.contentprovider.users";
            case SINGLE_USER:
                return "vnd.android.cursor.item/vnd.com.example.shihjie_sun.voicecontroller_beta.Database.contentprovider.users";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_USERS:
                //do nothing
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long id = db.insert(BackupDAO.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI + "/" + id);
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(BackupDAO.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ALL_USERS:
                //do nothing
                break;
            case SINGLE_USER:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(BackupDAO.KEY_ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_USERS:
                //do nothing
                break;
            case SINGLE_USER:
                String id = uri.getPathSegments().get(1);
                selection = BackupDAO.KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int deleteCount = db.delete(BackupDAO.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_USERS:
                //do nothing
                break;
            case SINGLE_USER:
                String id = uri.getPathSegments().get(1);
                selection = BackupDAO.KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int updateCount = db.update(BackupDAO.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }


    public Cursor queryy(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // TODO Auto-generated method stub
        if (uriMatcher.match(uri) != ALL_USERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = db.query(true, BackupDAO.TABLE_NAME, projection, selection,
                null, null, null, null, null);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }
}
