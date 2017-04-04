package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class InquireLocation52Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_location52);

        TextView descriptionText = (TextView)findViewById(R.id.location_description);
        descriptionText.setText(descriptionText.getText() + " " + InquireLocationActivity.rackDescription);

        int totalBalanceQty = 0;
        TableLayout table = (TableLayout) findViewById(R.id.location_table);
        for(int i = 0; i < InquireLocationActivity.racks.size(); i++) {
            TableRow row = new TableRow(this);

            TextView idText = new TextView(this);
            idText.setText(InquireLocationActivity.racks.get(i).getId());
            TextView invQtyText = new TextView(this);
            invQtyText.setText(InquireLocationActivity.racks.get(i).getIntQty());
            TextView idBalanceQty = new TextView(this);
            idBalanceQty.setText(InquireLocationActivity.racks.get(i).getBalanceQty());

            row.addView(idText);
            row.addView(invQtyText);
            row.addView(idBalanceQty);

            table.addView(row);

            totalBalanceQty += Integer.parseInt(InquireLocationActivity.racks.get(i).getBalanceQty());
        }

        TextView totalBalanceQtyText = (TextView)findViewById(R.id.total_balance_qty);
        totalBalanceQtyText.setText(totalBalanceQtyText.getText() + " " + totalBalanceQty);
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

    public void back(View view){this.finish();}
}
