package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class CameraToolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tool);

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mCameraSettingAdapter;
        RecyclerView.LayoutManager mLayoutManager;
        ArrayList<CameraSettings> cameras;

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
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
            mCameraSettingAdapter = new CameraToolAdapter(cameras, this);
            mRecyclerView.setAdapter(mCameraSettingAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.camera_tool_menu, menu);
        return true;
    }

    public void showCameraIPSettings(MenuItem item){
        Intent intent = new Intent(this, CamerasSettingActivity.class);
        startActivity(intent);
    }
}
