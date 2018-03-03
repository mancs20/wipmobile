package com.products.qc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class DisplayManifestDialogFragment extends DialogFragment {
	String[] adapter1;	
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	LayoutInflater inflater = getActivity().getLayoutInflater();

    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	
    	LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.display_manifest_dialog, null);
    	 ListView manifestListView = (ListView) ll.findViewById(R.id.manifest_listview_dialog);

    	 ArrayList<String> data = new ArrayList<String>();
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);
         manifestListView.setAdapter(adapter);
    	 
    	SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
		String warehouse_value = sharedPref.getString(getString(R.string.saved_warehouse), "");
		data.add("Warehouse: " + warehouse_value);        
		
		String warehouse_customer_value = sharedPref.getString(getString(R.string.saved_warehouse_customer), "");		
		data.add("Warehouse Customer: " + warehouse_customer_value);

        String referenceno_value = sharedPref.getString(getString(R.string.saved_referenceno), "");
        data.add("ReferenceNo: " + referenceno_value);
        
        String lot_value = sharedPref.getString(getString(R.string.saved_lot), "");
        data.add("Lot: " + lot_value);
        
        String description_value = sharedPref.getString(getString(R.string.saved_description), "");
        data.add("Description: " + description_value);
        
        String auxiliarreference_value = sharedPref.getString(getString(R.string.saved_auxiliarreference), "");
        data.add("AuxiliarReference: " + auxiliarreference_value);
        
        String shipperid_value = sharedPref.getString(getString(R.string.saved_shipperid), "");
        data.add("ShipperId: " + shipperid_value);
        
        String truckno_value = sharedPref.getString(getString(R.string.saved_truckno), "");
        data.add("TruckNo: " + truckno_value);
        
        String receiptdate_value = sharedPref.getString(getString(R.string.saved_receiptdate), "");
        data.add("DateReceived: " + receiptdate_value);
        
        builder.setView(ll);

        builder.setTitle("Display Manifest").setItems(adapter1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				
			}
		})
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               
           }
        });
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
