package com.example.bvpraktmme.kassenzettel;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.bvpraktmme.kassenzettel.processing.ProcessingActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    private Random random = new Random(14343442l);
    private static final int CAMERA_REQUEST = 1888;
    private int SELECT_FILE = 1;
    private Uri imageUri;
    public static String IMAGE_URI = "image_uri_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(MainActivity.this));
        setContentView(R.layout.activity_main);

        //Menu Insertion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Button takePictureButton = (Button) findViewById(R.id.take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Kassenzettel"+ random.nextInt());
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getApplicationContext().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        Button btnSelect = (Button) findViewById(R.id.select_gallery);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });

    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.take_picture) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Kassenzettel"+ random.nextInt());
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getApplicationContext().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
        if(id == R.id.select_gallery){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK ) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getApplicationContext(), ProcessingActivity.class);
                intent.putExtra(IMAGE_URI, imageUri.toString());
                startActivity(intent);

            }

        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Intent intent = new Intent(getApplicationContext(), ProcessingActivity.class);
        intent.putExtra(IMAGE_URI, data.getData().toString());
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        instance = null;
    }

}
