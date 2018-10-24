package com.example.android.udacitylastprojectphase1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.attr.switchMinWidth;
import static android.R.attr.value;

/**
 * Created by admin on 11.04.2018.
 */

    public class ProductsProvider extends ContentProvider {

    private ProductsDbHelper mDbHelper;

    public static final String LOG_TAG = ProductsProvider.class.getSimpleName();

    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductsContract.ProductsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = ProductsContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductsContract.ProductsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return  cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductsContract.ProductsEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductsContract.ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match)  {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for" + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        //SANITY CHECKS
        String name = values.getAsString(ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(ProductsContract.ProductsEntry.COLUMN_PRICE);
        if(price == null || price <= 0) {
            throw new IllegalArgumentException("Price is required and it has to be higher than 0");
        }

        Integer quantity = values.getAsInteger(ProductsContract.ProductsEntry.COLUMN_QUANTITY);
        if(quantity == null) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        String supName = values.getAsString(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME);
        if(supName == null) {
            throw new IllegalArgumentException("Supplier requires a name");
        }

        String suppPhone = values.getAsString(ProductsContract.ProductsEntry.COLUMN_SUPLIER_PHONE_NUMBER);
        if(suppPhone == null) {
            throw new IllegalArgumentException("Supplier requires a phone number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ProductsContract.ProductsEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ProductsContract.ProductsEntry.TABLE_NAME, selection, selectionArgs);
            case  PRODUCTS_ID:
                getContext().getContentResolver().notifyChange(uri, null);
                selection = ProductsContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return database.delete(ProductsContract.ProductsEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for" + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductsContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if(contentValues.containsKey(ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME);
            if(name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if(contentValues.containsKey(ProductsContract.ProductsEntry.COLUMN_PRICE)) {
            Integer price = contentValues.getAsInteger(ProductsContract.ProductsEntry.COLUMN_PRICE);
            if(price == null || price <= 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if(contentValues.containsKey(ProductsContract.ProductsEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(ProductsContract.ProductsEntry.COLUMN_QUANTITY);
            if(quantity == null) {
                throw new IllegalArgumentException("Product requires a quantity");
            }
        }

        if(contentValues.containsKey(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME)) {
            String suppName = contentValues.getAsString(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME);
            if(suppName == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        if(contentValues.containsKey(ProductsContract.ProductsEntry.COLUMN_SUPLIER_PHONE_NUMBER)) {
            String suppPhone = contentValues.getAsString(ProductsContract.ProductsEntry.COLUMN_SUPLIER_PHONE_NUMBER);
            if(suppPhone == null) {
                throw new IllegalArgumentException("Product requires a supplier phone number");
            }
        }

        if(contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri, null);

        return database.update(ProductsContract.ProductsEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }
}
