package com.products.qc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import dialogs.PalletTagValidationWebServiceDialogFragment;

public class RackInventoryActivity extends ActionBarActivity {

    public static String rackId;
    private EditText editTextRackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rack_inventory);

        editTextRackId = (EditText) findViewById(R.id.edit_text_rack_id);
        rackId = editTextRackId.getText().toString();
    }

    public void back(View view) {
        this.finish();
    }

    public void next(View view){
        if(editTextRackId.getText().toString().equals(""))
            Toast.makeText(this, "Enter rack id.", Toast.LENGTH_LONG).show();
        else {
//            PalletTagValidationWebServiceDialogFragment ptvwsdf = new PalletTagValidationWebServiceDialogFragment(this, editTextPallet.getText().toString(), "unknown");
//            ptvwsdf.show(this.getFragmentManager(), "connproblem");
            Intent intent = new Intent(this, InventoryQuantityActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (AppConstant.closing){
            //AppConstant.closing = false;
            finish();
        }
    }
}
