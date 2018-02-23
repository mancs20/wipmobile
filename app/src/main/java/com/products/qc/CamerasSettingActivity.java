package com.products.qc;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewDebug;

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

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Populate the list of adapter
        //TODO Get elements from SharedPreferences
        List<CameraSetting> cameras = new ArrayList<CameraSetting>();
        for (int i = 0; i < 10; i++){
            String name = "camera_";
            String ip = "IP_";
            CameraSetting cameraSetting = new CameraSetting();
            name = name.concat(String.valueOf(i));
            ip = ip.concat(String.valueOf(i));
            cameraSetting.setCameraName(name);
            cameraSetting.setCameraIP(ip);
        }

        // specify an adapter (see also next example)
        mCameraSettingAdapter = new CamerasSettingAdapter(cameras, getApplicationContext());
        mRecyclerView.setAdapter(mCameraSettingAdapter);
    }
}