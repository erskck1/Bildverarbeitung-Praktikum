package com.example.bvpraktmme.kassenzettel;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import android.provider.MediaStore;

import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bvpraktmme.kassenzettel.opencv.ObjectRecognizer;
import com.example.bvpraktmme.kassenzettel.permission.PermissionOrganizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PriceAreaDetectionActivity extends AppCompatActivity {
    private ObjectRecognizer recognizer;
    public static String FINAL_URI_KEY = "ocr_uri";
    private String finalConvertedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recognizer = new ObjectRecognizer();

        setContentView(R.layout.activity_processing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("PriceAreaActivity");

        Bundle extras = getIntent().getExtras();
        Uri imageUri = null;
        if (extras != null && extras.containsKey(ObjectDetectionActivity.CONVERTED_URI_KEY)) {
            String filepath = extras.getString(ObjectDetectionActivity.CONVERTED_URI_KEY);

            Bitmap bm = null;

            imageUri = Uri.parse(filepath);

            if (imageUri != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            recognizer.setImage(bm);
            boolean permission = PermissionOrganizer.checkPermission(PriceAreaDetectionActivity.this);

            if(permission) {
                showConvertedPicture();
            }
        }

        FloatingActionButton processingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        processingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalConvertedUri != null) {
                    Intent ocrIntent = new Intent(getApplicationContext(), OcrActivity.class);

                    ocrIntent.putExtra(PriceAreaDetectionActivity.FINAL_URI_KEY, finalConvertedUri);
                    startActivity(ocrIntent);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionOrganizer.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showConvertedPicture();
                } else {
                    //code for deny
                }
                break;
        }
    }

    /**
     * Method applying imageprocessing defined in ObjectRecognizer to find the the Area with the prices
     * and save it and display it in the Imageview
     */
    public void showConvertedPicture() {
        Bitmap inImage = recognizer.findPriceArea();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        //TODO dont save the image on disk, store it only in memory

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), inImage, "Title", null);
        finalConvertedUri = path;
        ImageView imageView = (ImageView) findViewById(R.id.display_image);
        Glide.with(this).loadFromMediaStore(Uri.parse(path)).into(imageView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
