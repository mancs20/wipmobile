package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RemovePicturesDialogFragment extends DialogFragment{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("Remove")
	           .setMessage("Remove this pictures?");
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   
	           }
	       });
	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	           }
	       });

	    return builder.create();
	}
}
