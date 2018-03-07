package com.products.qc;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OneCameraSettingActivity extends AppCompatActivity {

    boolean edit = false;
    EditText cameraName;
    EditText cameraIP;
    EditText cameraUser;
    EditText cameraPass;
    int idCameraPosition;
    private static final String PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_camera_setting);

        cameraName = (EditText) findViewById(R.id.edTxtCamName);
        cameraIP = (EditText) findViewById(R.id.edTxtCamIP);
        cameraUser = (EditText) findViewById(R.id.edTxtCamUser);
        cameraPass = (EditText) findViewById(R.id.edTxtCamPass);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ok_bar_button, menu);
        return true;
    }

    public void onOkButton(MenuItem item){
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

    private boolean ValidateFields(){
        boolean validation = true;
        if (cameraName.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(),R.string.toast_camera_name_empty,Toast.LENGTH_SHORT).show();
            validation = false;
        }else if(cameraIP.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(),R.string.toast_camera_ip_empty,Toast.LENGTH_SHORT).show();
            validation = false;
        }/*else{
            if((cameraIP.getText().toString().startsWith("http://")) || (cameraIP.getText().toString().startsWith("https://"))){
                int ipStarts = cameraIP.getText().toString().indexOf("//");
                String ip = cameraIP.getText().toString().substring(ipStarts);
                if (!validateIsIP(ip)){
                    Toast.makeText(getBaseContext(),R.string.toast_camera_ip_wrong,Toast.LENGTH_SHORT).show();
                    validation = false;
                }
            }else{
                Toast.makeText(getBaseContext(),R.string.toast_camera_ip_wrong,Toast.LENGTH_SHORT).show();
                validation = false;
            }
        }*/
        return validation;
    }

    private boolean validateIsIP(final String ip){
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
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
        onBackPressed();
    }
}
