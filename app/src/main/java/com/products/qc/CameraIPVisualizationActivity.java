package com.products.qc;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import yjkim.mjpegviewer.MjpegView;

public class CameraIPVisualizationActivity extends AppCompatActivity {
    MjpegView mv;
    FloatingActionButton btn;
    ImageView imageView;
    String ip;
    String user;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraip_visualization);

        CameraSettings cameraSettings;
        Bundle b = getIntent().getExtras();
        if (b != null){
            cameraSettings = b.getParcelable("cameraObject");
            if (cameraSettings != null) {
                ip = "http://" + cameraSettings.getCameraIP() + "/mjpeg.cgi?";
                user = cameraSettings.getCameraUser();
                password = cameraSettings.getCameraPassword();
            } else {
                Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_SHORT).show();
            finish();
        }

        mv = (MjpegView) findViewById(R.id.videoView);
        btn = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);

        //TODO validation when somethign goes wrong e.g: user, password, ip
        mv.Start(ip, user, password, handler);

        int a = 1;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getVisibility() == View.INVISIBLE) {
                    Bitmap bitmap = mv.getImage();
                    mv.setVisibility(View.GONE);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    mv.Stop();

                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.d("State : ",msg.obj.toString());
        }
    };

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

    /*@Override
    protected void onPause() {
        super.onPause();
        mv.Stop();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv.Stop();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        GoToCamerasSettings();
    }

    public void GoToCamerasSettings(){
        Intent intent = new Intent(getBaseContext(), CameraToolActivity.class);
        startActivity(intent);
        finish();
    }*/
}
