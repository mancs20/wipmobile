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
            PalletValidationWebServiceDialogFragment pvws = new PalletValidationWebServiceDialogFragment(this, pallet);
            pvws.show(this.getFragmentManager(), "connproblem");
            rslt = "START";
            //CallerPallet c = new CallerPallet(this, pallet);
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

class CallerPallet extends Thread {
    Activity activity;
    String pallet;

    public CallerPallet(Activity activity, String pallet) {
        this.activity = activity;
        this.pallet = pallet;
    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/getIncomingbyTag";
            String OPERATION_NAME = "getIncomingbyTag";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Tag");
            pi.setValue(pallet);
            pi.setType(Integer.class);
            request.addProperty(pi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            Object response = null;
            try {
                httpTransport.call(SOAP_ACTION, envelope);
                response = envelope.getResponse();
            } catch (Exception exception) {
                cp.dismiss();
                response = exception.toString();
                ConnectionProblemDialogFragment cp2 = new ConnectionProblemDialogFragment(activity);
                cp2.show(activity.getFragmentManager(), "connproblem");
                this.stop();
            }
            //LoginActivity.rslt = response.toString();
            ((PalletIntroductionActivity)activity).palletCorrect();
            cp.dismiss();
        } catch (Exception ex) {

            cp.dismiss();

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Try again. Something was wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
