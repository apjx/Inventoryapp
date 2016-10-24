package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract;

import static android.R.attr.name;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.inventoryapp.R.id.quantity;

/**
 * Created by Alexandre on 20/10/2016.
 */

public class ProductCursorAdapter extends CursorAdapter{

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        TextView mNameEditText = (TextView) view.findViewById(R.id.product_name);
        TextView mPrice = (TextView) view.findViewById(R.id.product_price);
        TextView mQuantity = (TextView) view.findViewById(quantity);
        Button minusOne = (Button) view.findViewById(R.id.decrease_button);

        final int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_ID));
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME));
        final String price = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE)));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY));

        mNameEditText.setText(name);
        mPrice.setText(price);
        mQuantity.setText(String.valueOf(quantity));

        minusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentValues values = new ContentValues();
                int mItemQty;
                mItemQty = (quantity - 1);
                values.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY, mItemQty);

                Uri currentUri = ContentUris.withAppendedId(ProductContract.ProductsEntry.CONTENT_URI, rowId);

                int rowUpdated = view.getContext().getContentResolver().update(currentUri, values, null, null);
            }
        });
    }
}