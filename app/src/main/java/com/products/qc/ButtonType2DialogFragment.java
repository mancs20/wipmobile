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

public class ButtonType2DialogFragment extends DialogFragment {
	int icFactorId;
	int sl;
	int m;
	int s;
	
	public ButtonType2DialogFragment(int icFactorId, int sl, int m, int s){
		this.icFactorId = icFactorId;
		this.sl = sl;
		this.m = m;
		this.s = s;
	}	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	LayoutInflater inflater = getActivity().getLayoutInflater();

    	

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
    	//Get the widgets reference from XML layout
        RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.activity_button_type2, null);
        
        final TextView tv = (TextView) rl.findViewById(R.id.tv);
        tv.setText("Count: " + String.valueOf(sl + m + s));
        final TextView tvsl = (TextView) rl.findViewById(R.id.TextView01);
        tvsl.setText("SL: " + String.valueOf(sl));
        final TextView tvm = (TextView) rl.findViewById(R.id.TextView02);
        tvm.setText("M: " + String.valueOf(m));
        final TextView tvs = (TextView) rl.findViewById(R.id.TextView03);
        tvs.setText("S: " + String.valueOf(s));
        
        
        final NumberPicker np = (NumberPicker)rl.findViewById(R.id.np);
        final NumberPicker np1 = (NumberPicker)rl.findViewById(R.id.NumberPicker01);
        final NumberPicker np2 = (NumberPicker)rl.findViewById(R.id.NumberPicker02);
        
        

        //Set TextView text color
        tv.setTextColor(Color.parseColor("#ffd32b3b"));
        tvsl.setTextColor(Color.parseColor("#ffd32b3b"));
        tvm.setTextColor(Color.parseColor("#ffd32b3b"));
        tvs.setTextColor(Color.parseColor("#ffd32b3b"));

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        np.setMinValue(0);
        np1.setMinValue(0);
        np2.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(999999);
        np1.setMaxValue(999999);
        np2.setMaxValue(999999);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);
        np1.setWrapSelectorWheel(true);
        np2.setWrapSelectorWheel(true);
        
        np.setValue(sl);
        np1.setValue(m);
        np2.setValue(s);

        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tvsl.setText("SL: " + newVal);

                tv.setText("Count: " + String.valueOf(np.getValue() + np1.getValue() + np2.getValue()));
            }
        });
        
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tvm.setText("M: " + newVal);
                tv.setText("Count: " + String.valueOf(np.getValue() + np1.getValue() + np2.getValue()));
            }
        });
        
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tvs.setText("S: " + newVal);
                tv.setText("Count: " + String.valueOf(np.getValue() + np1.getValue() + np2.getValue()));
            }
        });        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rl);

        builder.setTitle("Button Type 2").setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   int newSl = np.getValue();
        	   int newM = np1.getValue();
        	   int newS = np2.getValue();
        	   
               QueryRepository.updateSlMAndS(getActivity(), icFactorId, newSl, newM, newS);
           }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               
           }
        });
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
