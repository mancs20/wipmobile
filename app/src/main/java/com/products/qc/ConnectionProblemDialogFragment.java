package com.products.qc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConnectionProblemDialogFragment extends DialogFragment{

	/*Activity activity;
	public ConnectionProblemDialogFragment(Activity activity)
	{
		this.activity = activity;
	}*/

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("Connection Problem")
	           .setMessage("Fail to connect to server");
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	  
	           }
	       });

	    return builder.create();
	}
}
