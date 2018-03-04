package com.products.qc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import yjkim.mjpegviewer.MjpegView;

public class CameraIPVisualizationActivity extends AppCompatActivity {
    private MjpegView mv;
    private String ip;
    private String user;
    private String password;
    private Handler handler;
    private String picName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraip_visualization);

        CameraSettings cameraSettings;
        Bundle b = getIntent().getExtras();
        if (b != null){
            cameraSettings = b.getParcelable("cameraObject");
            picName = b.getString("picName");
            if (cameraSettings != null && picName != null) {
                ip = "http://" + cameraSettings.getCameraIP() + "/mjpeg.cgi?";
                user = cameraSettings.getCameraUser();
                password = cameraSettings.getCameraPassword();

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null){
                    actionBar.setTitle(cameraSettings.getCameraName());
                }
            } else {
                Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_SHORT).show();
            finish();
        }

        mv = (MjpegView) findViewById(R.id.videoView);
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        handler = new Handler(Looper.getMainLooper()) { /*
         * handleMessage() defines the operations to perform when
         * the Handler receives a new Message to process.
         */
            @Override
            public void handleMessage(Message inputMessage) {
                // Gets the image task from the incoming Message object.
                //PhotoTask photoTask = (PhotoTask) inputMessage.obj;
                String msg = (String) inputMessage.obj;

                if ((msg.equals(MjpegView.State.CONNECTION_ERROR.toString())) ||
                        (msg.equals(MjpegView.State.AUTHORIZATION_PROBLEM.toString())) ){
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };

        mv.Start(ip, user, password, handler);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = mv.getImage();
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();

                Bundle bundle = new Bundle();
                bundle.putByteArray("image" ,byteArray);
                bundle.putString("picName", picName);
                Intent intent = new Intent(getBaseContext(), CameraShowIpPictActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mv.Start(ip, user, password, handler);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mv.Stop();
    }
}
