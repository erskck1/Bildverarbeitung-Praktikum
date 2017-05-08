package com.example.bvpraktmme.kassenzettel;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ProcessingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        Intent intent = getIntent();
        String uriString = intent.getStringExtra("uri");
        Uri uri = Uri.parse(uriString);
        ImageView imageView = (ImageView) findViewById(R.id.display_image);
        Glide.with(this).loadFromMediaStore(uri).into(imageView);
    }
}
