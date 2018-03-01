package com.products.qc;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ButtonType1DialogFragment extends DialogFragment {
	int icFactorId;
	int sl;

    static public ButtonType1DialogFragment newInstance(int icFactorId, int sl) {
        ButtonType1DialogFragment f = new ButtonType1DialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("icFactorId", icFactorId);
        args.putInt("sl", sl);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	LayoutInflater inflater = getActivity().getLayoutInflater();

    	

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
    	//Get the widgets reference from XML layout
        RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.activity_button_type1, null);
        final TextView tv = (TextView) rl.findViewById(R.id.tv);
        tv.setText("Count : " + String.valueOf(sl));
        final NumberPicker np = (NumberPicker)rl.findViewById(R.id.np);
        
        
        //Set TextView text color
        tv.setTextColor(Color.parseColor("#ffd32b3b"));

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        np.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(999999);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);
        
        np.setValue(sl);

        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tv.setText("Count : " + newVal);
            }
        });        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rl);

        builder.setTitle("Button Type 1").setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   int newSl = np.getValue();
               QueryRepository.updateSlMAndS(getActivity(), icFactorId, newSl, 0, 0);
           }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               
           }
        });
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
