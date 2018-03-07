package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

public class CameraToolActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EditText lotText;
    private EditText tagText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tool);

        lotText = (EditText) findViewById(R.id.edTxtLot);
        tagText = (EditText) findViewById(R.id.edTxtTag);

        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        /* use this setting to improve performance if you know that changes
         in content do not change the layout size of the RecyclerView*/
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Populate the list of adapter (cameras)
        populateAdapter(mRecyclerView);
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

    public void showFTPSettings(MenuItem item){
        // Display the fragment as the main content.
        Intent intent = new Intent(this, FTPSettingsActivity.class);
        startActivity(intent);
    }

    private void populateAdapter(RecyclerView mRecyclerView){
        ArrayList<CameraSettings> cameras = CameraSettings.getCamerasFromSharedPreferences(this.getBaseContext());
        if (cameras != null && !cameras.isEmpty()){
            // specify an adapter (see also next example)
            RecyclerView.Adapter mCameraSettingAdapter = new CameraToolAdapter(cameras, this, lotText, tagText);
            mRecyclerView.setAdapter(mCameraSettingAdapter);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ArrayList<CameraSettings> camRestart = CameraSettings.getCamerasFromSharedPreferences(this.getBaseContext());
        if ((camRestart != null) && (mRecyclerView.getAdapter() == null || (camRestart.size() != mRecyclerView.getAdapter().getItemCount()))){
            populateAdapter(mRecyclerView);
        }
    }
}
