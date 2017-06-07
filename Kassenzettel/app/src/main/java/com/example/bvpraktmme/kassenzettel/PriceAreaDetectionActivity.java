package com.example.bvpraktmme.kassenzettel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import android.provider.MediaStore;

import android.support.v7.widget.Toolbar;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bvpraktmme.kassenzettel.opencv.ObjectRecognizer;
import com.example.bvpraktmme.kassenzettel.permission.PermissionOrganizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PriceAreaDetectionActivity extends AppCompatActivity {
    private ObjectRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recognizer = new ObjectRecognizer();

        setContentView(R.layout.activity_processing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(ObjectDetectionActivity.CONVERTED_URI_KEY)) {
            String filepath = extras.getString(ObjectDetectionActivity.CONVERTED_URI_KEY);

            Bitmap bm = null;

            Uri imageUri = Uri.parse(filepath);

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
        //TODO change to new Method for finding the price area
        Bitmap inImage = recognizer.findPriceArea();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), inImage, "Title", null);

        ImageView imageView = (ImageView) findViewById(R.id.display_image);
        Glide.with(this).loadFromMediaStore(Uri.parse(path)).into(imageView);

    }



}
