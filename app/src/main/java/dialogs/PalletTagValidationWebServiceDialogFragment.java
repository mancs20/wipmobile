package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.products.qc.AppConstant;
import com.products.qc.ConfirmationLocationDialogFragment;
import com.products.qc.InvalidPalletDialogFragment;
import com.products.qc.LocationIntroductionActivity;

public class PalletTagValidationWebServiceDialogFragment extends DialogFragment{

	public static String option = "";
	Activity activity;
	String palletTag;
	String locationTag;
	public PalletTagValidationWebServiceDialogFragment(Activity activity, String palletTag, String locationTag)
	{
		this.activity = activity;
		this.palletTag = palletTag;
		this.locationTag = locationTag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String[] options = new String[]{"True", "False"};


	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
				   if(option.equals("True")) {
					   ConfirmationLocationDialogFragment cldf = new ConfirmationLocationDialogFragment(activity, palletTag, locationTag, "remove");
					   cldf.show(activity.getFragmentManager(), "connproblem");
				   }
				   else {
					   Toast.makeText(activity, "The pallet don't exist or don't have a location", Toast.LENGTH_LONG).show();
				   }
	           }
	       });

	    return builder.create();
	}
}
