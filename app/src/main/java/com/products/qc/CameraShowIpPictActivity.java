package com.products.qc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraShowIpPictActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_show_ip_pict);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/

        Bitmap picture;
        String picName;
        Bundle b = getIntent().getExtras();
        ImageView pictureView = (ImageView) findViewById(R.id.pictureView);
        if (b != null){
            byte[] byteArray = b.getByteArray("image");
            picName = b.getString("picName");
            if (byteArray != null && picName != null){
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null){
                    actionBar.setTitle(picName);
                }
                picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                pictureView.setImageBitmap(picture);
            }else{
                Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_LONG).show();
                finish();
            }
        }else{
            Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload_bar_button, menu);
        return true;
    }

    public void upLoadPicture(MenuItem item){
        //TODO convert Bitmap and send it to ftp
        Toast.makeText(getBaseContext(), "Send picture to ftp", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), CameraToolActivity.class);
        startActivity(intent);
        finish();
    }

}


