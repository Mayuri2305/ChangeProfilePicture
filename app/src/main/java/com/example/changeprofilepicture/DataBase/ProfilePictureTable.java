package com.example.changeprofilepicture.DataBase;

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.changeprofilepicture.Data.ProfilePicture;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;
import static android.R.attr.value;

/**
 * Created by mayuri on 26/8/17.
 */


public class ProfilePictureTable extends DatabaseHandler {

    private static final String TAG = "Profile Picture Table";
    private static final String TABLE = "profilePicture";

    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE = "image";
   ;


    public ProfilePictureTable(Context context) {
        super(context);
    }


    public static String createTable() {
        String sql = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY , "
                + KEY_IMAGE + " TEXT "
                + ")";
        return sql;
    }

    public static String dropTable() {
        String sql = "DROP TABLE IF EXISTS " + TABLE;
        return sql;
    }

    public boolean create(ProfilePicture profilePicture) {

        boolean createSuccessful = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, profilePicture.getId());
        values.put(KEY_IMAGE, profilePicture.getImage());
        try {
            db.insert(TABLE, null, values);
            createSuccessful = true;
        } catch (Exception e) {
            createSuccessful = false;
        }
        db.close();

        return createSuccessful;
    }

    public boolean update(ProfilePicture profilePicture) {
        boolean updateSuccessful = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, profilePicture.getId());
        values.put(KEY_IMAGE, profilePicture.getImage());
        try {
            db.insert(TABLE, null, values);
            /**
             * @TODO
             * Update table
             */
            updateSuccessful = true;
        } catch (Exception e) {
            updateSuccessful = false;
        }
        db.close();

        return updateSuccessful;
    }
    public boolean delete(Integer profilePicture_id) {
        boolean deleteSuccessful = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            deleteSuccessful = db.delete(TABLE, KEY_ID + " ='" + profilePicture_id + "'", null) > 0;
            db.close();
        } catch (Exception e) {
            Log.d("DATABASE ", " PROFILE IMAGE DELETED " + e);
        }
        return deleteSuccessful;
    }

    public ProfilePicture read() {
        ProfilePicture profilePicture = new ProfilePicture();

        try {
            String sql = "SELECT * FROM " + TABLE;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    String image = cursor.getString(cursor.getColumnIndex(KEY_IMAGE));
                    profilePicture.setId(id);
                    profilePicture.setImage(image);
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.d("DATABASE ", " PROFILE IMAGE READ " + e);
        }
        return profilePicture;
    }
    public List<ProfilePicture> getAllProfilePicture(){
        List<ProfilePicture> profilePictures = new ArrayList<>();

        try {
            String sql = "SELECT * FROM " + TABLE ;
            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {
                    ProfilePicture profilePicture = null;
                    int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    String image = cursor.getString(cursor.getColumnIndex(KEY_IMAGE));
                    profilePicture=new ProfilePicture();
                    profilePicture.setId(id);
                    profilePicture.setImage(image);

                    profilePictures.add(profilePicture);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.d("DATABASE ", "PROFILE PICTURES READ " + e);
        }
        return profilePictures;
    }
}
