package com.example.bvpraktmme.kassenzettel.ocr;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bvpraktmme.kassenzettel.ExceptionHandler;
import com.example.bvpraktmme.kassenzettel.MainActivity;
import com.example.bvpraktmme.kassenzettel.PictureNotAvailableException;
import com.example.bvpraktmme.kassenzettel.R;
import com.example.bvpraktmme.kassenzettel.processing.ProcessingActivity;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OcrActivity extends AppCompatActivity {

    public static Bitmap image = null;
    private TessBaseAPI mTess; //Tess API reference
    private String datapath = ""; //path to folder containing language data file
    private static final String EXCEPTION_MESSAGE = "Please choose or take another picture!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_ocr_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("OCR Activity");

        datapath = getFilesDir()+ "/tesseract/";

        //initialize Tesseract API
        String lang = "deu";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, lang);
        new OcrActivity.ImageProcessing().execute();

    }

    private class ImageProcessing extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result;
            try {
                result = processImage();
            } catch (Exception e) {
                return "notexecuted";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.equals("notexecuted") && result !=null) {
                TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
                OCRTextView.setMovementMethod(new ScrollingMovementMethod());
                OCRTextView.setText(result);
                Toast.makeText(getApplicationContext(), "Process successfully ended!", Toast.LENGTH_SHORT).show();
            } else {
                if(MainActivity.instance != null) {
                    MainActivity.instance.finish();
                }
                Toast.makeText(getApplicationContext(), EXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mIntent);
                finish();
            }

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private String processImage(){
        String OCResult;
        mTess.setImage(image);
        OCResult = mTess.getUTF8Text();
        // TODO parse OCResult and create a Bill Object and then
        // return BillObject.toString();
        return OCResult;

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
