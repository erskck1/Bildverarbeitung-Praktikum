package com.example.bvpraktmme.kassenzettel.camera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bvpraktmme.kassenzettel.R;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
        }
    }
}
