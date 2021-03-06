package com.products.qc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
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


public class LoginActivity extends ActionBarActivity {
    EditText userEditText;
    EditText passwordEditText;
    public static String rslt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().setTitle("Login");
        userEditText = (EditText) findViewById(R.id.edit_user);
        passwordEditText = (EditText) findViewById(R.id.edit_password);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
                    login(null);
                }
                return false;
            }
        });
    }

    public void login(View view) {
//        // Pa probar
//        Intent intent = new Intent(this, ChoiceToolsActivity.class);
//        startActivity(intent);

        String user = userEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (user.equals("")) {
            Toast.makeText(LoginActivity.this, "Enter User", Toast.LENGTH_LONG).show();
        }
        else if (password.equals("")) {
            Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
        } else {
            //loginCorrect();
            //LoginWebServiceDialogFragment lws = new LoginWebServiceDialogFragment(this);
            //lws.show(this.getFragmentManager(), "connproblem");
            rslt = "START";
            Caller1 c = new Caller1(this, user, password);
            c.start();
        }
    }

    public void loginCorrect()
    {
        if (LoginActivity.rslt.equals("1"))
            Toast.makeText(this, "Wrong user name or password", Toast.LENGTH_LONG).show();
        else {
            AppConstant.user=userEditText.getText().toString();
            AppConstant.password=passwordEditText.getText().toString();
            Intent intent = new Intent(this, ChoiceToolsActivity.class);
            startActivity(intent);
        }
    }

    public void recLogin(View view){
        Intent intent = new Intent(this, LoginRecuperationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (AppConstant.signout)
            AppConstant.signout = false;
    }
}


class Caller1 extends Thread {
    Activity activity;
    String user;
    String password;

    public Caller1(Activity activity, String user, String password) {
        this.activity = activity;
        this.user = user;
        this.password = password;
    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/Login";
            String OPERATION_NAME = "Login";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSECService/SecurityService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("User");
            pi.setValue(user);
            pi.setType(String.class);
            request.addProperty(pi);

            PropertyInfo pi2 = new PropertyInfo();
            pi2.setName("Password");
            pi2.setValue(password);
            pi2.setType(String.class);
            request.addProperty(pi2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            Object response = null;
//            try {
                httpTransport.call(SOAP_ACTION, envelope);
                response = envelope.getResponse();
//            } catch (Exception exception) {
//                cp.dismiss();
                //response = exception.toString();
                //ConnectionProblemDialogFragment cp2 = new ConnectionProblemDialogFragment(activity);
                //cp2.show(activity.getFragmentManager(), "connproblem");
//                this.stop();
//            }
            LoginActivity.rslt = response.toString();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    ((LoginActivity)activity).loginCorrect();
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


