package com.example.bvpraktmme.kassenzettel.processing;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bvpraktmme.kassenzettel.ExceptionHandler;
import com.example.bvpraktmme.kassenzettel.MainActivity;
import com.example.bvpraktmme.kassenzettel.PictureNotAvailableException;
import com.example.bvpraktmme.kassenzettel.camera.CameraFragment;
import com.example.bvpraktmme.kassenzettel.ocr.OcrActivity;
import com.example.bvpraktmme.kassenzettel.R;
import com.example.bvpraktmme.kassenzettel.opencv.ObjectRecognizer;
import com.example.bvpraktmme.kassenzettel.permission.PermissionOrganizer;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class ProcessingActivity extends AppCompatActivity {

    private static final String EXCEPTION_MESSAGE = "Please choose or take another picture!";
    private static final String TAG = "ProcessingActivity";

    private Uri imageUri;
    private ObjectRecognizer recognizer;
    private Bitmap convertedImage;
    private FloatingActionButton processingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(ProcessingActivity.this));
        setContentView(R.layout.activity_processing);

        //Menu Insertion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("ProcessingActivity");

        // Ask for permissions
        final boolean permission =PermissionOrganizer.checkPermission(ProcessingActivity.this);

        Bundle extras = getIntent().getExtras();

        if(!OpenCVLoader.initDebug()) {
            Log.d("ERROR", "Unable to load OpenCV");
        } else {
            Log.d("SUCCESS", "OpenCV loaded");
            recognizer = new ObjectRecognizer();
        }

        // find selected convertedImage
        if (extras != null && extras.containsKey(CameraFragment.IMAGE_URI)) {
            imageUri = Uri.parse(extras.getString(CameraFragment.IMAGE_URI));
        }

        // if permissions are not activated, don't show the process button
        processingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        if(!permission) {
            processingButton.hide();
        } else {
            processingButton.show();
        }
        processingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startProcess();
                    } catch (Exception e) {
                        ProcessingActivity.this.finish();
                        Toast.makeText(getApplicationContext(), EXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
                        Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mIntent);
                        return;
                    }
                    OcrActivity.image = convertedImage;
                    Intent ocrIntent = new Intent(getApplicationContext(), OcrActivity.class);
                    startActivity(ocrIntent);
                }
            });

        // to show selected convertedImage
        ImageView imageView = (ImageView) findViewById(R.id.display_image);
        Glide.with(this).loadFromMediaStore(imageUri).into(imageView);

    }

    public void startProcess() throws PictureNotAvailableException {
        Bitmap bm = null;
        if (imageUri != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recognizer.setImage(bm);
        convertedImage = recognizer.applyFilters();
        recognizer.setImage(convertedImage);
        convertedImage = recognizer.findPriceArea();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionOrganizer.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    processingButton.show();
                    Context context = getApplicationContext();
                    CharSequence text = "Permissions active!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "If you want to continue, please activate the permissions!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    finish();
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
