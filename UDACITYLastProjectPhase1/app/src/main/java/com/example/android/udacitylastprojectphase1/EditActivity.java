package com.example.android.udacitylastprojectphase1;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.udacitylastprojectphase1.data.ProductsContract;
import com.example.android.udacitylastprojectphase1.data.ProductsDbHelper;

import static com.example.android.udacitylastprojectphase1.R.id.name;
import static com.example.android.udacitylastprojectphase1.R.id.quantity;

public class EditActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mSuppNameEditText;
    private EditText mSuppPhoneEditText;

    private static final int EXISTING_PRODUCTS_LOADER = 0;

    private Uri mCurrentPetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        Button addProductButton = (Button) findViewById(R.id.button);

        if(mCurrentPetUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_product));
        } else {
            setTitle(R.string.editor_activity_title_edit_product);
            addProductButton.setText(R.string.update_product_button);
            getLoaderManager().initLoader(EXISTING_PRODUCTS_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.product_name_edit_text);
        mQuantityEditText = (EditText) findViewById(R.id.product_quantity_edit_text);
        mPriceEditText = (EditText) findViewById(R.id.product_price_edit_text);
        mSuppNameEditText = (EditText) findViewById(R.id.product_supplier_name);
        mSuppPhoneEditText = (EditText) findViewById(R.id.product_supplier_phone_number_edit_text);

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertProduct();
            }
        });

        Button deleteProductButton = (Button) findViewById(R.id.button_delete);
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        Button plusButton = (Button) findViewById(R.id.button_plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuantity();
            }
        });

        Button minusButton = (Button) findViewById(R.id.button_minus);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuantity();
            }
        });

        Button callSupplierButton = (Button) findViewById(R.id.button_call_supplier);
        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupplier();
            }
        });
    }

    private void callSupplier() {
        String phoneNumber = mSuppPhoneEditText.getText().toString().trim();
        String uri = "tel:" + phoneNumber;

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));

        startActivity(intent);
    }

    private void incrementQuantity() {
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        int incrementedQuantity = quantity + 1;
        String incrementedQuantityString = String.valueOf(incrementedQuantity);

        mQuantityEditText.setText(incrementedQuantityString);
    }

    private void decrementQuantity() {
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        int decrementedQuantity = quantity - 1;
        String decrementedQuantityString = String.valueOf(decrementedQuantity);

        if(quantity > 0) {
            mQuantityEditText.setText(decrementedQuantityString);
        }else {
            Toast.makeText(this, R.string.quantity_error_toast, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void insertProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String suppNameString = mSuppNameEditText.getText().toString().trim();
        String suppPhoneString = mSuppPhoneEditText.getText().toString().trim();
        if(mCurrentPetUri == null) {
            if(TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, getString(R.string.editor_insert_product_null_variables), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int quantity = Integer.parseInt(quantityString);
        int price = Integer.parseInt(priceString);

        if(quantity >= 0 && price >=0) {
            ProductsDbHelper mDbHelper = new ProductsDbHelper(this);

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(ProductsContract.ProductsEntry.COLUMN_QUANTITY, quantity);
            values.put(ProductsContract.ProductsEntry.COLUMN_PRICE, price);
            values.put(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME, suppNameString);
            values.put(ProductsContract.ProductsEntry.COLUMN_SUPLIER_PHONE_NUMBER, suppPhoneString);

            if (mCurrentPetUri == null) {
                Uri newUri = getContentResolver().insert(ProductsContract.ProductsEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_product_succesed), Toast.LENGTH_LONG).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_product_succesed), Toast.LENGTH_LONG).show();
                }
            }
            finish();
        }   else Toast.makeText(this, "Price or quantity is invalid", Toast.LENGTH_SHORT).show();
    }

    private void deleteProduct() {
       if(mCurrentPetUri == null) {
           Toast.makeText(this, R.string.delete_error , Toast.LENGTH_SHORT).show();
       } else {
           DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   getContentResolver().delete(mCurrentPetUri, null, null);
                   finish();
               }
           };
           showDeleteDialog(discardButtonClickListener);
       }
    }

    private void showDeleteDialog(
        DialogInterface.OnClickListener deleteButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_interface_delete_button);
        builder.setPositiveButton( R.string.dialog_interface_delete_button_positive_answer, deleteButtonClickListener);
        builder.setNegativeButton(R.string.dialog_interface_delete_button_negative_answer, new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductsContract.ProductsEntry._ID,
                ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductsContract.ProductsEntry.COLUMN_QUANTITY,
                ProductsContract.ProductsEntry.COLUMN_PRICE,
                ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsContract.ProductsEntry.COLUMN_SUPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this, mCurrentPetUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if(cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_PRICE);
            int suppNameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME);
            int suppPhoneColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_SUPLIER_PHONE_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            String suppName = cursor.getString(suppNameColumnIndex);
            String suppPhone = cursor.getString(suppPhoneColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mSuppNameEditText.setText(suppName);
            mSuppPhoneEditText.setText(suppPhone);


        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSuppNameEditText.setText("");
        mSuppPhoneEditText.setText("");
    }
}
