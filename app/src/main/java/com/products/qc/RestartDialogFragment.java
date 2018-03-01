package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RestartDialogFragment extends DialogFragment{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("Restart")
	           .setMessage("Restart the application?");
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if(getActivity().getClass().getName().equals("com.products.qc.MainActivity")){
	        		   ActionBarMethods.restart(getActivity());
	        		   ((MainActivity)getActivity()).Restart();
	        	   }
	        	   else{
		        	   AppConstant.restarting = true;
					   getActivity().finish();
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
