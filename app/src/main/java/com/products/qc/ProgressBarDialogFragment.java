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

public class ProgressBarDialogFragment extends DialogFragment {
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	LayoutInflater inflater = getActivity().getLayoutInflater();

    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.progress_bar_dialog, null));
        

        builder.setTitle("Sending Data...");
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
