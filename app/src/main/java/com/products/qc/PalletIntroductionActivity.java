package com.products.qc;

import android.app.Activity;
import android.content.Context;
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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import dialogs.PalletValidationWebServiceDialogFragment;

public class PalletIntroductionActivity extends ActionBarActivity {

    EditText palletEditText;
    EditText rackEditText;
    public static String rslt = "";
    SharedPreferences sharedPref;
    String pallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet_introduction);
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
        if (AppConstant.closing || AppConstant.mainMenu || AppConstant.signout)
            finish();
    }

    public void back(View view){
        this.finish();
    }

    public void palletValidation(View view)
    {
        pallet = palletEditText.getText().toString();
        if (pallet.equals("")) {
            Toast.makeText(PalletIntroductionActivity.this, "Enter Pallet", Toast.LENGTH_LONG).show();
        } else {
            //palletCorrect();
            AppConstant.palletTag = pallet;
            Intent intent = new Intent(this, LocationIntroductionActivity.class);
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
        if (PalletIntroductionActivity.rslt.equals("Pallet doesn’t exist")) {
            InvalidPalletDialogFragment ip = new InvalidPalletDialogFragment(this, "Pallet doesn’t exist");
            ip.show(this.getFragmentManager(), "connproblem");
        }
        else if (PalletIntroductionActivity.rslt.equals("Pallet with location ")) {
            InvalidPalletDialogFragment ip = new InvalidPalletDialogFragment(this, "Pallet with location ");
            ip.show(this.getFragmentManager(), "connproblem");
        }
        else{
            AppConstant.palletTag = pallet;
            Intent intent = new Intent(this, LocationIntroductionActivity.class);
            startActivity(intent);
        }
    }
}


