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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bvpraktmme.kassenzettel.ExceptionHandler;
import com.example.bvpraktmme.kassenzettel.MainActivity;
import com.example.bvpraktmme.kassenzettel.PictureNotAvailableException;
import com.example.bvpraktmme.kassenzettel.R;
import com.example.bvpraktmme.kassenzettel.database.PurchaseDisplayActivity;
import com.example.bvpraktmme.kassenzettel.database.SQliteDatabase;
import com.example.bvpraktmme.kassenzettel.processing.ProcessingActivity;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class OcrActivity extends AppCompatActivity {

    public static Bitmap image = null;
    private TessBaseAPI mTess; //Tess API reference
    private String datapath = ""; //path to folder containing language data file
    private static final String EXCEPTION_MESSAGE = "Please choose or take another picture!";
    private ProgressBar bar;
    private SQliteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_ocr_activity);
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("OCR Activity");

        datapath = getFilesDir()+ "/tesseract/";

        //Initialize the database
        mDatabase = new SQliteDatabase(this);

        //initialize Tesseract API
        String lang = "deu";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, lang);
        new OcrActivity.ImageProcessing().execute();

    }

    @Override
    protected void onDestroy() {
        mDatabase.close();
        super.onDestroy();
    }

    private class ImageProcessing extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result;
            try {
                result = processImage();
            } catch (Exception e) {
                e.printStackTrace();
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

                //Create a bill object from the ocr result
                StringParser parser = new StringParser(result);
                Bill bill = parser.parse();
                //TODO go into new activity/maybe do away with the ocr activity alltogether
                String dateTime = bill.getDateAndTime();
                mDatabase.addBill(bill);
                Log.d("db", "onPostExecute: database created" + Arrays.toString(mDatabase.getMarketInfoByDate(dateTime)));

                //Send the dateTime of the bill to the display activity to query database

                Intent displayIntent = new Intent(getApplicationContext(), PurchaseDisplayActivity.class);
                displayIntent.putExtra("dateTime", dateTime);
                startActivity(displayIntent);
            } else {
                if(MainActivity.instance != null) {
                    MainActivity.instance.finish();
                }
                Toast.makeText(getApplicationContext(), EXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mIntent);
                finish();
            }
            bar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private String processImage(){
        String OCResult;
        mTess.setImage(image);
        OCResult = mTess.getUTF8Text();

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
