package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hotel_orders.db";
    private static final int DATABASE_VERSION = 2; // incremented to trigger upgrade

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tasks table with quantity column
        db.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nameofdish TEXT, " +
                "price INTEGER, " +
                "quantity INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop and recreate table on version change
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    // Insert or update a dish in the table
    public void insertOrUpdateDish(String name, int price) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if dish already exists
        Cursor cursor = db.rawQuery("SELECT id, quantity FROM tasks WHERE nameofdish = ?", new String[]{name});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int currentQty = cursor.getInt(1);

            // Update quantity
            ContentValues values = new ContentValues();
            values.put("quantity", currentQty + 1);
            db.update("tasks", values, "id = ?", new String[]{String.valueOf(id)});
        } else {
            // Insert new dish
            ContentValues values = new ContentValues();
            values.put("nameofdish", name);
            values.put("price", price);
            values.put("quantity", 1);
            db.insert("tasks", null, values);
        }
        cursor.close();
    }

    // Clear all orders
    public void clearOrders() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tasks");
    }

    // Optional: Get all tasks (used in AllOrdersActivity)
    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks", null);
    }
}
