package com.example.android.udacitylastprojectphase1;

import android.content.ContentUris;import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.udacitylastprojectphase1.data.ProductsContract.ProductsEntry;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCTS_LOADER = 0;

    ProductsCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button  = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        final ListView productsListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(ProductsEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);

                startActivity(intent);
            }
        });


        mCursorAdapter = new ProductsCursorAdapter(this, null);
        productsListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);

    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductsEntry.COLUMN_QUANTITY,
                ProductsEntry.COLUMN_PRICE
        };

        return new android.content.CursorLoader(this,
                ProductsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
