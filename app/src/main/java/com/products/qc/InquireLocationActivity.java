package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import dialogs.LocationWebServiceDialogFragment;
import dialogs.PalletWebServiceDialogFragment;

public class InquireLocationActivity extends ActionBarActivity {

    EditText editTextPallet;
    EditText editTextLocation;
    RadioButton radioButtonPallet;
    RadioButton radioButtonLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_location);
        editTextPallet = (EditText) findViewById(R.id.edit_text_pallet);
        editTextLocation = (EditText) findViewById(R.id.edit_text_location);
        radioButtonPallet = (RadioButton) findViewById(R.id.radio_button_pallet);
        radioButtonLocation = (RadioButton) findViewById(R.id.radio_button_location);


        editTextLocation.setEnabled(false);
    }

    public void back(View view) {
        this.finish();
    }

    public void checkPallet(View view){
        radioButtonLocation.setChecked(false);
        editTextLocation.getText().clear();
        editTextLocation.setEnabled(false);
        editTextPallet.setEnabled(true);
    }
    public void checkLocation(View view){
        radioButtonPallet.setChecked(false);
        editTextPallet.getText().clear();
        editTextPallet.setEnabled(false);
        editTextLocation.setEnabled(true);
    }

    public void next(View view) {
        if(radioButtonPallet.isChecked())
        {
            if(editTextPallet.getText().toString().equals(""))
                Toast.makeText(this, "The pallet was doesn't empty", Toast.LENGTH_LONG).show();
            else {
                PalletWebServiceDialogFragment pwsdf = new PalletWebServiceDialogFragment(this);
                pwsdf.show(this.getFragmentManager(), "connproblem");
                //Intent inquireLocation51Intent = new Intent(this, InquireLocation51Activity.class);
                //startActivity(inquireLocation51Intent);
            }
        }
        if(radioButtonLocation.isChecked()) {
            if(editTextLocation.getText().toString().equals(""))
                Toast.makeText(this, "The location was doesn't empty", Toast.LENGTH_LONG).show();
            else {
                LocationWebServiceDialogFragment lwsdf = new LocationWebServiceDialogFragment(this);
                lwsdf.show(this.getFragmentManager(), "connproblem");
                //Intent inquireLocation52Intent = new Intent(this, InquireLocation52Activity.class);
                //startActivity(inquireLocation52Intent);
            }
        }
    }
}
