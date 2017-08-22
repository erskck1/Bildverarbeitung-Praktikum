package com.example.bvpraktmme.kassenzettel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bvpraktmme.kassenzettel.ocr.Bill;
import com.example.bvpraktmme.kassenzettel.ocr.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Graf on 17.07.2017.
 */

public class SQliteDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    //initializing the column names
    //TODO is an id needed?
    private static final String DATABASE_NAME = "kassenzettel";
    private static final String ITEM_TABLE = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ITEM_NAME = "itemname";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DATE_TIME = "dateandtime";
    private static final String COLUMN_MARKET = "market";
    private static final String COLUMN_ADDRESS = "adress";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_PPK = "priceperkg";

    public SQliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //TODO change dateTIme to Number for easier comparison
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create the table with columns of fitting type
        String CREATE_ITEM_TABLE = " CREATE TABLE " + ITEM_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_ITEM_NAME + " TEXT," + COLUMN_PRICE + " REAL," + COLUMN_MARKET + " TEXT," + COLUMN_DATE_TIME + " TEXT," +
                COLUMN_ADDRESS + " TEXT," + COLUMN_LOCATION + " TEXT," + COLUMN_WEIGHT + " REAL," + COLUMN_PPK + " REAL)";
        db.execSQL(CREATE_ITEM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        onCreate(db);
    }

    /**
     * Adds a bill into the databse, according to the bill datastructure
     * each row is one item with all the necesary information
     * @param bill
     */
    public void addBill(Bill bill){
        SQLiteDatabase db = this.getWritableDatabase();
        for(ShoppingItem item: bill.getItems()){
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEM_NAME, item.getProduct());
            values.put(COLUMN_PRICE, item.getPrice());
            values.put(COLUMN_DATE_TIME, bill.getDateAndTime());
            values.put(COLUMN_MARKET, bill.getMarket());
            values.put(COLUMN_LOCATION, bill.getCity());

            values.put(COLUMN_WEIGHT, item.getWeight());
            values.put(COLUMN_PPK, item.getPricePerKg());
            db.insert(ITEM_TABLE, null,values);
        }

    }

    //TODO write query methods for finding all products (either for a date and time or for a market or for any and all such queries

    /**
     * Returns a list of all items in the database that were entered at a certain time
     * @param dateTime
     * @return
     */
    public List<ShoppingItem> findItemsByPurchaseDate(String dateTime){
        List<ShoppingItem> items = new ArrayList<>();
        String sql = "SELECT * FROM " + ITEM_TABLE + " WHERE " + COLUMN_DATE_TIME +  " = \"" + dateTime + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
                float price = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                float ppk = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PPK));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
                ShoppingItem item = new ShoppingItem(name, price, weight, ppk);
                items.add(item);
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        return items;
    }

    /**
     * Finds all Product in the database that have been purchased from a certain market
     * @param market
     * @return
     */
    public List<ShoppingItem> findItemsByMarket(String market){
        List<ShoppingItem> items = new ArrayList<>();
        String sql = "SELECT * FROM " + ITEM_TABLE + " WHERE " + COLUMN_MARKET + " = \"" + market + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
                float price = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                float ppk = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PPK));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
                ShoppingItem item = new ShoppingItem(name, price, weight, ppk);
                items.add(item);
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        return items;

    }

    /**
     * Returns the metadata info such as market name adress and such for a given time
     * @param dateTime
     * @return
     */
    public String [] getMarketInfoByDate(String dateTime){
        //market, adress, city
        String [] data = new String[3];
        String sql = "SELECT * FROM " + ITEM_TABLE + " WHERE " + COLUMN_DATE_TIME +  " = \"" + dateTime + "\"" + " LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        //TODO some of these columns are null or thrown, why?
        if(cursor.moveToFirst()) {
            data[0] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MARKET));
            data[1] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            data[2] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
        }
        cursor.close();

        return data;
    }

}
