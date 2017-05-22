package com.example.bvpraktmme.kassenzettel;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        //Menu Insertion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Floating Action Button
        FloatingActionButton processingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        processingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here Code
            }
        });

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
