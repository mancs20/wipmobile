package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

    public void back(View view){this.finish();}
}
