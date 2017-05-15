package com.example.bvpraktmme.kassenzettel;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ProcessingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        //Menu Insertion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("call", "acit");

        Bundle extras = getIntent().getExtras();
        Uri imageUri = null;

        if (extras != null && extras.containsKey(CameraFragment.IMAGE_URI)) {
            imageUri= Uri.parse(extras.getString(CameraFragment.IMAGE_URI));
        }

        /*Bitmap bm = null;
        if (imageUri != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        ImageView imageView = (ImageView) findViewById(R.id.display_image);
        Glide.with(this).loadFromMediaStore(imageUri).into(imageView);
    }
}
