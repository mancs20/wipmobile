package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmationLocationDialogFragment extends DialogFragment{

	String palletTag;
	Activity activity;
	String location;
	public ConfirmationLocationDialogFragment(Activity activity, String palletTag, String location)
	{
		this.activity = activity;
		this.palletTag = palletTag;
		this.location = location;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("Confirmation Dialog")
	           .setMessage("Do you want to place pallet " + palletTag + " in location " + location + "?");
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
				   CallerLocation c = new CallerLocation(activity, location);
				   AppConstant.closing = true;
				   activity.finish();
	           }
	       });
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

	    return builder.create();
	}
}
