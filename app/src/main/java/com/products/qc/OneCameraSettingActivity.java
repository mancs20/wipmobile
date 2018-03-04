package com.products.qc;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class OneCameraSettingActivity extends AppCompatActivity {

    boolean edit = false;
    EditText cameraName;
    EditText cameraIP;
    EditText cameraUser;
    EditText cameraPass;
    int idCameraPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_camera_setting);

        cameraName = (EditText) findViewById(R.id.edTxtCamName);
        cameraIP = (EditText) findViewById(R.id.edTxtCamIP);
        cameraUser = (EditText) findViewById(R.id.edTxtCamUser);
        cameraPass = (EditText) findViewById(R.id.edTxtCamPass);
        Button btnOk = (Button) findViewById(R.id.btnCreateEditCam);
        Button btnCancel = (Button) findViewById(R.id.btnCancelCreateEditCam);

        CameraSettings cameraSettings;

        Bundle b = getIntent().getExtras();
        if(b != null){
            edit = true;
            cameraSettings = b.getParcelable("cameraObject");
            if (cameraSettings != null) {
                cameraName.setText(cameraSettings.getCameraName());
                cameraIP.setText(cameraSettings.getCameraIP());
                cameraUser.setText(cameraSettings.getCameraUser());
                cameraPass.setText(cameraSettings.getCameraPassword());
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null ){
                    actionBar.setTitle(getString(R.string.edit_string) + " " + cameraSettings.getCameraName());
                }
            }
            idCameraPosition = b.getInt("cameraId");
        }


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidateFields()) {
                    ArrayList<CameraSettings> cameras = CameraSettings.getCamerasFromSharedPreferences(getBaseContext());
                    if (cameras != null){
                        if (!edit){
                            //add camera
                            cameras.add(CreateEditCamera());
                        }else{
                            CameraSettings editedCamera = CreateEditCamera();
                            cameras.set(idCameraPosition, editedCamera);
                        }
                        CameraSettings.saveCamerasToSharedPreferences(getBaseContext(),cameras);
                    }else{
                        //there are no cameras saved
                        ArrayList<CameraSettings> cameraAdd = new ArrayList<>();
                        cameraAdd.add(CreateEditCamera());
                        CameraSettings.saveCamerasToSharedPreferences(getBaseContext(),cameraAdd);
                    }

                    GoToCamerasSettings();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToCamerasSettings();
            }
        });
    }

    private boolean ValidateFields(){
        //TODO validate entries, ip
        boolean validation = true;
        if (cameraName.getText().toString().matches("")&&cameraIP.getText().toString().matches("")){
            Toast.makeText(getBaseContext(),R.string.toast_camera_name_or_ip_empty,Toast.LENGTH_LONG).show();
            validation = false;
        }
        return validation;
    }

    private CameraSettings CreateEditCamera(){
        CameraSettings camera = new CameraSettings();
        camera.setCameraName(cameraName.getText().toString());
        camera.setCameraIP(cameraIP.getText().toString());
        camera.setCameraUser(cameraUser.getText().toString());
        camera.setCameraPassword(cameraPass.getText().toString());
        return camera;
    }

    public void GoToCamerasSettings(){
        Intent intent = new Intent(getBaseContext(), CamerasSettingActivity.class);
        startActivity(intent);
        finish();
    }
}
