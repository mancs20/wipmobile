package com.products.qc;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.products.qc.IcfactorReaderContract.IcfactorEntry;
import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.PictureReaderContract.PictureEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

public class FinishSamplingDialogFragment extends DialogFragment{
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	
        builder.setTitle("Send Data");
        builder.setMessage("Finish quality control?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	//Enviar lo Datos al webservice
            	((StatusActivity)getActivity()).startSendingData();
            }
         });
        
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   	SendDataDialogFragment sendDatadialog = new SendDataDialogFragment();						
           		sendDatadialog.show(getFragmentManager(), "sendDatadialog");
           }
        });
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

