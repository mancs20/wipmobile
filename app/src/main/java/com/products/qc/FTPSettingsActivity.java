package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FTPSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_settings);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new FtpSettingsFragment())
                .commit();
    }

}
