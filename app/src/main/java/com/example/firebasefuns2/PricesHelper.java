package com.example.firebasefuns2;

/**
 * @author Lin Ai Tan
 * @version v1.0 11/23/2021
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class helps connect and perform database querees
 */
public class PricesHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "pricesDatabase.db";
    static final int DATABASE_VERSION = 1;

    static final String PRICES_TABLE = "pricesDatabase";
    static final String ID = "_id"; // by convention
    static final String Seller = "Seller";
    static final String Price = "Price";
    static final String Website = "Website";
    static final String DrugName = "DrugName";

    //Constrcutor
    public PricesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     onCreate is the primary callback method which is called when the activity is created
     *
     * @param: Bundle saved instance state
     * @return
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // this where we create our database tables
        // our database will have one table to store contacts
        // AKA records AKA rows
        // this method only executes one time
        // right before the first call to getWriteableDatabase()
        String sqlCreate = "CREATE TABLE " + PRICES_TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Seller + " TEXT, " +
                Price + " DECIMAL, " +
                Website + " TEXT, " +
                DrugName + " TEXT)";
        // construct the SQL (structured query language) statement
        // and execute it
        Log.d(MainActivity.TAG, "onCreate: " + sqlCreate);
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     inserts a price into the database
     *
     * @param: new video object to be inserted
     * @return
     */
    public void insertPrice(Price p) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Seller, p.getSeller());
        contentValues.put(Price, p.getPrice());
        contentValues.put(Website, p.getWebsite());
        contentValues.put(DrugName,p.getDrug());

        // get a writeable ref to the database
        SQLiteDatabase db = getWritableDatabase();
        long returnId = db.insert(PRICES_TABLE, null, contentValues);
        int id = (int) returnId;
        p.setId(id);
        Log.d("SQL","inserted here");
        // close the writeable ref when done!!
        db.close();
    }

    // select (read of crud)
    // helper method
    /**
     returns cursor to get all results of databse
     *
     * @param:
     * @return Cursor
     */
    public Cursor getSelectAllCursor() {
        // we need to construct a query to get a cursor
        // to step through records
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PRICES_TABLE, new String[]{ID,
                        Seller,
                        Price,
                        Website,
                        DrugName},
                null, null, null,
                null, null);
        return cursor;
    }

    /**
     getSelectAllVideos returns the video database as a list of video objects
     *
     * @param:
     * @return list of videos
     */
    public List<Price> getAllPrices() {
        List<Price> prices = new ArrayList<>();
        Cursor cursor = getSelectAllCursor();
        // the cursor starts "before" the first record
        // in case there is no first record
        while (cursor.moveToNext()) { // returns false when no more records to process
            // parse the field values
            int id = cursor.getInt(0);
            String seller = cursor.getString(1);
            Double price = cursor.getDouble(2);
            String website = cursor.getString(3);
            String drug_name = cursor.getString(4);
            Price p = new Price(id, seller,price,website, drug_name);
            prices.add(p);
        }
        Log.d("SQL","" + prices.size());
        return prices;
    }

    /**
     getSelectVidepById returns a particular vidoe given its id
     *
     * @param: id of the video
     * @return vidoe object
     */
    public List<Price> getPricesByDrug(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PRICES_TABLE, new String[]{ID,
                        Seller,
                        Price,
                        Website,
                        DrugName},
                DrugName + "=?", new String[]{name}, null,
                null, Price);

        List<Price> prices = new ArrayList<>();

        while (cursor.moveToNext()) { // returns false when no more records to process
            // parse the field values
            int id = cursor.getInt(0);
            String seller = cursor.getString(1);
            Double price = cursor.getDouble(2);
            String website = cursor.getString(3);
            String drug = cursor.getString(4);
            Price p = new Price(id, seller,price,website, drug);
            prices.add(p);
        }
        return prices;
    }

    public Price getPriceById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PRICES_TABLE, new String[]{ID,
                        Seller,
                        Price,
                        Website,
                        DrugName},
                ID + "=?", new String[]{"" + id}, null,
                null, null);

        Price p = new Price();

        if (cursor.moveToNext()) { // returns false when no more records to process
            // parse the field values
            int d_id = cursor.getInt(0);
            String seller = cursor.getString(1);
            Double price = cursor.getDouble(2);
            String website = cursor.getString(3);
            String drug = cursor.getString(4);
            p = new Price(d_id, seller,price,website, drug);
        }
        return p;
    }
}
