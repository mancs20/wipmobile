package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class InquireLocation51Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_location51);
    }

    public void back(View view) {
        this.finish();
    }
}
