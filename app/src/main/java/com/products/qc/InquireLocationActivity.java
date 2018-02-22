package com.products.qc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import dialogs.LocationWebServiceDialogFragment;
import dialogs.PalletWebServiceDialogFragment;

public class InquireLocationActivity extends AppCompatActivity {
    public static String rslt = "";
    EditText editTextPallet;
    EditText editTextLocation;
    EditText editTextLot;
    RadioButton radioButtonPallet;
    RadioButton radioButtonLocation;
    RadioButton radioButtonLot;
    public static String invQty;
    public static String balanceQty;
    public static String rackIdSource;
    public static String palletTag;
    public static String locationTag;
    public static String lot;
    public static String size;

    public static String rackDescription;
    public static List<Rack> racks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_location);
        editTextPallet = (EditText) findViewById(R.id.edit_text_pallet);
        editTextLocation = (EditText) findViewById(R.id.edit_text_location);
        editTextLot=(EditText) findViewById(R.id.edit_text_lot);
        radioButtonPallet = (RadioButton) findViewById(R.id.radio_button_pallet);
        radioButtonLocation = (RadioButton) findViewById(R.id.radio_button_location);
        radioButtonLot= (RadioButton) findViewById(R.id.radio_button_lot);

        editTextLocation.setEnabled(false);

        editTextPallet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
                    next(null);
                }
                return false;
            }
        });

        editTextLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
                    next(null);
                }
                return false;
            }
        });

        editTextLot.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    public void back(View view) {
        this.finish();
    }

    public void checkPallet(View view){
        radioButtonLocation.setChecked(false);
        radioButtonLot.setChecked(false);

        editTextLot.getText().clear();
        editTextLocation.getText().clear();

        editTextLot.setEnabled(false);
        editTextLocation.setEnabled(false);

        editTextPallet.setEnabled(true);
        editTextPallet.requestFocus();
        editTextPallet.setSelected(true);
    }
    public void checkLocation(View view){
        radioButtonPallet.setChecked(false);
        radioButtonLot.setChecked(false);

        editTextPallet.getText().clear();
        editTextLot.getText().clear();

        editTextPallet.setEnabled(false);
        editTextLot.setEnabled(false);

        editTextLocation.setEnabled(true);
        editTextLocation.requestFocus();
        editTextLocation.setSelected(true);


    }

    public void checkLot(View view){

        radioButtonPallet.setChecked(false);
        radioButtonLocation.setChecked(false);

        editTextPallet.getText().clear();
        editTextLocation.getText().clear();

        editTextPallet.setEnabled(false);
        editTextLocation.setEnabled(false);

        editTextLot.setEnabled(true);
        editTextLot.requestFocus();
        editTextLot.setSelected(true);



    }

    public void next(View view) {

        if(radioButtonPallet.isChecked())
        {
            palletTag = editTextPallet.getText().toString();

            if(editTextPallet.getText().toString().equals(""))
                Toast.makeText(this, "The pallet was doesn't empty", Toast.LENGTH_LONG).show();
            else {
                //PalletWebServiceDialogFragment pwsdf = new PalletWebServiceDialogFragment(this);
                //pwsdf.show(this.getFragmentManager(), "connproblem");

                CallerTagInfo c = new CallerTagInfo(this, palletTag);
                c.start();
            }
        }
        else if(radioButtonLocation.isChecked()) {
            locationTag = editTextLocation.getText().toString();
            if(editTextLocation.getText().toString().equals(""))
                Toast.makeText(this, "The location was doesn't empty", Toast.LENGTH_LONG).show();
            else {

                CallerLocationInfo c = new CallerLocationInfo(this, locationTag);
                c.start();
            }
        }
        else if(radioButtonLot.isChecked()) {
            lot = editTextLot.getText().toString();
            if(editTextLot.getText().toString().equals(""))
                Toast.makeText(this, "The Lot was doesn't empty", Toast.LENGTH_LONG).show();
            else {

                CallerLotInfo c = new CallerLotInfo(this, lot);
                c.start();
            }
        }
    }
}

class CallerTagInfo extends Thread {
    Activity activity;
    String pallet;

    public CallerTagInfo(Activity activity, String pallet) {
        this.activity = activity;
        this.pallet = pallet;
    }

    public void tagCorrect(Activity activity) throws IOException, SAXException, ParserConfigurationException {
        if(InquireLocationActivity.rslt.equals("anyType{}")){
            Toast.makeText(activity, "Pallet is not in the database.", Toast.LENGTH_LONG).show();
        }
        else{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(InquireLocationActivity.rslt)));

            InquireLocationActivity.invQty =
                    document.getElementsByTagName("InvQty").getLength() != 0 ?
                            document.getElementsByTagName("InvQty").item(0).getTextContent() : "";
            InquireLocationActivity.balanceQty =
                    document.getElementsByTagName("BalanceQty").getLength() != 0 ?
                            document.getElementsByTagName("BalanceQty").item(0).getTextContent() : "";
            InquireLocationActivity.rackIdSource =
                    document.getElementsByTagName("Description").getLength() != 0 ?
                            document.getElementsByTagName("Description").item(0).getTextContent() : "";
            InquireLocationActivity.lot =
                    document.getElementsByTagName("lot").getLength() != 0 ?
                            document.getElementsByTagName("lot").item(0).getTextContent() : "";
            InquireLocationActivity.size =
                    document.getElementsByTagName("size").getLength() != 0 ?
                            document.getElementsByTagName("size").item(0).getTextContent() : "";

            Intent inquireLocation51Intent = new Intent(activity, InquireLocation51Activity.class);
            activity.startActivity(inquireLocation51Intent);
        }
    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/getTagInfo";
            String OPERATION_NAME = "getTagInfo";
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
            InquireLocationActivity.rslt = response.toString();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        tagCorrect(activity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
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

class CallerLocationInfo extends Thread {
    Activity activity;
    String rack;

    public CallerLocationInfo(Activity activity, String rack) {
        this.activity = activity;
        this.rack = rack;
    }

    public void tagCorrect(Activity activity) throws IOException, SAXException, ParserConfigurationException {
        if(InquireLocationActivity.rslt.equals("")){
            Toast.makeText(activity, "Pallet is not in the database.", Toast.LENGTH_LONG).show();
        }
        else{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(InquireLocationActivity.rslt)));

            InquireLocationActivity.rackDescription = document.getElementsByTagName("Description").item(0).getTextContent();
            InquireLocationActivity.racks = new ArrayList<>();

            NodeList nodes = document.getElementsByTagName("TagIdSource");
            for (int j = 0; j < nodes.getLength(); j++) {
                Node n = nodes.item(j);
                Rack rack = new Rack();
                NodeList nodes2 = n.getChildNodes();
                for (int k = 0; k < nodes2.getLength() && (rack.getId().equals("") ||
                        rack.getIntQty().equals("") || rack.getBalanceQty().equals("") || rack.getLot().equals("")|| rack.getSize().equals("")); k++) {
                    Node n2 = nodes2.item(k);
                    if (n2.getNodeName().equals("Id")) {
                        rack.setId(n2.getTextContent().trim());
                    } else if (n2.getNodeName().equals("InvQty")) {
                        rack.setIntQty(n2.getTextContent().trim());
                    } else if (n2.getNodeName().equals("BalanceQty")) {
                        rack.setBalanceQty(n2.getTextContent().trim());
                    }else if (n2.getNodeName().equals("lot")) {
                        rack.setLot(n2.getTextContent().trim());
                    }else if (n2.getNodeName().equals("size")) {
                        rack.setSize(n2.getTextContent().trim());
                    }
                }
                InquireLocationActivity.racks.add(rack);
            }
            Intent inquireLocation52Intent = new Intent(activity, InquireLocation52Activity.class);
            activity.startActivity(inquireLocation52Intent);
        }
    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/getRackInfo";
            String OPERATION_NAME = "getRackInfo";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSWMService/WMService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Rack");
            pi.setValue(rack);
            pi.setType(String.class);
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
            InquireLocationActivity.rslt = response.toString();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        tagCorrect(activity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
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

class CallerLotInfo extends Thread {
    Activity activity;
    String lot;

    public CallerLotInfo(Activity activity, String lot) {
        this.activity = activity;
        this.lot = lot;

    }


    public void callCorrect()
    {
        if (InquireLocationActivity.rslt!="") {
           //AppConstant.palletTag = pallet;
            AppConstant.currentLot = InquireLocationActivity.rslt;
            //activity.finish();


            Intent inquireLocation53Intent = new Intent(activity, InquireLocation53Activity.class);
            activity.startActivity(inquireLocation53Intent);
        }else
        {
            Toast.makeText(activity, "Lot is not in the database.", Toast.LENGTH_LONG).show();
            //ip.show(activity.getFragmentManager(), "connproblem");
        }

    }

    public void run() {
        ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            String SOAP_ACTION = "http://tempuri.org/getTagLotInfo";
            String OPERATION_NAME = "getTagLotInfo";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSWMService/WMService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Lot");
            pi.setValue(lot);
            pi.setType(String.class);
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
            InquireLocationActivity.rslt = response.toString();
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