package com.example.user.your_breakfast.database;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.user.your_breakfast.model.Food;
import com.example.user.your_breakfast.model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "foodcard.db";
    private static final int DB_VER = 1;
    private static final String TABLE_NAME = "cart";
    private static final String REQUEST_TABLE_NAME = "orderId";
    private static final String FAVORITE_TABLE_NAME = "favorite";

    public Database(Context context) {
        super(context, DB_NAME, null, null, DB_VER);
    }

    public void redoAddToCart(String foodId, int quantity) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Select quantity from %s where foodId = '%s'", TABLE_NAME, foodId);
        Log.d("TAG", "SQL statement: " + query);
        Cursor cursor = db.rawQuery(query, null);
        int realQuantity = 0;
        if (cursor.moveToNext()) {
            realQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
        }
        if (quantity == realQuantity) {
            query = String.format("Delete from %s where foodId = '%s'", TABLE_NAME, foodId);
        } else {
            query = String.format("update %s set quantity = '%s' where foodId = '%s'", TABLE_NAME, realQuantity - quantity, foodId);
        }
        cursor.close();
        db.execSQL(query);
        db.close();
    }

    public List<Order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = String.format("Select foodId, foodName, foodPrice, quantity, time, imgURL from %s", TABLE_NAME);
        Log.d("TAG", "SQL statement: " + sql);
        Cursor cursor = db.rawQuery(sql, null);
        List<Order> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(new Order(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5)
            ));
        }
        cursor.close();
        db.close();
        return result;
    }

    public void addToCarts(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query;
        String getQuantity = String.format("Select quantity from %s where foodId = '%s'", TABLE_NAME, order.getFoodId());
        Cursor cursor = db.rawQuery(getQuantity, null);
        if (cursor.moveToFirst()) {
            int quantity = cursor.getInt(0);
            query = String.format("Update %s set quantity = %s where foodId = '%s'", TABLE_NAME, quantity + order.getQuantity(), order.getFoodId());
        } else
            query = String.format("INSERT INTO %s (foodId, foodName, foodPrice, quantity, time, imgURL) values ('%s', '%s', '%s', '%s', '%s', '%s') "
                    , TABLE_NAME, order.getFoodId(), order.getFoodName(), order.getFoodPrice(), order.getQuantity(), order.getTime(), order.getImgURL());
        db.execSQL(query);
        Log.d("TAG", "SQL statement: " + query);
        cursor.close();
        db.close();
    }

    public void addToCarts(String foodId, int quantity) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Update %s set quantity = %s where foodId = '%s'", TABLE_NAME, quantity, foodId);
        db.execSQL(query);
        db.close();
    }


    public void cleanCart() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Delete from %s", TABLE_NAME);
        db.execSQL(query);
        db.close();
    }

    public void deleteCart(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Delete from %s where foodId = '%s'", TABLE_NAME, foodId);
        db.execSQL(query);
        db.close();
    }


    public void addRequestID(String requestID, String phoneNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Insert into %s (ID, userID) values( '%s','%s' )", REQUEST_TABLE_NAME, requestID, phoneNumber);
        db.execSQL(query);
        db.close();
    }

    public void addToFavorite(String foodID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Insert into %s values ('%s')", FAVORITE_TABLE_NAME, foodID);
        db.execSQL(query);
        db.close();
    }

    public void removeFromFavorite(String foodID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Delete from %s where foodID = '%s'", FAVORITE_TABLE_NAME, foodID);
        db.execSQL(query);
        db.close();
    }

}
