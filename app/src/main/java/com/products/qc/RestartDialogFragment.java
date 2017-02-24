package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RestartDialogFragment extends DialogFragment{

	Activity activity;
	public RestartDialogFragment(Activity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("Restart")
	           .setMessage("Do you wanna restart?");
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if(activity.getClass().getName().equals("com.products.qc.MainActivity")){
	        		   ActionBarMethods.restart(activity);
	        		   ((MainActivity)activity).Restart();
	        	   }
	        	   else{
		        	   AppConstant.restarting = true;
		               activity.finish();
	        	   }
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
