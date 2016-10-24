package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductsEntry;

import java.net.URI;

/**
 * Created by Alexandre on 20/10/2016.
 */

public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDbHelper mProductDbHelper;

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 200;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#",PRODUCT_ID);
    }

    //Initialize the provider and the database helper object.
    @Override
    public boolean onCreate() {
        mProductDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mProductDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS :
                cursor = database.query(ProductsEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            case PRODUCT_ID :
                selection = ProductsEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default :
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductsEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
    }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS :

                String name = contentValues.getAsString(ProductsEntry.COLUMN_PRODUCT_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Pet requires a name");
                }

                Integer price = contentValues.getAsInteger(ProductsEntry.COLUMN_PRODUCT_PRICE);
                if (price == null || price <= 0) {
                    throw new IllegalArgumentException("Pet requires a name");
                }

                String supplier = contentValues.getAsString(ProductsEntry.COLUMN_PRODUCT_SUPPLIER);
                if (supplier == null) {
                    throw new IllegalArgumentException("Pet requires a name");
                }

                String email = contentValues.getAsString(ProductsEntry.COLUMN_PRODUCT_EMAIL);
                if (email == null) {
                    throw new IllegalArgumentException("Pet requires a name");
                }

                SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
                long id = database.insert(ProductsEntry.TABLE_NAME, null, contentValues);
                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                    // Once we know the ID of the new row in the table,
                    // return the new URI with the ID appended to the end of it
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default :
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        int rowDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowDeleted = database.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductsEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                getContext().getContentResolver().notifyChange(uri, null);
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductsEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                getContext().getContentResolver().notifyChange(uri, null);
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductsEntry.COLUMN_PRODUCT_NAME)){
        String name = values.getAsString(ProductsEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        }

        if (values.containsKey(ProductsEntry.COLUMN_PRODUCT_PRICE)){
        Integer price = values.getAsInteger(ProductsEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        }

        if (values.containsKey(ProductsEntry.COLUMN_PRODUCT_SUPPLIER)){
        String supplier = values.getAsString(ProductsEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        }

        if (values.containsKey(ProductsEntry.COLUMN_PRODUCT_EMAIL)){
        String email = values.getAsString(ProductsEntry.COLUMN_PRODUCT_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        int nbRowsUpdated = database.update(ProductsEntry.TABLE_NAME, values, selection, selectionArgs);

        return nbRowsUpdated;
    }

}
