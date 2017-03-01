package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RemoveConfirmationDialogFragment extends DialogFragment{

	Activity activity;
	String palletTag;
	String palletLocation;

	public RemoveConfirmationDialogFragment(){

	}

	public RemoveConfirmationDialogFragment(Activity activity, String palletTag, String palletLocation)
	{
		this.activity = activity;
		this.palletTag = palletTag;
		this.palletLocation = palletLocation;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("Remove Confirmation")
	           	.setMessage("Do you want to remove pallet " + palletTag + " from location " + palletLocation + " ?")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

	    return builder.create();
	}
}
