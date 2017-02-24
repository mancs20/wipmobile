package com.products.qc;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.products.qc.IcfactorDataReaderContract.IcfactorDataEntry;
import com.products.qc.IcfactorReaderContract.IcfactorEntry;
import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.PictureReaderContract.PictureEntry;
import com.products.qc.ProductReaderContract.ProductEntry;
import com.products.qc.SamplingReaderContract.SamplingEntry;

public abstract class ActionBarMethods {
	
	public static void restart(Activity context){
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase samplingdb = mDbSamplingHelper.getWritableDatabase();
        samplingdb.execSQL(SamplingEntry.SQL_DELETE_ENTRIES);
        samplingdb.close();
        mDbSamplingHelper.close();
        
		IcfactorReaderDbHelper mDbIcqfactorHelper = new IcfactorReaderDbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase icfactordb = mDbIcqfactorHelper.getWritableDatabase();
        icfactordb.execSQL(IcfactorEntry.SQL_DELETE_ENTRIES);
        icfactordb.close();
        mDbIcqfactorHelper.close();
		
		IcfactorDataReaderDbHelper mDbIcqfactorDataHelper = new IcfactorDataReaderDbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase icfactorDatadb = mDbIcqfactorDataHelper.getWritableDatabase();
        icfactorDatadb.execSQL(IcfactorDataEntry.SQL_DELETE_ENTRIES);
        icfactorDatadb.close();
        mDbIcqfactorDataHelper.close();
        
        PictureReaderDbHelper mDbPictureHelper = new PictureReaderDbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase picturedb = mDbPictureHelper.getWritableDatabase();
        picturedb.execSQL(PictureEntry.SQL_DELETE_ENTRIES);
        picturedb.close();
        mDbPictureHelper.close();
        
        PalletReaderDbHelper mDbPalleteHelper = new PalletReaderDbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase palletdb = mDbPalleteHelper.getWritableDatabase();
        palletdb.execSQL(PalletEntry.SQL_DELETE_ENTRIES);
        palletdb.close();
        mDbPalleteHelper.close();
        
        ProductReaderDbHelper mDbProductHelper = new ProductReaderDbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase productdb = mDbProductHelper.getWritableDatabase();
        productdb.execSQL(ProductEntry.SQL_DELETE_ENTRIES);
        productdb.close();
        mDbProductHelper.close();
        
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        
        File directory = context.getDir("images", Context.MODE_PRIVATE);
        directory.delete();
        
        if(context.getLocalClassName().equals("MainActivity")) {
        	AppConstant.restarting = false;
	        EditText codeEditText = (EditText) context.findViewById(R.id.edit_code);
	                	
			codeEditText.setText("");
        }
        else {
	    	AppConstant.restarting = true;
    	}
	}
	
	public static void status(Activity activity){
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		int currentPallet = sharedPref.getInt(activity.getString(R.string.saved_current_pallet), 0);
		
		ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
		
		Cursor products = QueryRepository.getAllProducts(activity);
		
		for (int i = 0; i < products.getCount(); i++) {
			TableRow tr = new TableRow(activity);
			TextView tv1 = new TextView(activity);
			TextView tv2 = new TextView(activity);
			
			int pid = QueryRepository.getPalletIdByCode(activity, currentPallet);
			Cursor pallet = QueryRepository.getPalletById(activity, pid);
			
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
			int count = QueryRepository.getSampledSamplingCountByProduct(activity, productId);
			String column2 = "   " + count + "/" + products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_MIN));
			
			tv1.setText(column1);
			tv2.setText(column2);
			tr.addView(tv1);
			tr.addView(tv2);
			
			tableRows.add(tr);
			
			products.moveToNext();
		}
		
		products.close();
		StatusDialogFragment status_dialog = new StatusDialogFragment(tableRows);		
		status_dialog.show(activity.getFragmentManager(), "status");
	}

	public static void freight(Activity activity) {
		DisplayManifestDialogFragment display_manifest_dialog = new DisplayManifestDialogFragment();		
		display_manifest_dialog.show(activity.getFragmentManager(), "display");
	}
}
