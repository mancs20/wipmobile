package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.products.qc.ConfirmationLocationDialogFragment;
import com.products.qc.InquireLocation52Activity;
import com.products.qc.InvalidLocationDialogFragment;

public class LocationWebServiceDialogFragment extends DialogFragment{

	public static String option = "";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String[] options = new String[]{"True", "False"};


	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("WebService Options").
		setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id) {
				if(id == 0)
					option = "True";
				else option = "False";

			}
		});
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
				   if(option.equals("False")) {
					   Toast.makeText(getActivity(), "Location doesn't exist", Toast.LENGTH_LONG).show();
				   }
				   else{
					   Intent inquireLocation52Intent = new Intent(getActivity(), InquireLocation52Activity.class);
					   startActivity(inquireLocation52Intent);
				   }
	           }
	       });

	    return builder.create();
	}
}
