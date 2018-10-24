package com.example.android.udacitylastprojectphase1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.udacitylastprojectphase1.data.ProductsContract;

/**
 * Created by admin on 14.04.2018.
 */

public class ProductsCursorAdapter extends CursorAdapter {

    Context appContext;

    public ProductsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button button = (Button) view.findViewById(R.id.sell_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellUpdate(cursor);
            }
        });

        int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_PRICE);

        String productName = cursor.getString(nameColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);

        nameTextView.setText(productName);
        quantityTextView.setText(productQuantity);
        priceTextView.setText(productPrice);
    }

    private void sellUpdate(Cursor cursor) {
        int currQuantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_QUANTITY);
        int currQuantity = cursor.getInt(currQuantityColumnIndex);
        int currIDColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry._ID);
        int currID = cursor.getInt(currIDColumnIndex);
        appContext = MyApp.getContext();

        if (currQuantity > 0) {
            int afterClickQuantity = currQuantity - 1;
            String afterClickQuantityString = Integer.toString(afterClickQuantity);

            Uri currentProductUri = ContentUris.withAppendedId(ProductsContract.ProductsEntry.CONTENT_URI, currID);

            ContentValues values = new ContentValues();
            values.put(ProductsContract.ProductsEntry.COLUMN_QUANTITY, afterClickQuantityString);

            appContext.getContentResolver().update(currentProductUri, values, null, null);
        } else {
            Toast.makeText(MyApp.getContext(), R.string.sell_error, Toast.LENGTH_SHORT).show();
        }
    }
}
