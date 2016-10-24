package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.ProductContract.ProductsEntry;

/**
 * Created by Alexandre on 20/10/2016.
 */

public class ProductDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductsEntry.TABLE_NAME + " ("
                + ProductsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_PRODUCT_PRICE + " INTEGER, "
                + ProductsEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER, "
                + ProductsEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_PRODUCT_EMAIL + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_PRODUCT_PICTURE_URI + " TEXT);";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
