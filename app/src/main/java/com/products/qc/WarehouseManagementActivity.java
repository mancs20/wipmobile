package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

public class WarehouseManagementActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_management);
    }

    public void back(View view){
        this.finish();
    }

    public void placePallet(View view){
        Intent intent = new Intent(this, PalletIntroductionActivity.class);
        startActivity(intent);
    }

    public void inquiryLocation(View view){
        Intent intent = new Intent(this, InquireLocationActivity.class);
        startActivity(intent);
    }

    public void unlocatePallet(View view){
        Intent intent = new Intent(this, RackInventoryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (AppConstant.closing){
            AppConstant.closing = false;
            Toast.makeText(WarehouseManagementActivity.this, "Action successfully done.", Toast.LENGTH_LONG).show();
        }
    }
}
