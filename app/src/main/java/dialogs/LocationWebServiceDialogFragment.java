package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.products.qc.ConfirmationLocationDialogFragment;
import com.products.qc.InquireLocation52Activity;
import com.products.qc.InvalidLocationDialogFragment;

public class LocationWebServiceDialogFragment extends DialogFragment{

	public static String option = "";
	Activity activity;
	public LocationWebServiceDialogFragment(Activity activity)
	{
		this.activity = activity;
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
				   if(option.equals("False")) {
					   InvalidLocationDialogFragment ildf = new InvalidLocationDialogFragment(activity, "Location doen't exist");
					   ildf.show(activity.getFragmentManager(), "connproblem");
				   }
				   else{
					   Intent inquireLocation52Intent = new Intent(activity, InquireLocation52Activity.class);
					   startActivity(inquireLocation52Intent);
				   }
	           }
	       });

	    return builder.create();
	}
}
