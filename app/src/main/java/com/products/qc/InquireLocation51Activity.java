package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class InquireLocation51Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_location51);
        TextView palletID = (TextView)findViewById(R.id.pallet_id);
        TextView invQty = (TextView)findViewById(R.id.pallet_inv_qty);
        TextView balanceQty = (TextView)findViewById(R.id.pallet_balance_qty);
        TextView rackSourceId = (TextView)findViewById(R.id.pallet_rack_source_id);

        palletID.setText(palletID.getText() + " " + InquireLocationActivity.palletTag);
        invQty.setText(invQty.getText() + " " + InquireLocationActivity.invQty);
        balanceQty.setText(balanceQty.getText() + " " + InquireLocationActivity.balanceQty);
        rackSourceId.setText(rackSourceId.getText() + " " + InquireLocationActivity.rackIdSource);
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
        if (AppConstant.mainMenu || AppConstant.signout)
            finish();
    }

    public void back(View view) {
        this.finish();
    }
}
