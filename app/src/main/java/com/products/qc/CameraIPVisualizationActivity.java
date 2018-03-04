package com.products.qc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
    Handler handler;


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
        btn = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);

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

    /*@SuppressLint("HandlerLeak")
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.d("State : ",msg.obj.toString());
        }
    };*/

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
}
