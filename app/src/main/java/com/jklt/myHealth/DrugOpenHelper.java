package com.jklt.myHealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DrugOpenHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "videosDatabase.db";
    static final int DATABASE_VERSION = 1;

    static final String DRUGS_TABLE = "tableContacts";
    static final String ID = "_id";
    static final String NAME = "title";
    static final String DESCRIPTION = "description";

    public DrugOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "CREATE TABLE " + DRUGS_TABLE +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT, " +
                DESCRIPTION + " TEXT )";
        Log.d(MainActivity.TAG, "onCreate: " + sqlCreate);
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertVideo(Drug drug) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, drug.getName());
        contentValues.put(DESCRIPTION, drug.getDescription());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(DRUGS_TABLE, null, contentValues);
        db.close();
    }



    public Cursor getSelectAllCursor() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DRUGS_TABLE, new String[]{ID,
                        NAME,
                        DESCRIPTION},
                null, null, null, null, null);
        return cursor;
    }

    public ArrayList<Drug> getSelectAllVideos() {
        ArrayList<Drug> drugs = new ArrayList<>();
        Cursor cursor = getSelectAllCursor();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            Drug drug = new Drug(id, name, description);
            drugs.add(drug);
        }
        return drugs;
    }

    public Drug getSelectVideoById(int idParam) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DRUGS_TABLE, new String[]{ID,
                        NAME,
                        DESCRIPTION,
                        },
                ID + "=?", new String[]{"" + idParam}, null, null, null);
        Drug drug = null;
        if (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            drug = new Drug(id, name, description);
        }
        return drug;
    }

    public Boolean deleteContactById(int idParam) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DRUGS_TABLE, new String[]{ID,
                        NAME,
                        DESCRIPTION,
                        },
                ID + "=?", new String[]{"" + idParam}, null, null, null);
        Drug drug = null;
        if (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            db.delete(DRUGS_TABLE, ID + " = ?", new String[]{Long.toString(id)} );
            db.close();
            return true;
        }
        return false;
    }


    public void deleteAllContacts() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DRUGS_TABLE, null, null);
        db.close();
    }
}
