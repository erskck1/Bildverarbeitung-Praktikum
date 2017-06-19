package com.example.bvpraktmme.kassenzettel;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bvpraktmme.kassenzettel.opencv.ObjectRecognizer;
import com.example.bvpraktmme.kassenzettel.permission.PermissionOrganizer;

import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ObjectDetectionActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
    }

    private ObjectRecognizer recognizer;
    public static String CONVERTED_URI_KEY = "converted_uri";
    private String convertedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_processing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("ObjectDetectActivity");

        if(!OpenCVLoader.initDebug()) {
            Log.d("ERROR", "Unable to load OpenCV");
        } else {
            Log.d("SUCCESS", "OpenCV loaded");
            recognizer = new ObjectRecognizer();
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(CameraFragment.IMAGE_URI)) {
            String filepath = extras.getString(CameraFragment.IMAGE_URI);

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
            boolean permission = PermissionOrganizer.checkPermission(ObjectDetectionActivity.this);

            if(permission) {
                showConvertedPicture();
            }


            imageUri= Uri.parse(extras.getString(CameraFragment.IMAGE_URI));
            //convertedUri = imageUri.toString();

            FloatingActionButton processingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            processingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check if the file has already been converted, then start the intent for the
                    // next activity, with the converted Image
                    if(convertedUri != null) {
                        Intent priceAreaIntent = new Intent(getApplicationContext(), PriceAreaDetectionActivity.class);
                        priceAreaIntent.putExtra(ObjectDetectionActivity.CONVERTED_URI_KEY, convertedUri);
                        startActivity(priceAreaIntent);
                    }
                }
            });


        }
    }

    public void showConvertedPicture() {
        Bitmap inImage = recognizer.applyFilters();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        //TODO dont save the image on disk, store it only in memory

        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), inImage, "Title", null);
        convertedUri = path;

        ImageView imageView = (ImageView) findViewById(R.id.display_image);
        Glide.with(this).loadFromMediaStore(Uri.parse(path)).into(imageView);
        
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionOrganizer.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showConvertedPicture();
                } else {
                    // TODO
                    //code for deny
                }
                break;
        }
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
