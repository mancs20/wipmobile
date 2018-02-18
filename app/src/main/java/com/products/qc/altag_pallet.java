package com.products.qc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class altag_pallet extends ActionBarActivity {

    EditText palletEditText;
    EditText rackEditText;
    public static String rslt = "";
    SharedPreferences sharedPref;
    String pallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_altag_pallet);
        palletEditText = (EditText) findViewById(R.id.pallet_enter);

        palletEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
                    palletValidation(null);
                }
                return false;
            }
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_menu, menu);

        return true;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        palletEditText.getText().clear();
        palletEditText.requestFocus();
        palletEditText.setSelected(true);
        if (AppConstant.closing){
            AppConstant.closing = false;
            Toast.makeText(altag_pallet.this, "Action successfully done.", Toast.LENGTH_LONG).show();
        }

        if (AppConstant.mainMenu || AppConstant.signout)
            finish();
    }

    public void back(View view){
        this.finish();
    }

    public void palletValidation(View view)
    {
        pallet = palletEditText.getText().toString();
        if (pallet.equals("")) {
            Toast.makeText(altag_pallet.this, "Enter Pallet", Toast.LENGTH_LONG).show();
        } else {
            //palletCorrect();
            AppConstant.palletTag = pallet;
            Intent intent = new Intent(this, altag_altag.class);
            startActivity(intent);
            //PalletValidationWebServiceDialogFragment pvws = new PalletValidationWebServiceDialogFragment(this, pallet);
            //pvws.show(this.getFragmentManager(), "connproblem");
            //rslt = "START";
            //CallerPallet c = new CallerPallet(this, pallet, rack);
            //c.start();
        }
    }

    public void palletCorrect()
    {
        if (altag_pallet.rslt.equals("Pallet doesn’t exist")) {
            InvalidPalletDialogFragment ip = new InvalidPalletDialogFragment(this, "Pallet doesn’t exist");
            ip.show(this.getFragmentManager(), "connproblem");
        }
        else if (altag_pallet.rslt.equals("Pallet with location ")) {
            InvalidPalletDialogFragment ip = new InvalidPalletDialogFragment(this, "Pallet with location ");
            ip.show(this.getFragmentManager(), "connproblem");
        }
        else{
            AppConstant.palletTag = pallet;
            Intent intent = new Intent(this, altag_altag.class);
            startActivity(intent);
        }
    }

}
