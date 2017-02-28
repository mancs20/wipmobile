package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.products.qc.AppConstant;
import com.products.qc.ChoiceToolsActivity;
import com.products.qc.InvalidLoginDialogFragment;
import com.products.qc.InvalidPalletDialogFragment;
import com.products.qc.LocationIntroductionActivity;

public class PalletValidationWebServiceDialogFragment extends DialogFragment{

	public static String option = "";
	Activity activity;
	String pallet;
	public PalletValidationWebServiceDialogFragment(Activity activity, String pallet)
	{
		this.activity = activity;
		this.pallet = pallet;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String[] options = new String[]{"Pallet doesn’t exist", "Pallet with location", "True"};


	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle("WebService Options").
		setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id) {
				if(id == 0)
					option = "Pallet doesn’t exist";
				if(id == 1) option = "Pallet with location";
				if(id == 2) option = "True";
			}
		});
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
				   if(option.equals("Pallet doesn’t exist")) {
					   InvalidPalletDialogFragment ip = new InvalidPalletDialogFragment(activity, "Pallet doesn’t exist");
					   ip.show(activity.getFragmentManager(), "connproblem");
				   }
				   if(option.equals("Pallet with location")){
					   InvalidPalletDialogFragment ip = new InvalidPalletDialogFragment(activity, "Pallet with location ");
					   ip.show(activity.getFragmentManager(), "connproblem");
				   }
				   if(option.equals("True")){
					   AppConstant.palletTag = pallet;
					   Intent intent = new Intent(activity, LocationIntroductionActivity.class);
					   startActivity(intent);
				   }
	           }
	       });

	    return builder.create();
	}
}
