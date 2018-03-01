package com.products.qc;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class StatusDialogFragment extends DialogFragment {
	String[] adapter;
	TableLayout table;
	
	ArrayList<TableRow> tableRows;
	public StatusDialogFragment(ArrayList<TableRow> tableRows){
		this.tableRows = tableRows;
	}

	/**
	 * Create a new instance of MyDialogFragment, providing "num"
	 * as an argument.
	 */
	/*static StatusDialogFragment newInstance(ArrayList<TableRow> tableRows) {
		StatusDialogFragment f = new StatusDialogFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.arrayl("num", num);
		f.setArguments(args);

		return f;
	}*/
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	LayoutInflater inflater = getActivity().getLayoutInflater();

    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ScrollView scrollView = (ScrollView)inflater.inflate(R.layout.status_dialog, null);
        table = (TableLayout)scrollView.findViewById(R.id.table_dialog);
        for (TableRow row : tableRows) {
			table.addView(row);
		}
        builder.setView(scrollView);
        

        builder.setTitle("Status").setItems(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
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
