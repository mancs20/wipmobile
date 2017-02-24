package com.products.qc;

import java.util.ArrayList;
import java.util.Iterator;

import com.products.qc.IcfactorReaderContract.IcfactorEntry;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ChageQcFactorDialogFragment extends DialogFragment {

	int tableNumber;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	
    	
    	
    	Cursor icFactors = QueryRepository.getAllQcFactorTables(getActivity());
    	String[] icFactor = new String[icFactors.getCount()];
    	
    	
    	for(int i = 0; i < icFactors.getCount(); i++){
    		icFactor[i] = "QCFactorTable" + String.valueOf(icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_TABLE)) + 1);
    		icFactors.moveToNext();
    	}
    	SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		            	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));              	
		
		//int palletId = QueryRepository.getPalletIdByPalletId(sharedPref.getInt(getString(R.string.saved_current_pallet), 0), getActivity());
    	int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
    	tableNumber = QueryRepository.getTableNumberBySamplingId(getActivity(), samplingId);
        builder.setTitle("QcFactorTables")
               .setSingleChoiceItems(icFactor, tableNumber, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   tableNumber = which;
	               }
               });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	SharedPreferences sharedPref = getActivity().getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        		            	
            	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));              	
        		
        		//int palletId = QueryRepository.getPalletIdByCode(sharedPref.getInt(getString(R.string.saved_current_pallet), 0), getActivity());
            	int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
            	QueryRepository.updateSamplingQcFactorTableNumber(getActivity(), tableNumber, samplingId);
                ((QcFactorTableActivity)getActivity()).updateTable();
            }
         });
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               
           }
        });
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
