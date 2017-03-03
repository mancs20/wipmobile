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
import com.products.qc.InvalidLocationDialogFragment;
import com.products.qc.InvalidPalletDialogFragment;
import com.products.qc.LocationIntroductionActivity;

public class LocationValidationWebServiceDialogFragment extends DialogFragment{

	public static String option = "";
	Activity activity;
	String pallet;
	String location;
	public LocationValidationWebServiceDialogFragment(Activity activity, String pallet, String location)
	{
		this.activity = activity;
		this.pallet = pallet;
		this.location = location;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String[] options = new String[]{"Location doesn't exist", "Location is not empty", "True"};


	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("WebService Options").
		setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id) {
				if(id == 0)
					option = "Location doesn't exist";
				else if(id == 1) option = "Location is not empty";
				else option = "True";

			}
		});
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
				   switch (option) {
					   case "True":
						   ConfirmationLocationDialogFragment cldf = new ConfirmationLocationDialogFragment(activity, pallet, location, "place ");
						   cldf.show(activity.getFragmentManager(), "connproblem");
						   break;
					   case "Location doesn't exist":
						   Toast.makeText(activity, "Location doesn't exist", Toast.LENGTH_LONG).show();
						   break;
					   default:
						   Toast.makeText(activity, "Location is not empty", Toast.LENGTH_LONG).show();
						   break;
				   }
	           }
	       });

	    return builder.create();
	}
}
