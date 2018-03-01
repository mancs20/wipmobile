package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.products.qc.ChoiceToolsActivity;
import com.products.qc.InvalidEmailDialogFragment;
import com.products.qc.InvalidLoginDialogFragment;
import com.products.qc.LoginRecuperationActivity;

public class RecuperationLoginWebServiceDialogFragment extends DialogFragment{

	public static String option = "";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String[] options = new String[]{"True", "False"};


	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("WebService Options").
		setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id) {
				if(id == 1)
					option = "True";
				else option = "False";
			}
		});
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
				   if(option.equals("True")) {
					   Toast.makeText(getActivity(), "Incorrect email", Toast.LENGTH_LONG).show();
					   getActivity().finish();
				   }
				   else{
					   Toast.makeText(getActivity(), "The password was sent to your email", Toast.LENGTH_LONG).show();
					   getActivity().finish();
				   }
	           }
	       });

	    return builder.create();
	}
}
