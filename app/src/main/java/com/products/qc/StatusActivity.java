package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.ProductReaderContract.ProductEntry;

import java.util.ArrayList;
import java.util.TimerTask;

public class StatusActivity extends AppCompatActivity {
	
	public static String rslt="";
	QControlCaller c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		int currentPallet = sharedPref.getInt(getString(R.string.saved_current_pallet), 0);
		
		ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
		
		Cursor products = QueryRepository.getAllProducts(this);
		
		for (int i = 0; i < products.getCount(); i++) {
			TableRow tr = new TableRow(this);
			TextView tv1 = new TextView(this);
			TextView tv2 = new TextView(this);
			
			int pid = QueryRepository.getPalletIdByCode(this, currentPallet);
			Cursor pallet = QueryRepository.getPalletById(this, pid);
			
			if(products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_ENTRY_ID)) == 
					pallet.getInt(pallet.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_PRODUCT))) {
				tv1.setBackgroundColor(Color.GRAY);		
				tv2.setBackgroundColor(Color.GRAY);
				tv1.setTextColor(Color.WHITE);
				tv2.setTextColor(Color.WHITE);
			}				
			tv2.setGravity(Gravity.RIGHT);
			
			String column1 =
					products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_NAMESN)) +
					" / " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VARIETYSN)) +
					" / " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_STYLESN)) +
					" / " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SIZESN)) +
					" / " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_LABELSN));
					
			int productId = products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_ENTRY_ID));
			int count = QueryRepository.getSampledSamplingCountByProduct(this, productId);
			String column2 = "   " + count + "/" + products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_MIN));
			
			tv1.setText(column1);
			tv2.setText(column2);
			tr.addView(tv1);
			tr.addView(tv2);
			
			tableRows.add(tr);
			
			products.moveToNext();
		}
		
		products.close();
		LinearLayout ll = (LinearLayout)findViewById(R.id.ll);
		
		ScrollView scrollView = (ScrollView)ll.findViewById(R.id.scroll);
        TableLayout table = (TableLayout)scrollView.findViewById(R.id.table_status);
        for (TableRow row : tableRows) {
			table.addView(row);
		}
		
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(String.valueOf(currentPallet));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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
	
	public void next(View view){
		boolean requiredSampled = Utils.requiredSample(this);
        
        if (!requiredSampled) {
        	FinishSamplingDialogFragment finishDatadialog = new FinishSamplingDialogFragment();						
        	finishDatadialog.show(getFragmentManager(), "finishDatadialog");
        }
        else {
        	//AppConstant.resampling = true;
        	//finish();
        	SendDataDialogFragment sendDatadialog = new SendDataDialogFragment();						
        	sendDatadialog.show(getFragmentManager(), "sendDatadialog");
        }
	}
	
	public void finish(View view){
		FinishSamplingDialogFragment finishDatadialog = new FinishSamplingDialogFragment();						
    	finishDatadialog.show(getFragmentManager(), "finishDatadialog");
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
	
	public void startSendingData() {
		/*ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		Button next = (Button) findViewById(R.id.button_next);
		Button back = (Button) findViewById(R.id.button_back);
		next.setVisibility(4);
		back.setVisibility(4);
		pb.setVisibility(0);*/
		c = new QControlCaller(this);
		c.start();
		//pgc = new ProgressBarCaller(this);
		//pgc.start();
		//pgc.interrupt();
	}
	
	public void stopSendingData() {
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		Button next = (Button) findViewById(R.id.button_next);
		Button back = (Button) findViewById(R.id.button_back);
		next.setVisibility(View.VISIBLE);
		back.setVisibility(View.VISIBLE);
		pb.setVisibility(View.VISIBLE);
	}
}