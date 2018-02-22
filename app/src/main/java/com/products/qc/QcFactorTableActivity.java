package com.products.qc;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.products.qc.IcfactorDataReaderContract.IcfactorDataEntry;
import com.products.qc.IcfactorReaderContract.IcfactorEntry;
import com.products.qc.PalletReaderContract.PalletEntry;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class QcFactorTableActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qc_factor_table);
		
		updateTable();
		
		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
		
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(currentPallet);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.qc_factor_table, menu);
		SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
		if (currentPallet.equals("0") || Utils.sampledCount(this) == 0)
			menu.removeItem(R.id.action_send_data);
		Cursor icFactorTables =  QueryRepository.getAllQcFactorTables(this);
		if(icFactorTables.getCount() < 2)		
			menu.getItem(3).setVisible(false);
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
			case R.id.action_change_factors:
				ChageQcFactorDialogFragment changeQc_dialog = new ChageQcFactorDialogFragment();
				changeQc_dialog.show(getFragmentManager(), "changeQc_dialog");
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
	public void onStart()
	{
		super.onStart();
		if (AppConstant.restarting || AppConstant.freighting || AppConstant.resampling || AppConstant.mainMenu || AppConstant.signout)
			finish();
	}

	public void next(View view){
		
		SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		            	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));              	
		
		//int palletId = QueryRepository.getPalletIdByPalletId(sharedPref.getInt(getString(R.string.saved_current_pallet), 0), this);
    	int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
    	int tableNumber = QueryRepository.getTableNumberBySamplingId(this, samplingId);
		
		Cursor icFactors = QueryRepository.getIcFactor(this, tableNumber);
		Cursor icFactorsQ = QueryRepository.getIcFactorQ(this, tableNumber);
		Cursor icFactorsQC = QueryRepository.getIcFactorQC(this, tableNumber);
		Cursor icFactorsC = QueryRepository.getIcFactorC(this, tableNumber);
		
		TableLayout table = (TableLayout)findViewById(R.id.table);
		for(int i = 0; i < icFactorsQ.getCount(); i++){
			TableRow tr = (TableRow)table.getChildAt(i + 1);
			RelativeLayout rl = (RelativeLayout)tr.getChildAt(2);
			
			int icFactorButton = icFactorsQ.getInt(icFactorsQ.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_BUTTON));
			final int icFactorId = icFactorsQ.getInt(icFactorsQ.getColumnIndexOrThrow(IcfactorEntry._ID));
			
			if(icFactorButton == 1){
				Button button_plus = (Button)rl.findViewById(R.id.button_plus);
				TextView np = (TextView)rl.findViewById(R.id.tv_number);
				Button button_minus = (Button)rl.findViewById(R.id.button_minus);
				int newSl = Integer.parseInt(np.getText().toString());
	            QueryRepository.updateSlMAndS(this, icFactorId, newSl, 0, 0);
			}
			else{
				Button button_plus1 = (Button)rl.findViewById(R.id.button_plus1);
				TextView np1 = (TextView)rl.findViewById(R.id.tv_number1);
				Button button_minus1 = (Button)rl.findViewById(R.id.button_minus1);
				
				Button button_plus2 = (Button)rl.findViewById(R.id.button_plus2);
				TextView np2 = (TextView)rl.findViewById(R.id.tv_number2);
				Button button_minus2 = (Button)rl.findViewById(R.id.button_minus2);
				
				Button button_plus3 = (Button)rl.findViewById(R.id.button_plus3);
				TextView np3 = (TextView)rl.findViewById(R.id.tv_number3);
				Button button_minus3 = (Button)rl.findViewById(R.id.button_minus3);
				
				int newSl = Integer.parseInt(np1.getText().toString());
				int newM = Integer.parseInt(np2.getText().toString());
				int newS = Integer.parseInt(np3.getText().toString());
        	   
               QueryRepository.updateSlMAndS(this, icFactorId, newSl, newM, newS);
			}
			
			icFactorsQ.moveToNext();
		}
		
		for(int i = icFactorsQ.getCount(); i < icFactorsQ.getCount() + icFactorsQC.getCount(); i++){
			TableRow tr = (TableRow)table.getChildAt(i + 1);
			RelativeLayout rl = (RelativeLayout)tr.getChildAt(2);
			
			int icFactorButton = icFactorsQC.getInt(icFactorsC.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_BUTTON));
			final int icFactorId = icFactorsQC.getInt(icFactorsC.getColumnIndexOrThrow(IcfactorEntry._ID));
			
			if(icFactorButton == 1){
				Button button_plus = (Button)rl.findViewById(R.id.button_plus);
				TextView np = (TextView)rl.findViewById(R.id.tv_number);
				Button button_minus = (Button)rl.findViewById(R.id.button_minus);
				int newSl = Integer.parseInt(np.getText().toString());
	            QueryRepository.updateSlMAndS(this, icFactorId, newSl, 0, 0);
			}
			else{
				Button button_plus1 = (Button)rl.findViewById(R.id.button_plus1);
				TextView np1 = (TextView)rl.findViewById(R.id.tv_number1);
				Button button_minus1 = (Button)rl.findViewById(R.id.button_minus1);
				
				Button button_plus2 = (Button)rl.findViewById(R.id.button_plus2);
				TextView np2 = (TextView)rl.findViewById(R.id.tv_number2);
				Button button_minus2 = (Button)rl.findViewById(R.id.button_minus2);
				
				Button button_plus3 = (Button)rl.findViewById(R.id.button_plus3);
				TextView np3 = (TextView)rl.findViewById(R.id.tv_number3);
				Button button_minus3 = (Button)rl.findViewById(R.id.button_minus3);
				
				int newSl = Integer.parseInt(np1.getText().toString());
				int newM = Integer.parseInt(np2.getText().toString());
				int newS = Integer.parseInt(np3.getText().toString());
        	   
               QueryRepository.updateSlMAndS(this, icFactorId, newSl, newM, newS);
			}
			
			icFactorsQC.moveToNext();
		}
		
		for(int i = icFactorsQ.getCount() + icFactorsQC.getCount(); i < icFactors.getCount(); i++){
			TableRow tr = (TableRow)table.getChildAt(i + 1);
			RelativeLayout rl = (RelativeLayout)tr.getChildAt(2);
			
			int icFactorButton = icFactorsC.getInt(icFactorsC.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_BUTTON));
			final int icFactorId = icFactorsC.getInt(icFactorsC.getColumnIndexOrThrow(IcfactorEntry._ID));
			
			if(icFactorButton == 1){
				Button button_plus = (Button)rl.findViewById(R.id.button_plus);
				TextView np = (TextView)rl.findViewById(R.id.tv_number);
				Button button_minus = (Button)rl.findViewById(R.id.button_minus);
				int newSl = Integer.parseInt(np.getText().toString());
	            QueryRepository.updateSlMAndS(this, icFactorId, newSl, 0, 0);
			}
			else{
				Button button_plus1 = (Button)rl.findViewById(R.id.button_plus1);
				TextView np1 = (TextView)rl.findViewById(R.id.tv_number1);
				Button button_minus1 = (Button)rl.findViewById(R.id.button_minus1);
				
				Button button_plus2 = (Button)rl.findViewById(R.id.button_plus2);
				TextView np2 = (TextView)rl.findViewById(R.id.tv_number2);
				Button button_minus2 = (Button)rl.findViewById(R.id.button_minus2);
				
				Button button_plus3 = (Button)rl.findViewById(R.id.button_plus3);
				TextView np3 = (TextView)rl.findViewById(R.id.tv_number3);
				Button button_minus3 = (Button)rl.findViewById(R.id.button_minus3);
				
				int newSl = Integer.parseInt(np1.getText().toString());
				int newM = Integer.parseInt(np2.getText().toString());
				int newS = Integer.parseInt(np3.getText().toString());
        	   
               QueryRepository.updateSlMAndS(this, icFactorId, newSl, newM, newS);
			}
			
			icFactorsC.moveToNext();
		}
		
		Intent intent = new Intent(this, GridViewActivity.class);
	    startActivity(intent);
	}
	
	public void back(View view){
		finish();
	}
	
	public void updateTable(){
		TableLayout table = (TableLayout)findViewById(R.id.table);
		table.removeAllViews();
		
		TextView col1 = new TextView(this);
		col1.setText("QC Factor");
		col1.setPadding(3, 3, 3, 3);
		col1.setTextAppearance(this, android.R.style.TextAppearance_Large);
		col1.setTypeface(null, Typeface.BOLD);
		
		TextView col2 = new TextView(this);
		col2.setText("Factor Type");
		col2.setPadding(3, 3, 3, 3);
		col2.setTextAppearance(this, android.R.style.TextAppearance_Large);
		col2.setTypeface(null, Typeface.BOLD);
		
		TextView col3 = new TextView(this);
		col3.setText("Button Type");
		col3.setPadding(3, 3, 3, 3);
		col3.setTextAppearance(this, android.R.style.TextAppearance_Large);
		col3.setTypeface(null, Typeface.BOLD);
		
		TableRow head = new TableRow(this);
		
		head.addView(col1);
		head.addView(col2);
		head.addView(col3);
		
		table.addView(head);
		
		SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		            	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));              	
		
		//int palletId = QueryRepository.getPalletIdByPalletId(sharedPref.getInt(getString(R.string.saved_current_pallet), 0), this);
    	int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
    	int tableNumber = QueryRepository.getTableNumberBySamplingId(this, samplingId);
		
		Cursor icFactors = QueryRepository.getIcFactorQ(this, tableNumber);
		
		for(int i = 0; i < icFactors.getCount(); i++){
			final Activity activity = this;
			final int icFactorId = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry._ID));
			TextView tv = new TextView(this);
			//tv.setPadding(50, 0, 0, 0);
			tv.setTextSize(20);
			String icFactorName = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_NAME));
			
			tv.setText(icFactorName);
			TableRow row = new TableRow(this);
			row.addView(tv);
			
			TextView editFactor = new TextView(this);
			
			String factor = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_FACTOR));
			//spinner.setSelection(adapter.getPosition(factor));
			editFactor.setText(factor);
			editFactor.setTextSize(20);
			//editFactor.setGravity(17);
			row.addView(editFactor);
			
			
			int icFactorButton = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_BUTTON));
			
			Cursor c = QueryRepository.getIcFactorSlMAndS(activity, icFactorId);
			int sl = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_SL));
			int m = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_M));
			int s = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_S));
			
			if(icFactorButton == 1){
				LayoutInflater inflater = this.getLayoutInflater();
				
				RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.button_type1, null);
		        final TextView textView = (TextView) rl.findViewById(R.id.tv);
		        textView.setText("Count : " + String.valueOf(sl));
		        Button button_plus = (Button)rl.findViewById(R.id.button_plus);
				final TextView np = (TextView)rl.findViewById(R.id.tv_number);
				Button button_minus = (Button)rl.findViewById(R.id.button_minus);
		        
		        
		        //Set TextView text color
		        textView.setTextColor(Color.parseColor("#ffd32b3b"));

		        //Populate NumberPicker values from minimum and maximum value range
		        //Set the minimum value of NumberPicker
		        //np.setMinValue(0);
		        //Specify the maximum value/number of NumberPicker
		        //np.setMaxValue(999999);

		        //Gets whether the selector wheel wraps when reaching the min/max value.
		        //np.setWrapSelectorWheel(true);
		        
		        np.setText(String.valueOf(sl));

		        //Set a value change listener for NumberPicker
		        button_plus.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np.setText(String.valueOf(Integer.parseInt(np.getText().toString()) + 1));
						textView.setText("Count: " + np.getText());
					}
				});
				button_minus.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np.getText().toString()) > 0){
							np.setText(String.valueOf(Integer.parseInt(np.getText().toString()) - 1));
							textView.setText("Count: " + np.getText());
						}						
					}
				});
		        row.addView(rl);
			}			
			
			else{
				LayoutInflater inflater = this.getLayoutInflater();	    	

		        // Inflate and set the layout for the dialog
		        // Pass null as the parent view because its going in the dialog layout
		    	//Get the widgets reference from XML layout
		        RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.button_type2, null);
		        
		        final TextView textView = (TextView) rl.findViewById(R.id.tv);
		        textView.setText("Count: " + String.valueOf(sl + m + s));
		        final TextView tvsl = (TextView) rl.findViewById(R.id.TextView01);
		        tvsl.setText("SL: " + String.valueOf(sl));
		        final TextView tvm = (TextView) rl.findViewById(R.id.TextView02);
		        tvm.setText("M: " + String.valueOf(m));
		        final TextView tvs = (TextView) rl.findViewById(R.id.TextView03);
		        tvs.setText("S: " + String.valueOf(s));
		        
		        
		        Button button_plus1 = (Button)rl.findViewById(R.id.button_plus1);
				final TextView np1 = (TextView)rl.findViewById(R.id.tv_number1);
				Button button_minus1 = (Button)rl.findViewById(R.id.button_minus1);
				
				Button button_plus2 = (Button)rl.findViewById(R.id.button_plus2);
				final TextView np2 = (TextView)rl.findViewById(R.id.tv_number2);
				Button button_minus2 = (Button)rl.findViewById(R.id.button_minus2);
				
				Button button_plus3 = (Button)rl.findViewById(R.id.button_plus3);
				final TextView np3 = (TextView)rl.findViewById(R.id.tv_number3);
				Button button_minus3 = (Button)rl.findViewById(R.id.button_minus3);
		        
		        

		        //Set TextView text color
		        textView.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvsl.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvm.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvs.setTextColor(Color.parseColor("#ffd32b3b"));

		        //Populate NumberPicker values from minimum and maximum value range
		        //Set the minimum value of NumberPicker
		        /*np.setMinValue(0);
		        np1.setMinValue(0);
		        np2.setMinValue(0);
		        //Specify the maximum value/number of NumberPicker
		        np.setMaxValue(999999);
		        np1.setMaxValue(999999);
		        np2.setMaxValue(999999);

		        //Gets whether the selector wheel wraps when reaching the min/max value.
		        np.setWrapSelectorWheel(true);
		        np1.setWrapSelectorWheel(true);
		        np2.setWrapSelectorWheel(true);*/
		        
		        np1.setText(String.valueOf(sl));
		        np2.setText(String.valueOf(m));
		        np3.setText(String.valueOf(s));
	        
		        button_plus1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np1.setText(String.valueOf(Integer.parseInt(np1.getText().toString()) + 1));
						tvsl.setText("SL: " + np1.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus1.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np1.getText().toString()) > 0){
							np1.setText(String.valueOf(Integer.parseInt(np1.getText().toString()) - 1));
							tvsl.setText("SL: " + np1.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});		        
		        
		        
		        button_plus2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np2.setText(String.valueOf(Integer.parseInt(np2.getText().toString()) + 1));
						tvm.setText("M: " + np2.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus2.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np2.getText().toString()) > 0){
							np2.setText(String.valueOf(Integer.parseInt(np2.getText().toString()) - 1));
							tvm.setText("M: " + np2.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});
		        
				button_plus3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np3.setText(String.valueOf(Integer.parseInt(np3.getText().toString()) + 1));
						tvs.setText("S: " + np3.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus3.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np3.getText().toString()) > 0){
							np3.setText(String.valueOf(Integer.parseInt(np3.getText().toString()) - 1));
							tvs.setText("S: " + np3.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});  

				row.addView(rl);
			}
			table.addView(row);			
			
			icFactors.moveToNext();
		}
		icFactors = QueryRepository.getIcFactorQC(this, tableNumber);
		for(int i = 0; i < icFactors.getCount(); i++){
			final Activity activity = this;
			final int icFactorId = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry._ID));
			TextView tv = new TextView(this);
			//tv.setPadding(50, 0, 0, 0);
			tv.setTextSize(20);
			String icFactorName = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_NAME));
			
			tv.setText(icFactorName);
			TableRow row = new TableRow(this);
			row.addView(tv);
			
			TextView editFactor = new TextView(this);
			
			String factor = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_FACTOR));
			//spinner.setSelection(adapter.getPosition(factor));
			editFactor.setText(factor);
			editFactor.setTextSize(20);
			//editFactor.setGravity(17);
			row.addView(editFactor);
			
			
			int icFactorButton = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_BUTTON));
			
			Cursor c = QueryRepository.getIcFactorSlMAndS(activity, icFactorId);
			int sl = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_SL));
			int m = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_M));
			int s = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_S));
			
			if(icFactorButton == 1){
				LayoutInflater inflater = this.getLayoutInflater();
				
				RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.button_type1, null);
		        final TextView textView = (TextView) rl.findViewById(R.id.tv);
		        textView.setText("Count : " + String.valueOf(sl));
		        Button button_plus = (Button)rl.findViewById(R.id.button_plus);
				final TextView np = (TextView)rl.findViewById(R.id.tv_number);
				Button button_minus = (Button)rl.findViewById(R.id.button_minus);
		        
		        
		        //Set TextView text color
		        textView.setTextColor(Color.parseColor("#ffd32b3b"));

		        //Populate NumberPicker values from minimum and maximum value range
		        //Set the minimum value of NumberPicker
		        //np.setMinValue(0);
		        //Specify the maximum value/number of NumberPicker
		        //np.setMaxValue(999999);

		        //Gets whether the selector wheel wraps when reaching the min/max value.
		        //np.setWrapSelectorWheel(true);
		        
		        np.setText(String.valueOf(sl));

		        //Set a value change listener for NumberPicker
		        button_plus.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np.setText(String.valueOf(Integer.parseInt(np.getText().toString()) + 1));
						textView.setText("Count: " + np.getText());
					}
				});
				button_minus.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np.getText().toString()) > 0){
							np.setText(String.valueOf(Integer.parseInt(np.getText().toString()) - 1));
							textView.setText("Count: " + np.getText());
						}
						
					}
				});
		        row.addView(rl);
			}			
			
			else{
				LayoutInflater inflater = this.getLayoutInflater();	    	

		        // Inflate and set the layout for the dialog
		        // Pass null as the parent view because its going in the dialog layout
		    	//Get the widgets reference from XML layout
		        RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.button_type2, null);
		        
		        final TextView textView = (TextView) rl.findViewById(R.id.tv);
		        textView.setText("Count: " + String.valueOf(sl + m + s));
		        final TextView tvsl = (TextView) rl.findViewById(R.id.TextView01);
		        tvsl.setText("SL: " + String.valueOf(sl));
		        final TextView tvm = (TextView) rl.findViewById(R.id.TextView02);
		        tvm.setText("M: " + String.valueOf(m));
		        final TextView tvs = (TextView) rl.findViewById(R.id.TextView03);
		        tvs.setText("S: " + String.valueOf(s));
		        
		        
		        Button button_plus1 = (Button)rl.findViewById(R.id.button_plus1);
				final TextView np1 = (TextView)rl.findViewById(R.id.tv_number1);
				Button button_minus1 = (Button)rl.findViewById(R.id.button_minus1);
				
				Button button_plus2 = (Button)rl.findViewById(R.id.button_plus2);
				final TextView np2 = (TextView)rl.findViewById(R.id.tv_number2);
				Button button_minus2 = (Button)rl.findViewById(R.id.button_minus2);
				
				Button button_plus3 = (Button)rl.findViewById(R.id.button_plus3);
				final TextView np3 = (TextView)rl.findViewById(R.id.tv_number3);
				Button button_minus3 = (Button)rl.findViewById(R.id.button_minus3);
		        
		        

		        //Set TextView text color
		        textView.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvsl.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvm.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvs.setTextColor(Color.parseColor("#ffd32b3b"));

		        //Populate NumberPicker values from minimum and maximum value range
		        //Set the minimum value of NumberPicker
		        /*np.setMinValue(0);
		        np1.setMinValue(0);
		        np2.setMinValue(0);
		        //Specify the maximum value/number of NumberPicker
		        np.setMaxValue(999999);
		        np1.setMaxValue(999999);
		        np2.setMaxValue(999999);

		        //Gets whether the selector wheel wraps when reaching the min/max value.
		        np.setWrapSelectorWheel(true);
		        np1.setWrapSelectorWheel(true);
		        np2.setWrapSelectorWheel(true);*/
		        
		        np1.setText(String.valueOf(sl));
		        np2.setText(String.valueOf(m));
		        np3.setText(String.valueOf(s));
	        
		        button_plus1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np1.setText(String.valueOf(Integer.parseInt(np1.getText().toString()) + 1));
						tvsl.setText("SL: " + np1.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus1.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np1.getText().toString()) > 0){
							np1.setText(String.valueOf(Integer.parseInt(np1.getText().toString()) - 1));
							tvsl.setText("SL: " + np1.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});		        
		        
		        
		        button_plus2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np2.setText(String.valueOf(Integer.parseInt(np2.getText().toString()) + 1));
						tvm.setText("M: " + np2.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus2.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np2.getText().toString()) > 0){
							np2.setText(String.valueOf(Integer.parseInt(np2.getText().toString()) - 1));
							tvm.setText("M: " + np2.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});
		        
				button_plus3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np3.setText(String.valueOf(Integer.parseInt(np3.getText().toString()) + 1));
						tvs.setText("S: " + np3.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus3.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np3.getText().toString()) > 0){
							np3.setText(String.valueOf(Integer.parseInt(np3.getText().toString()) - 1));
							tvs.setText("S: " + np3.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});  

				row.addView(rl);
			}
			table.addView(row);			
			
			icFactors.moveToNext();
		}
		icFactors = QueryRepository.getIcFactorC(this, tableNumber);
		for(int i = 0; i < icFactors.getCount(); i++){
			final Activity activity = this;
			final int icFactorId = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry._ID));
			TextView tv = new TextView(this);
			//tv.setPadding(50, 0, 0, 0);
			tv.setTextSize(20);
			String icFactorName = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_NAME));
			
			tv.setText(icFactorName);
			TableRow row = new TableRow(this);
			row.addView(tv);
			
			TextView editFactor = new TextView(this);
			
			String factor = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_FACTOR));
			//spinner.setSelection(adapter.getPosition(factor));
			editFactor.setText(factor);
			editFactor.setTextSize(20);
			//editFactor.setGravity(17);
			row.addView(editFactor);
			
			
			int icFactorButton = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_BUTTON));
			
			Cursor c = QueryRepository.getIcFactorSlMAndS(activity, icFactorId);
			int sl = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_SL));
			int m = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_M));
			int s = c.getInt(c.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_S));
			
			if(icFactorButton == 1){
				LayoutInflater inflater = this.getLayoutInflater();
				
				RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.button_type1, null);
		        final TextView textView = (TextView) rl.findViewById(R.id.tv);
		        textView.setText("Count : " + String.valueOf(sl));
		        Button button_plus = (Button)rl.findViewById(R.id.button_plus);
				final TextView np = (TextView)rl.findViewById(R.id.tv_number);
				Button button_minus = (Button)rl.findViewById(R.id.button_minus);
		        
		        
		        //Set TextView text color
		        textView.setTextColor(Color.parseColor("#ffd32b3b"));

		        //Populate NumberPicker values from minimum and maximum value range
		        //Set the minimum value of NumberPicker
		        //np.setMinValue(0);
		        //Specify the maximum value/number of NumberPicker
		        //np.setMaxValue(999999);

		        //Gets whether the selector wheel wraps when reaching the min/max value.
		        //np.setWrapSelectorWheel(true);
		        
		        np.setText(String.valueOf(sl));

		        //Set a value change listener for NumberPicker
		        button_plus.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np.setText(String.valueOf(Integer.parseInt(np.getText().toString()) + 1));
						textView.setText("Count: " + np.getText());
					}
				});
				button_minus.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np.getText().toString()) > 0){
							np.setText(String.valueOf(Integer.parseInt(np.getText().toString()) - 1));
							textView.setText("Count: " + np.getText());
						}
						
					}
				});
		        row.addView(rl);
			}			
			
			else{
				LayoutInflater inflater = this.getLayoutInflater();	    	

		        // Inflate and set the layout for the dialog
		        // Pass null as the parent view because its going in the dialog layout
		    	//Get the widgets reference from XML layout
		        RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.button_type2, null);
		        
		        final TextView textView = (TextView) rl.findViewById(R.id.tv);
		        textView.setText("Count: " + String.valueOf(sl + m + s));
		        final TextView tvsl = (TextView) rl.findViewById(R.id.TextView01);
		        tvsl.setText("SL: " + String.valueOf(sl));
		        final TextView tvm = (TextView) rl.findViewById(R.id.TextView02);
		        tvm.setText("M: " + String.valueOf(m));
		        final TextView tvs = (TextView) rl.findViewById(R.id.TextView03);
		        tvs.setText("S: " + String.valueOf(s));
		        
		        
		        Button button_plus1 = (Button)rl.findViewById(R.id.button_plus1);
				final TextView np1 = (TextView)rl.findViewById(R.id.tv_number1);
				Button button_minus1 = (Button)rl.findViewById(R.id.button_minus1);
				
				Button button_plus2 = (Button)rl.findViewById(R.id.button_plus2);
				final TextView np2 = (TextView)rl.findViewById(R.id.tv_number2);
				Button button_minus2 = (Button)rl.findViewById(R.id.button_minus2);
				
				Button button_plus3 = (Button)rl.findViewById(R.id.button_plus3);
				final TextView np3 = (TextView)rl.findViewById(R.id.tv_number3);
				Button button_minus3 = (Button)rl.findViewById(R.id.button_minus3);
		        
		        

		        //Set TextView text color
		        textView.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvsl.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvm.setTextColor(Color.parseColor("#ffd32b3b"));
		        tvs.setTextColor(Color.parseColor("#ffd32b3b"));

		        //Populate NumberPicker values from minimum and maximum value range
		        //Set the minimum value of NumberPicker
		        /*np.setMinValue(0);
		        np1.setMinValue(0);
		        np2.setMinValue(0);
		        //Specify the maximum value/number of NumberPicker
		        np.setMaxValue(999999);
		        np1.setMaxValue(999999);
		        np2.setMaxValue(999999);

		        //Gets whether the selector wheel wraps when reaching the min/max value.
		        np.setWrapSelectorWheel(true);
		        np1.setWrapSelectorWheel(true);
		        np2.setWrapSelectorWheel(true);*/
		        
		        np1.setText(String.valueOf(sl));
		        np2.setText(String.valueOf(m));
		        np3.setText(String.valueOf(s));
	        
		        button_plus1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np1.setText(String.valueOf(Integer.parseInt(np1.getText().toString()) + 1));
						tvsl.setText("SL: " + np1.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus1.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np1.getText().toString()) > 0){
							np1.setText(String.valueOf(Integer.parseInt(np1.getText().toString()) - 1));
							tvsl.setText("SL: " + np1.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});		        
		        
		        
		        button_plus2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np2.setText(String.valueOf(Integer.parseInt(np2.getText().toString()) + 1));
						tvm.setText("M: " + np2.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus2.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np2.getText().toString()) > 0){
							np2.setText(String.valueOf(Integer.parseInt(np2.getText().toString()) - 1));
							tvm.setText("M: " + np2.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});
		        
				button_plus3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						np3.setText(String.valueOf(Integer.parseInt(np3.getText().toString()) + 1));
						tvs.setText("S: " + np3.getText());
						textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
					}
				});
				button_minus3.setOnClickListener(new OnClickListener() {
									
					@Override
					public void onClick(View v) {
						if(Integer.parseInt(np3.getText().toString()) > 0){
							np3.setText(String.valueOf(Integer.parseInt(np3.getText().toString()) - 1));
							tvs.setText("S: " + np3.getText());
							textView.setText("Count: " + String.valueOf(Integer.parseInt(np1.getText().toString()) + Integer.parseInt(np2.getText().toString()) + Integer.parseInt(np3.getText().toString())));
						}						
					}
				});  

				row.addView(rl);
			}
			table.addView(row);			
			
			icFactors.moveToNext();
		}
	}
}
