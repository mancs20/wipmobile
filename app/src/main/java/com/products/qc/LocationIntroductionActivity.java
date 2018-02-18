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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import dialogs.LocationValidationWebServiceDialogFragment;

public class LocationIntroductionActivity extends ActionBarActivity {
    EditText locationEditText;
    TextView tv1;
    public static String rslt = "";
    SharedPreferences sharedPref;
    String location;
    String pallet = AppConstant.palletTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_introduction);
        locationEditText = (EditText) findViewById(R.id.location_enter);
        tv1=(TextView)findViewById(R.id.textView1);
        locationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
                    locationValidation(null);
                }
                return false;
            }
        });

        locationEditText.requestFocus();
        locationEditText.setSelected(true);

        loadLot();
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
        if (AppConstant.mainMenu || AppConstant.signout)
            finish();
    }

    public void loadLot()
    {

        String test="";


        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            StringReader sr = new StringReader( AppConstant.currentLot);
            InputSource is = new InputSource(sr);
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("LineLot");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    test = test + getValue("description", (Element) node) + " \n";

                    NodeList tagList = ((Element) node).getElementsByTagName("LineTag");
                    for (int j = 0; j < tagList.getLength(); j++) {

                        Node nodetag = tagList.item(j);
                        if (nodetag.getNodeType() == Node.ELEMENT_NODE) {
                            test = test + "     Tag: " + getValue("tagid", (Element) nodetag) + "   ";
                            test = test + "     InvQty:" + getValue("invqty", (Element) nodetag) + "   ";
                            test = test + "     BalanceQty: " + getValue("balanceqty", (Element) nodetag) + "   ";
                            test = test + "     Lot:" + getValue("lot", (Element) nodetag) + "   ";

                            test = test + "     Loc:" + getValue("loc", (Element) nodetag) + "   ";

                            test = test+"\n";
                        }

                    }
                }
                test=test+"--------------------------------------------\n";
            }tv1.setText(test);




        }catch(Exception ex)
        {

        }
    }

    private String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        if (nodeList.getLength()>0) {
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }
        return "";
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
//                ConfirmationLocationDialogFragment cldf = new ConfirmationLocationDialogFragment(activity, pallet, rack, "place ");
//                cldf.show(activity.getFragmentManager(), "connproblem");
                AppConstant.closing = true;
                activity.finish();
                //Intent intent = new Intent(activity, PalletIntroductionActivity.class);
                //activity.startActivity(intent);
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
            pi1.setName("Rack");
            pi1.setValue(rack);
            pi1.setType(String.class);
            request.addProperty(pi1);

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