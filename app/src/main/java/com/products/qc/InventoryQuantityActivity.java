package com.products.qc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

public class InventoryQuantityActivity extends ActionBarActivity {

    public static String rslt = "";
    private EditText editTextQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_quantity);

        editTextQuantity = (EditText) findViewById(R.id.edit_text_quantity);

        editTextQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
                    next(null);
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

    public void next(View view) {
        if(editTextQuantity.getText().toString().equals(""))
            Toast.makeText(this, "Enter quantity.", Toast.LENGTH_LONG).show();
        else {
            rslt = "START";
            String rackId = RackInventoryActivity.rackId;
            String quantity = editTextQuantity.getText().toString();
            CallerQuantity c = new CallerQuantity(this, rackId, quantity);
            c.start();
        }
    }

    public void back(View view) {
        this.finish();
    }
}

class CallerQuantity extends Thread {
    Activity activity;
    String rackId;
    String quantity;

    public CallerQuantity(Activity activity, String rackId, String quantity) {
        this.activity = activity;
        this.rackId = rackId;
        this.quantity = quantity;
    }

    public void locationCorrect()
    {
        switch (LocationIntroductionActivity.rslt) {
            case "0":
                AppConstant.closing = true;
                Toast.makeText(activity, "Quantity match correct.", Toast.LENGTH_LONG).show();
                activity.finish();
                break;
            case "1":
                Toast.makeText(activity, "Rack is not in the database.", Toast.LENGTH_LONG).show();
                break;
            case "2":
                Toast.makeText(activity, "Quantity is not the same as in the database.", Toast.LENGTH_LONG).show();
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

            String SOAP_ACTION = "http://tempuri.org/getRackQuantity";
            String OPERATION_NAME = "getRackQuantity";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSWMService/WMService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Rack");
            pi.setValue(rackId);
            pi.setType(Integer.class);
            request.addProperty(pi);

            PropertyInfo pi1 = new PropertyInfo();
            pi1.setName("Qty");
            pi1.setValue(quantity);
            pi1.setType(Integer.class);
            request.addProperty(pi1);

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
