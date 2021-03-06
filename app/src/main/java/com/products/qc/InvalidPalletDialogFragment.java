package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class InvalidPalletDialogFragment extends DialogFragment{

	String error;
	Activity activity;
	public InvalidPalletDialogFragment(Activity activity, String error)
	{
		this.activity = activity;
		this.error = error;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("Invalid Pallet")
	           .setMessage(error);
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	  
	           }
	       });

	    return builder.create();
	}
}
