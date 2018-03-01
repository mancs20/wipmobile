package com.products.qc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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

import dialogs.LoginWebServiceDialogFragment;
import dialogs.RecuperationLoginWebServiceDialogFragment;

public class LoginRecuperationActivity extends AppCompatActivity {

    EditText emailEditText;
    public static String rslt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_recuperation);
        emailEditText = (EditText) findViewById(R.id.edit_email);

        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
                    sendEmail(null);
                }
                return false;
            }
        });
    }

    public void sendEmail(View view) {
        String email = emailEditText.getText().toString();
        if (email.equals("")) {
            Toast.makeText(LoginRecuperationActivity.this, "Enter Email", Toast.LENGTH_LONG).show();
        } else {
            //emailCorrect();
            //RecuperationLoginWebServiceDialogFragment rlws = new RecuperationLoginWebServiceDialogFragment(this);
            //rlws.show(this.getFragmentManager(), "connproblem");
            rslt = "START";
            CallerEmail c = new CallerEmail(this, email);
            c.start();
        }

    }

    public void cancel(View view){
        this.finish();
    }

    public void emailCorrect() {
        if (LoginRecuperationActivity.rslt.equals("anyType{}")) {
            //InvalidEmailDialogFragment ie = new InvalidEmailDialogFragment();
            //ie.show(this.getFragmentManager(), "connproblem");
            Toast.makeText(this, "Wrong user email", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(LoginRecuperationActivity.this, "The password was sent to your email", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }
}

class CallerEmail extends Thread {
    Activity activity;
    String email;

    public CallerEmail(Activity activity, String email) {
        this.activity = activity;
        this.email = email;
    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/forgotPassword";
            String OPERATION_NAME = "forgotPassword";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSECService/SecurityService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("User");
            pi.setValue(email);
            pi.setType(String.class);
            request.addProperty(pi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            Object response = null;
            //try {
                httpTransport.call(SOAP_ACTION, envelope);
                response = envelope.getResponse();
            //} catch (Exception exception) {
               // cp.dismiss();
                //response = exception.toString();
                //ConnectionProblemDialogFragment cp2 = new ConnectionProblemDialogFragment(activity);
                //cp2.show(activity.getFragmentManager(), "connproblem");
                //this.stop();
            //}
            LoginRecuperationActivity.rslt = response.toString();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    ((LoginRecuperationActivity)activity).emailCorrect();
                }
            });
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
