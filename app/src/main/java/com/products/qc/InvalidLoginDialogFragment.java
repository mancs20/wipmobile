package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class InvalidLoginDialogFragment extends DialogFragment{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("Invalid Login")
	           .setMessage("Invalid User or Password");
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	  
	           }
	       });

	    return builder.create();
	}
}
