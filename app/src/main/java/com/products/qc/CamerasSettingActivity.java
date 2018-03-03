package com.products.qc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Populate the list of adapter
        //TODO Get elements from SharedPreferences
        cameras = CameraSettings.getCamerasFromSharedPreferences(this.getBaseContext());
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
/*        if (sharedPref.contains("CamerasIP")){
            Gson gson = new Gson();
            String json = sharedPref.getString("CamerasIP", null);
            Type type = new TypeToken<ArrayList<CameraSettings>>() {}.getType();
            ArrayList cameras = gson.fromJson(json, type);
        }*/

        if (cameras != null){

            /*try {
                userList = (ArrayList) ObjectSerializer.deserialize(prefs.getString("UserList", ObjectSerializer.serialize(new ArrayList())));
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            //List<CameraSettings> cameras = new ArrayList<CameraSettings>();
        /*for (int i = 0; i < 10; i++){
            String name = "camera_";
            String ip = "IP_";
            CameraSettings cameraSetting = new CameraSettings();
            name = name.concat(String.valueOf(i));
            ip = ip.concat(String.valueOf(i));
            cameraSetting.setCameraName(name);
            cameraSetting.setCameraIP(ip);
            cameras.add(cameraSetting);
        }*/

            // specify an adapter (see also next example)
            mCameraSettingAdapter = new CamerasSettingAdapter(cameras, this);
            mRecyclerView.setAdapter(mCameraSettingAdapter);
        }








        /*List<CameraSettings> cameras = new ArrayList<CameraSettings>();
        *//*for (int i = 0; i < 10; i++){
            String name = "camera_";
            String ip = "IP_";
            CameraSettings cameraSetting = new CameraSettings();
            name = name.concat(String.valueOf(i));
            ip = ip.concat(String.valueOf(i));
            cameraSetting.setCameraName(name);
            cameraSetting.setCameraIP(ip);
            cameras.add(cameraSetting);
        }*//*

        // specify an adapter (see also next example)
        mCameraSettingAdapter = new CamerasSettingAdapter(cameras, this);
        mRecyclerView.setAdapter(mCameraSettingAdapter);*/
    }

/*    public static ArrayList<CameraSettings> getCamerasFromSharedPreferences(Context context){
        ArrayList<CameraSettings> cameras = new ArrayList<>();
        //SharedPreferences sharedPref = getActivity.(getBaseContext().MODE_PRIVATE);
        context.getApplicationContext();
        SharedPreferences sharedPref = get .getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains("CamerasIP")){
            Gson gson = new Gson();
            String json = sharedPref.getString("CamerasIP", null);
            Type type = new TypeToken<ArrayList<CameraSettings>>() {}.getType();
            cameras = gson.fromJson(json, type);
        }
        return cameras;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), CameraToolActivity.class);
        startActivity(intent);
        finish();
    }
}