package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class CamerasSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameras_setting);
        RecyclerView mRecyclerView;
        RecyclerView.Adapter mCameraSettingAdapter;
        RecyclerView.LayoutManager mLayoutManager;
        ArrayList<CameraSettings> cameras;

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        FloatingActionButton addCamera = (FloatingActionButton) findViewById(R.id.floatingBtnAddCamera);
        addCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), OneCameraSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        /* use this setting to improve performance if you know that changes
         in content do not change the layout size of the RecyclerView*/
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Populate the list of adapter (cameras)
        cameras = CameraSettings.getCamerasFromSharedPreferences(this.getBaseContext());
        if (cameras != null && !cameras.isEmpty()){
            // specify an adapter (see also next example)
            mCameraSettingAdapter = new CamerasSettingAdapter(cameras, this);
            mRecyclerView.setAdapter(mCameraSettingAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), CameraToolActivity.class);
        startActivity(intent);
        finish();
    }
}