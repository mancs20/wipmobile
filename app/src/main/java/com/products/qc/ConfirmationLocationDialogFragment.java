package com.products.qc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class ConfirmationLocationDialogFragment extends DialogFragment{

	String palletTag;
	Activity activity;
	String location;
	String text;

	/*static public ConfirmationLocationDialogFragment newInstance(Activity activity, String palletTag, String location, String text) {
		ConfirmationLocationDialogFragment f = new ConfirmationLocationDialogFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		//args.putString("activity", activity);
		args.putString("palletTag", palletTag);
		args.putString("location", location);
		args.putString("text", text);
		f.setArguments(args);

		return f;
	}*/

	public ConfirmationLocationDialogFragment(Activity activity, String palletTag, String location, String text)
	{
		this.activity = activity;
		this.palletTag = palletTag;
		this.location = location;
		this.text = text;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("Confirmation Dialog")
	           .setMessage(text + " pallet " + palletTag + " in location " + location + "?");
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
				   //CallerPlacePallet c = new CallerPlacePallet(activity, location, AppConstant.palletTag);
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
