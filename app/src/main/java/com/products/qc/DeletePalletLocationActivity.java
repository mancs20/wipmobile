package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import dialogs.PalletTagValidationWebServiceDialogFragment;
import dialogs.RecuperationLoginWebServiceDialogFragment;

public class DeletePalletLocationActivity extends ActionBarActivity {

    EditText editTextPallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pallet_location);

        editTextPallet = (EditText) findViewById(R.id.edit_text_pallet_tag);
    }

    public void back(View view) {
        this.finish();
    }

    public void next(View view){
        if(editTextPallet.getText().toString().equals(""))
            Toast.makeText(this, "The pallet tag was doesn't empty", Toast.LENGTH_LONG).show();
        else{
            PalletTagValidationWebServiceDialogFragment ptvwsdf = new PalletTagValidationWebServiceDialogFragment(this, editTextPallet.getText().toString(), "unknown");
            ptvwsdf.show(this.getFragmentManager(), "connproblem");
        }
    }
}
