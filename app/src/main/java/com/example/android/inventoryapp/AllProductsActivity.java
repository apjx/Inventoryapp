package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ProductContract;

import static android.R.attr.id;

public class AllProductsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter productCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllProductsActivity.this, AddProductActvity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_textview);
        listView.setEmptyView(emptyView);

        productCursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(productCursorAdapter);

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(AllProductsActivity.this, AddProductActvity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductsEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductsEntry.COLUMN_ID,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(
                this,
                ProductContract.ProductsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        productCursorAdapter.changeCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.changeCursor(null);
    }
}

