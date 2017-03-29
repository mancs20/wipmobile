package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

    public void back(View view) {
        this.finish();
    }
}
