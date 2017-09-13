package com.example.changeprofilepicture.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.changeprofilepicture.Data.ProfilePicture;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler sInstance;
    private static final int DATABASE_VERSION = 1;

    protected static final String DATABASE_NAME = "ChangeProfilePicture";
    public static synchronized DatabaseHandler getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProfilePictureTable.createTable());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(ProfilePictureTable.dropTable());

        onCreate(db);
    }

    public void resetAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(ProfilePictureTable.dropTable());
        db.close();
    }
}
