package com.example.bvpraktmme.kassenzettel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ProcessingActivity extends AppCompatActivity {

    private static final String TAG = "ProcessingActivity";

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        //Menu Insertion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(CameraFragment.IMAGE_URI)) {
            imageUri= Uri.parse(extras.getString(CameraFragment.IMAGE_URI));

            FloatingActionButton processingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            processingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent objectDetectionIntent = new Intent(getApplicationContext(), ObjectDetectionActivity.class);
                    objectDetectionIntent.putExtra(CameraFragment.IMAGE_URI, imageUri.toString());
                    startActivity(objectDetectionIntent);
                }
            });

        }

        ImageView imageView = (ImageView) findViewById(R.id.display_image);
        Glide.with(this).loadFromMediaStore(imageUri).into(imageView);

    }
}
