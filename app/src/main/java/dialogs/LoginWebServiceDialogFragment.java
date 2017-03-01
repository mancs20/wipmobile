package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.products.qc.ChoiceToolsActivity;
import com.products.qc.InvalidLoginDialogFragment;

public class LoginWebServiceDialogFragment extends DialogFragment{

	public static String option = "";
	Activity activity;
	public LoginWebServiceDialogFragment(Activity activity)
	{
		this.activity = activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String[] options = new String[]{"True", "False"};


	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("WebService Options").
		setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id) {
				if(id == 1)
					option = "True";
				else option = "False";
			}
		});
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
				   if(option.equals("True")) {
					   InvalidLoginDialogFragment il = new InvalidLoginDialogFragment(activity);
					   il.show(activity.getFragmentManager(), "connproblem");
				   }
				   else{
					   Intent intent = new Intent(activity, ChoiceToolsActivity.class);
					   startActivity(intent);
				   }
	           }
	       });

	    return builder.create();
	}
}
