package com.products.qc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.products.qc.IcfactorReaderContract.IcfactorEntry;
import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.PictureReaderContract.PictureEntry;
import com.products.qc.ProductReaderContract.ProductEntry;
import com.products.qc.SamplingReaderContract.SamplingEntry;
import android.support.v7.app.ActionBarActivity;

public class BrixPressureMeasurementsActivity extends ActionBarActivity {
	SharedPreferences sharedPref;
	ArrayAdapter<CharSequence> adapter;
	Spinner measurementsSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brix_pressure_measurements);
		
		sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		measurementsSpinner = (Spinner)findViewById(R.id.edit_measurements);
    	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
    	
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(currentPallet);
		
    	int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
		Cursor pallet = QueryRepository.getSamplingBrixPressureMeasurements(this, samplingId);
		
		Float brixValue = pallet.getFloat(pallet.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_BRIX));
		Float pressureValue = pallet.getFloat(pallet.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_PRESSURE));
		String measurementsValue = pallet.getString(pallet.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_MEASUREMENTS));
		
		EditText editBrix = (EditText)findViewById(R.id.edit_brix);
		EditText editPressure = (EditText)findViewById(R.id.edit_pressure);
		adapter = ArrayAdapter.createFromResource(this,
		        R.array.measurements, android.R.layout.simple_spinner_item);  
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		measurementsSpinner.setAdapter(adapter);		
		measurementsSpinner.setSelection(adapter.getPosition(measurementsValue));
		
		if (brixValue != null && brixValue != 0.0)
			editBrix.setText(brixValue.toString());
		if (pressureValue != null && brixValue != 0.0)
			editPressure.setText(pressureValue.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.brix_pressure_measurements, menu);
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
				RestartDialogFragment restart_dialog = new RestartDialogFragment(this);
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
			default:
				return super.onOptionsItemSelected(item);
    	}
    }
	
	public void next(View view) {
		Editor editor = sharedPref.edit();
		
		Float brixValue = null;
		Float pressureValue = null;
		String measurementsValue = "";			
		
		EditText editBrix = (EditText)findViewById(R.id.edit_brix);
		EditText editPressure = (EditText)findViewById(R.id.edit_pressure);
		
		if (!editBrix.getText().toString().equals(""))
			brixValue = Float.parseFloat(editBrix.getText().toString());
		if (!editPressure.getText().toString().equals(""))
			pressureValue = Float.parseFloat(editPressure.getText().toString());
		
		measurementsValue = (String)measurementsSpinner.getSelectedItem();
		
		int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
		QueryRepository.updateSamplingBrixPressureMeasurements(this, samplingId, brixValue, pressureValue, measurementsValue);
		QueryRepository.updateSamplingSampled(this, samplingId);
		
		Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        editor.putString(getString(R.string.saved_finish_date), format.format(date));
        editor.commit();
		
        Intent intent = new Intent(this, StatusActivity.class);
	    startActivity(intent);
	}
	
	public void back(View view) {
		finish();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (AppConstant.restarting || AppConstant.freighting || AppConstant.resampling ||
				AppConstant.mainMenu || AppConstant.signout)
			finish();
	}	
}
