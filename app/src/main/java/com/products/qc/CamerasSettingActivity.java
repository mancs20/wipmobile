package com.products.qc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


public class CamerasSettingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mCameraSettingAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameras_setting);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        Button addCamera = (Button) findViewById(R.id.floatingBtnAddCamera);
        addCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO go to OneCameraSettingActivity

            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Populate the list of adapter
        //TODO Get elements from SharedPreferences
        List<CameraSettings> cameras = new ArrayList<CameraSettings>();
        for (int i = 0; i < 10; i++){
            String name = "camera_";
            String ip = "IP_";
            CameraSettings cameraSetting = new CameraSettings();
            name = name.concat(String.valueOf(i));
            ip = ip.concat(String.valueOf(i));
            cameraSetting.setCameraName(name);
            cameraSetting.setCameraIP(ip);
            cameras.add(cameraSetting);
        }

        // specify an adapter (see also next example)
        mCameraSettingAdapter = new CamerasSettingAdapter(cameras, this);
        mRecyclerView.setAdapter(mCameraSettingAdapter);
    }
}