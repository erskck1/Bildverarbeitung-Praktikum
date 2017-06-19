package com.example.bvpraktmme.kassenzettel;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OcrActivity extends AppCompatActivity {

    Bitmap image = null;
    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //path to folder containing language data file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("OCR Activity");


        //TODO Use Image instead of Hardcoded
        Bundle extras = getIntent().getExtras();
        Uri finalImageUri = null;
        if (extras != null && extras.containsKey(PriceAreaDetectionActivity.FINAL_URI_KEY)){
            String filepath = extras.getString(PriceAreaDetectionActivity.FINAL_URI_KEY);

            finalImageUri = Uri.parse(filepath);

            if (finalImageUri != null) {
                try {
                    image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), finalImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Display processed image at top
        ImageView imageView = (ImageView) findViewById(R.id.ProcessedImageView);
        Glide.with(this).loadFromMediaStore(finalImageUri).into(imageView);


        datapath = getFilesDir()+ "/tesseract/";

        //initialize Tesseract API
        String lang = "deu";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, lang);
        processImage();
    }

    public void processImage(){
        String OCResult = null;
        mTess.setImage(image);
        OCResult = mTess.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setMovementMethod(new ScrollingMovementMethod());
        OCRTextView.setText(OCResult);
    }


    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/deu.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/deu.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/deu.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
