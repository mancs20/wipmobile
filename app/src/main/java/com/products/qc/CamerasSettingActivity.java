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


    private RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameras_setting);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        FloatingActionButton addCamera = (FloatingActionButton) findViewById(R.id.floatingBtnAddCamera);
        addCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), OneCameraSettingActivity.class);
                startActivity(intent);
            }
        });
        /* use this setting to improve performance if you know that changes
         in content do not change the layout size of the RecyclerView*/
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //Populate the list of adapter (cameras)
        PopulateAdapter(mRecyclerView);
    }

    private void PopulateAdapter(RecyclerView mRecyclerView){
        ArrayList<CameraSettings> cameras = CameraSettings.getCamerasFromSharedPreferences(this.getBaseContext());
        if (cameras != null && !cameras.isEmpty()){
            // specify an adapter (see also next example)
            RecyclerView.Adapter mCameraSettingAdapter = new CamerasSettingAdapter(cameras, this);
            mRecyclerView.setAdapter(mCameraSettingAdapter);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PopulateAdapter(mRecyclerView);
        /*ArrayList<CameraSettings> camRestart = CameraSettings.getCamerasFromSharedPreferences(this.getBaseContext());
        if ((camRestart != null) && (mRecyclerView.getAdapter() == null ||(camRestart.size() != mRecyclerView.getAdapter().getItemCount()))){
            PopulateAdapter(mRecyclerView);
        }*/
    }
}