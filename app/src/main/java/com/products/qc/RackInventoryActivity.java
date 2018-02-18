package com.products.qc;

import android.content.Intent;
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

import dialogs.PalletTagValidationWebServiceDialogFragment;

public class RackInventoryActivity extends ActionBarActivity {

    public static String rackId;
    private EditText editTextRackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rack_inventory);

        editTextRackId = (EditText) findViewById(R.id.edit_text_rack_id);

        editTextRackId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    @Override
    public void onStart()
    {
        super.onStart();

        editTextRackId.getText().clear();
        editTextRackId.requestFocus();
        editTextRackId.setSelected(true);

        if ( AppConstant.mainMenu || AppConstant.signout)
            finish();
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
            rackId = editTextRackId.getText().toString();
            Intent intent = new Intent(this, InventoryQuantityActivity.class);
            startActivity(intent);
        }
    }
}
