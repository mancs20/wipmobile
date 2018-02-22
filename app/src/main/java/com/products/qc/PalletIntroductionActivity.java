package com.products.qc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.io.*;
import org.xml.sax.*;

import dialogs.PalletValidationWebServiceDialogFragment;

public class PalletIntroductionActivity extends AppCompatActivity {

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
        palletEditText.getText().clear();
        palletEditText.requestFocus();
        palletEditText.setSelected(true);
        if (AppConstant.closing){
            AppConstant.closing = false;
            Toast.makeText(PalletIntroductionActivity.this, "Action successfully done.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(PalletIntroductionActivity.this, "Enter Pallet", Toast.LENGTH_LONG).show();
        } else {
            palletCorrect();

            //Intent intent = new Intent(this, LocationIntroductionActivity.class);
            //startActivity(intent);

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

            rslt = "START";
            CallerPalletLot c = new CallerPalletLot(this, pallet);
            c.start();
            //Intent intent = new Intent(this, LocationIntroductionActivity.class);
            //startActivity(intent);
        }
    }
}

class CallerPalletLot extends Thread {
    Activity activity;
    String pallet;
    String rack;

    public CallerPalletLot(Activity activity, String pallet) {
        this.activity = activity;
        this.pallet = pallet;

    }


    public void callCorrect()
    {
        if (PalletIntroductionActivity.rslt!="") {
            AppConstant.palletTag = pallet;
            AppConstant.currentLot = PalletIntroductionActivity.rslt;
            //activity.finish();


            Intent intent = new Intent(activity, LocationIntroductionActivity.class);
            activity.startActivity(intent);
        }else
        {
            InvalidPalletDialogFragment ip = new InvalidPalletDialogFragment(activity, "Pallet doesn’t exist");
            ip.show(activity.getFragmentManager(), "connproblem");
        }

    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/getTagFullInfo";
            String OPERATION_NAME = "getTagFullInfo";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSWMService/WMService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Tag");
            pi.setValue(pallet);
            pi.setType(Long.class);
            request.addProperty(pi);

            PropertyInfo pi2 = new PropertyInfo();
            pi2.setName("User");
            pi2.setValue(AppConstant.user);
            pi2.setType(String.class);
            request.addProperty(pi2);

            PropertyInfo pi3 = new PropertyInfo();
            pi3.setName("Password");
            pi3.setValue(AppConstant.password);
            pi3.setType(String.class);
            request.addProperty(pi3);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            Object response = null;
            //try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
//            } catch (Exception exception) {
//                cp.dismiss();
//                response = exception.toString();
//                ConnectionProblemDialogFragment cp2 = new ConnectionProblemDialogFragment(activity);
//                cp2.show(activity.getFragmentManager(), "connproblem");
//                this.stop();
//            }
            PalletIntroductionActivity.rslt = response.toString();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    callCorrect();
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


