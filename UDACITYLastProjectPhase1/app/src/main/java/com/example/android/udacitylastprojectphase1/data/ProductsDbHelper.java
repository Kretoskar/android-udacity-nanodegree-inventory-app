package com.example.android.udacitylastprojectphase1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.udacitylastprojectphase1.data.ProductsContract.ProductsEntry;

/**
 * Created by admin on 09.04.2018.
 */

public class ProductsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shop.db";

    private static final int DATABASE_VERSION = 1;

    public ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductsEntry.TABLE_NAME + "("
                + ProductsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ProductsEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + ProductsEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + ProductsEntry.COLUMN_SUPLIER_PHONE_NUMBER + " TEXT);";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductsEntry.TABLE_NAME);
        onCreate(db);
    }
}
