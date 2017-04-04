package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_signout:
                AppConstant.signout = true;
                this.finish();
                return true;
            case R.id.action_main_menu:
                AppConstant.mainMenu = true;
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (AppConstant.closing){
            AppConstant.closing = false;
            Toast.makeText(WarehouseManagementActivity.this, "Action successfully done.", Toast.LENGTH_LONG).show();
        }
        if (AppConstant.signout || AppConstant.mainMenu)
            this.finish();
    }
}
