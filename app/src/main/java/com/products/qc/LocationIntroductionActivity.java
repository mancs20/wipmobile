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

public class LocationIntroductionActivity extends ActionBarActivity {
    EditText locationEditText;
    public static String rslt = "";
    SharedPreferences sharedPref;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_introduction);
        locationEditText = (EditText) findViewById(R.id.location_enter);
    }

    public void back(View view){
        this.finish();
    }

    public void locationValidation(View view)
    {
        location = locationEditText.getText().toString();
        if (location.equals("")) {
            Toast.makeText(LocationIntroductionActivity.this, "Enter Location", Toast.LENGTH_LONG).show();
        } else {
            locationCorrect();
            rslt = "START";
            CallerLocation c = new CallerLocation(this, location);
            //c.start();
        }
    }

    public void locationCorrect()
    {
        if (LocationIntroductionActivity.rslt.equals("0")) {
            Toast.makeText(LocationIntroductionActivity.this, "Location with Pallet", Toast.LENGTH_LONG).show();
        }
        else{
            String palletTag = AppConstant.palletTag;
            ConfirmationLocationDialogFragment ip = new ConfirmationLocationDialogFragment(this, palletTag, location);
            ip.show(this.getFragmentManager(), "connproblem");
            if(rslt.equals("1")){
                rslt = "START";
                CallerLocation c = new CallerLocation(this, location);
                AppConstant.closing = true;
                this.finish();
            }
        }
    }
}

class CallerLocation extends Thread {
    Activity activity;
    String location;

    public CallerLocation(Activity activity, String location) {
        this.activity = activity;
        this.location = location;
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
            pi.setValue(location);
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
            ((LocationIntroductionActivity)activity).locationCorrect();
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
