package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.products.qc.SamplingReaderContract.SamplingEntry;

public class TemperatureGrowerPlusActivity extends AppCompatActivity {
	SharedPreferences sharedPref;
	EditText edit_plus;
	TextView textview_plus;
	CheckBox plusCheckBox;
	public Menu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temperature_grower_plus);
		
		plusCheckBox = (CheckBox) findViewById(R.id.textview_plus);
		edit_plus = (EditText) findViewById(R.id.edit_plus);
		edit_plus.setEnabled(false);
		textview_plus = (TextView) findViewById(R.id.textView1);
		textview_plus.setEnabled(false);
		
		sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
    	
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(currentPallet);

		edit_plus.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
		if (currentPallet.equals("0") || Utils.sampledCount(this) == 0)
			menu.removeItem(R.id.action_send_data);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	switch (item.getItemId()) {
			case R.id.action_status:
				ActionBarMethods.status(this);
				return true;
			case R.id.action_restart:
				RestartDialogFragment restart_dialog = new RestartDialogFragment();
				restart_dialog.show(this.getFragmentManager(), "display");
				return true;
			case R.id.action_freight:
				ActionBarMethods.freight(this);
				return true;
			case R.id.action_signout:
				AppConstant.signout = true;
				this.finish();
				return true;
			case R.id.action_main_menu:
				AppConstant.mainMenu = true;
				this.finish();
				return true;
			case R.id.action_send_data:
				if (Utils.requiredSample(this)) {
					final Activity activity = this;
					new AlertDialog.Builder(this)
							.setTitle("Send Data")
							.setMessage("Quality Control uncompleted")
							.setNegativeButton(android.R.string.cancel, null) // dismisses by default
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which) {
									QControlCaller c = new QControlCaller(activity);
									c.start();
								}
							})
							.create()
							.show();
				} else {
					QControlCaller c = new QControlCaller(this);
					c.start();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
    	}
    }

	@Override
	protected void onRestart() {
		super.onRestart();
		onCreateOptionsMenu(menu);
	}
	
	public void back(View view) {
		this.finish();
	}
	
	public void next(View view) {
		EditText temperatureEditText = (EditText) findViewById(R.id.edit_temperature);
		if (temperatureEditText.getText().toString().equals("")) {
			Toast.makeText(TemperatureGrowerPlusActivity.this, "Enter Temperature", Toast.LENGTH_LONG).show();
		}
		else {
			EditText growerEditText = (EditText) findViewById(R.id.edit_grower);
			if (growerEditText.getText().toString().equals("")) {
				Toast.makeText(TemperatureGrowerPlusActivity.this, "Enter Grower", Toast.LENGTH_LONG).show();
			}
			else {
				float temp = Float.parseFloat(temperatureEditText.getText().toString());
				String grower = growerEditText.getText().toString();
				//int palletId = QueryRepository.getPalletIdByPalletId(sharedPref.getInt(getString(R.string.saved_current_pallet), 0), this);
				int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
				
				if (plusCheckBox.isChecked()) {
					EditText edit_plus = (EditText) findViewById(R.id.edit_plus);
					
					if (edit_plus.getText().toString().equals("")) {
						Toast.makeText(TemperatureGrowerPlusActivity.this, "Enter % Plus", Toast.LENGTH_LONG).show();
					}
					else {
						int plus = Integer.parseInt(edit_plus.getText().toString());
						if(plus < 0 || plus > 100)
							Toast.makeText(TemperatureGrowerPlusActivity.this, "Plus must be betwen 0 and 100", Toast.LENGTH_LONG).show();
						else{
							QueryRepository.updateSamplingByTempGrowerPlus(this, samplingId, temp, grower, plus);
							
							Intent intent = new Intent(this, QcFactorTableActivity.class);
						    startActivity(intent);
						}
					}
				}
				else {
					QueryRepository.updateSamplingByTempGrowerPlus(this, samplingId, temp, grower, null);
					
					Intent intent = new Intent(this, QcFactorTableActivity.class);
				    startActivity(intent);
				}
			}
		}
	}
	
	public void percentEnableDisable(View view) {
		edit_plus.setEnabled(!edit_plus.isEnabled());
		textview_plus.setEnabled(!textview_plus.isEnabled());
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (AppConstant.restarting || AppConstant.resampling || AppConstant.mainMenu || AppConstant.signout) {
			finish();
		}
		else if (AppConstant.freighting) {
			AppConstant.freighting = false;
			sharedPref = this.getSharedPreferences(
	                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
	    	
	    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
			int samplingId = QueryRepository.getSamplingIdByCode(this, Integer.parseInt(currentPallet));
			
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.saved_current_sampling), samplingId);
    	    editor.commit();
    	    
    	    Cursor sampling = QueryRepository.getSamplingTempGrowerPlus(this, samplingId);
    	    
			EditText edit_temperature = (EditText) findViewById(R.id.edit_temperature);
			edit_temperature.setText("");
			edit_temperature.requestFocus();
			
			EditText edit_grower = (EditText) findViewById(R.id.edit_grower);
			edit_grower.setText("");
    		
			edit_plus.setText("");
			edit_plus.setEnabled(false);
			textview_plus.setEnabled(false);
			plusCheckBox.setChecked(false);
		} else {
			//int palletId = QueryRepository.getPalletIdByPalletId(sharedPref.getInt(getString(R.string.saved_current_pallet), 0), this);
			int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
			Cursor sampling = QueryRepository.getSamplingTempGrowerPlus(this, samplingId);
			
			int columnIndexTemp = sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_TEMPERATURE);
			if (!sampling.isNull(columnIndexTemp)) {
				EditText edit_temperature = (EditText) findViewById(R.id.edit_temperature);
				edit_temperature.setText(String.valueOf(sampling.getFloat(columnIndexTemp)));
			}
			int columnIndexGrower = sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_GROWER);
			if (!sampling.isNull(columnIndexGrower)) {
				EditText edit_grower = (EditText) findViewById(R.id.edit_grower);
				edit_grower.setText(sampling.getString(columnIndexGrower));
			}
			
			int columnIndexPlus = sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_PLUS);
			if (!sampling.isNull(columnIndexPlus)) {
				edit_plus.setText(String.valueOf(sampling.getInt(columnIndexPlus)));
				edit_plus.setEnabled(true);
				textview_plus.setEnabled(true);
				plusCheckBox.setChecked(true);
			}
			
			sampling.close();
		}
	}	
}
