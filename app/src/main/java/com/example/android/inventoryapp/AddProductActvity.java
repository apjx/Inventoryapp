package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductProvider;

import java.io.FileDescriptor;
import java.io.IOException;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.attr.order;
import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_SENDTO;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.android.inventoryapp.R.id.quantity;
import static com.example.android.inventoryapp.data.ProductProvider.LOG_TAG;

/**
 * Created by Alexandre on 20/10/2016.
 */

public class AddProductActvity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private int PICK_IMAGE_REQUEST = 1;

    private static final int EXISTING_PRODUCT_LOADER = 1;

    private EditText mNameEditText;
    private EditText mPrice;
    private TextView mQuantity;
    private EditText mSupplier;
    private EditText mEmail;
    private ImageView mPicture;

    private String pictureUri;

    private String quantityVal;

    Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPrice = (EditText) findViewById(R.id.edit_product_price);
        mQuantity = (TextView) findViewById(R.id.current_quantity);
        mSupplier = (EditText) findViewById(R.id.edit_product_supplier);
        mEmail = (EditText) findViewById(R.id.edit_product_email);
        mPicture = (ImageView) findViewById(R.id.product_picture);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mCurrentProductUri == null){
            setTitle("New product");
            mQuantity.setText("1");
        }
        else{
            setTitle("Edit product");
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        Button minus = (Button) findViewById(R.id.minus_button);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int counter = Integer.parseInt(mQuantity.getText().toString());
                counter--;
                quantityVal = Integer.toString(counter);
                mQuantity.setText(quantityVal);
            }
    });

        Button plus = (Button) findViewById(R.id.plus_button);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int counter = Integer.parseInt(mQuantity.getText().toString());
                counter++;
                quantityVal = Integer.toString(counter);
                mQuantity.setText(quantityVal);
            }
        });

        Button sendEmailButton = (Button) findViewById(R.id.send_email_button);
        final String emails = mEmail.getText().toString();
        final String productName = mNameEditText.getText().toString();
        Log.d("cpppp", "ff" + emails);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emails});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New order of" + productName + "for Alex Company");
                startActivity(emailIntent);
            }
        });

        Button addPictureButton = (Button) findViewById(R.id.add_picture);
        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    ;}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        // The document selected by the user won't be returned in the intent.
        // Instead, a URI to that document will be contained in the return intent
        // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && resultData != null && resultData.getData() != null) {
            Uri uri = resultData.getData();
            pictureUri = uri.toString();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView productPicture = (ImageView) findViewById(R.id.product_picture);
                productPicture.setImageBitmap(bitmap);
                productPicture.setVisibility(View.VISIBLE);

                Button addPictureButton = (Button) findViewById(R.id.add_picture);
                addPictureButton.setText("Swap picture");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add_poductduct.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_poduct, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                deleteProduct();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveProduct(){

        String name = mNameEditText.getText().toString().trim();
        String price = mPrice.getText().toString().trim();
        String quantity = mQuantity.getText().toString().trim();
        String supplier = mSupplier.getText().toString().trim();
        String email = mEmail.getText().toString().trim();

        ContentValues newProduct = new ContentValues();
        newProduct.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME, name);
        newProduct.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE, price);
        newProduct.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        newProduct.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        newProduct.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_EMAIL, email);
        newProduct.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_PICTURE_URI, pictureUri);
        Log.d("blublu", "picture Uri = " + pictureUri);

if (mCurrentProductUri == null){
        Uri newUri = getContentResolver().insert(ProductContract.ProductsEntry.CONTENT_URI, newProduct);
    // Show a toast message depending on whether or not the insertion was successful.
    if (newUri == null) {
        // If the new content URI is null, then there was an error with insertion.
        Toast.makeText(this, getString(R.string.insert_product_failed),
                Toast.LENGTH_SHORT).show();
    } else {
        // Otherwise, the insertion was successful and we can display a toast.
        Toast.makeText(this, getString(R.string.insert_product_successful),
                Toast.LENGTH_SHORT).show();
}} else{
    int rowsAffected = getContentResolver().update(mCurrentProductUri, newProduct, null, null);
    // Show a toast message depending on whether or not the update was successful.
    if (rowsAffected == 0) {
        // If no rows were affected, then there was an error with the update.
        Toast.makeText(this, getString(R.string.update_product_failed),
                Toast.LENGTH_SHORT).show();
    } else {
        // Otherwise, the update was successful and we can display a toast.
        Toast.makeText(this, getString(R.string.update_product_successful),
                Toast.LENGTH_SHORT).show();
    }
}
}

    public void deleteProduct(){
        Log.d("cdddd", mCurrentProductUri.toString());
        if (mCurrentProductUri != null){
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the product that we want.
        int rowDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
        }
        else{
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_EMAIL,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_PICTURE_URI};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();

        String currentName = data.getString(data.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME));
        int currentPrice = data.getInt(data.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE));
        int currentQuantity = data.getInt(data.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY));
        String currentSupplier = data.getString(data.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER));
        String currentEmail = data.getString(data.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_EMAIL));
        String currentImageStringUri = data.getString(data.getColumnIndexOrThrow(ProductContract.ProductsEntry.COLUMN_PRODUCT_PICTURE_URI));
        Uri currentImageUri = Uri.parse(currentImageStringUri);

        mNameEditText.setText(currentName);
        mPrice.setText(String.valueOf(currentPrice));
        mQuantity.setText(String.valueOf(currentQuantity));
        mSupplier.setText(currentSupplier);
        mEmail.setText(currentEmail);
        mPicture.setImageBitmap(getBitmapFromUri(currentImageUri));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mPrice.setText(null);
        mQuantity.setText(null);
        mSupplier.setText(null);
        mEmail.setText(null);
        mPicture.setImageBitmap(null);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }
}
