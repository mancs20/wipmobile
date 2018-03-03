package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CameraIPVisualizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_visualization);

        CameraSettings cameraSettings;
        Bundle b = getIntent().getExtras();
        if(b != null){
            cameraSettings = b.getParcelable("cameraObject");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GoToCamerasSettings();
    }

    public void GoToCamerasSettings(){
        Intent intent = new Intent(getBaseContext(), CameraToolActivity.class);
        startActivity(intent);
        finish();
    }
}
