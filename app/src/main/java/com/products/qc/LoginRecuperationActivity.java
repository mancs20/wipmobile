package com.products.qc;

import android.app.Activity;
import android.content.Intent;
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

public class LoginRecuperationActivity extends ActionBarActivity {

    EditText emailEditText;
    public static String rslt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_recuperation);
        emailEditText = (EditText) findViewById(R.id.edit_email);
    }

    public void sendEmail(View view) {
        String email = emailEditText.getText().toString();
        if (email.equals("")) {
            Toast.makeText(LoginRecuperationActivity.this, "Enter Email", Toast.LENGTH_LONG).show();
        } else {
            emailCorrect();
            rslt = "START";
            CallerEmail c = new CallerEmail(this, email);
            //c.start();
        }

    }

    public void cancel(View view){
        this.finish();
    }

    public void emailCorrect()
    {
        if (LoginRecuperationActivity.rslt.equals("mm")) {
            InvalidEmailDialogFragment ie = new InvalidEmailDialogFragment(this);
            ie.show(this.getFragmentManager(), "connproblem");
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

            String SOAP_ACTION = "http://tempuri.org/getIncomingbyTag";
            String OPERATION_NAME = "getIncomingbyTag";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Tag");
            pi.setValue(email);
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
            ((LoginRecuperationActivity)activity).emailCorrect();
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
