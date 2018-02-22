package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ChoiceToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_tools);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void qualityControl(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void warehouseManagement(View view){
        Intent intent = new Intent(this, WarehouseManagementActivity.class);
        startActivity(intent);
    }

    public void cameraTool(View view){
        Intent intent = new Intent(this, CameraToolActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (AppConstant.signout)
            this.finish();
        if (AppConstant.mainMenu)
            AppConstant.mainMenu = false;
    }
}
