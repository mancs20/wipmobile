package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class InquireLocationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_location);
    }

    public void back(View view) {
        this.finish();
    }

    public void next(View view) {
        Intent inquireLocation51Intent = new Intent(this, InquireLocation51Activity.class);
        startActivity(inquireLocation51Intent);
    }
}
