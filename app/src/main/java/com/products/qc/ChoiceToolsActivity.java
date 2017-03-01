package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class ChoiceToolsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_tools);
    }

    public void qualityControl(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void warehouseManagement(View view){
        Intent intent = new Intent(this, WarehouseManagementActivity.class);
        startActivity(intent);
    }

}
