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

import dialogs.LocationValidationWebServiceDialogFragment;

public class LocationIntroductionActivity extends ActionBarActivity {
    EditText locationEditText;
    public static String rslt = "";
    SharedPreferences sharedPref;
    String location;
    String pallet = AppConstant.palletTag;

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
            //locationCorrect();
            //LocationValidationWebServiceDialogFragment lvwsdf = new LocationValidationWebServiceDialogFragment(this, AppConstant.palletTag, location);
            //lvwsdf.show(this.getFragmentManager(), "connproblem");
            rslt = "START";
            CallerPalletLocation c = new CallerPalletLocation(this, pallet, location);
            c.start();
        }
    }


}

class CallerPalletLocation extends Thread {
    Activity activity;
    String pallet;
    String rack;

    public CallerPalletLocation(Activity activity, String pallet, String rack) {
        this.activity = activity;
        this.pallet = pallet;
        this.rack = rack;
    }

    public void locationCorrect()
    {
        switch (LocationIntroductionActivity.rslt) {
            case "0":
                ConfirmationLocationDialogFragment cldf = new ConfirmationLocationDialogFragment(activity, pallet, rack, "place ");
                cldf.show(activity.getFragmentManager(), "connproblem");
                break;
            case "1":
                Toast.makeText(activity, "Wrong Pallet Tag.", Toast.LENGTH_LONG).show();
                break;
            case "2":
                Toast.makeText(activity, "Wrong Location.", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(activity, "Wrong Data.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/getRackPallet";
            String OPERATION_NAME = "getRackPallet";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSWMService/WMService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Tag");
            pi.setValue(pallet);
            pi.setType(Long.class);
            request.addProperty(pi);

            PropertyInfo pi1 = new PropertyInfo();
            pi1.setName("Rack");
            pi1.setValue(pallet);
            pi1.setType(Integer.class);
            request.addProperty(pi1);

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
            LocationIntroductionActivity.rslt = response.toString();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    locationCorrect();
                }
            });
            //((PalletIntroductionActivity)activity).palletCorrect();
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

class CallerPlacePallet extends Thread {
    Activity activity;
    String pallet;
    String rack;

    public CallerPlacePallet(Activity activity, String pallet, String rack) {
        this.activity = activity;
        this.pallet = pallet;
        this.rack = rack;
    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/placePallet";
            String OPERATION_NAME = "placePallet";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSWMService/WMService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Tag");
            pi.setValue(pallet);
            pi.setType(Long.class);
            request.addProperty(pi);

            PropertyInfo pi1 = new PropertyInfo();
            pi.setName("Rack");
            pi.setValue(pallet);
            pi.setType(Integer.class);
            request.addProperty(pi1);

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