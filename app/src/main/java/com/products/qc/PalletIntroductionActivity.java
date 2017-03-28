package com.products.qc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
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
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (AppConstant.closing){
            AppConstant.closing = false;
            finish();
        }
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


