package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class DeletePalletLocationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pallet_location);
    }

    public void back(View view) {
        this.finish();
    }
}