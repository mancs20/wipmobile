package com.products.qc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.products.qc.IcfactorDataReaderContract.IcfactorDataEntry;
import com.products.qc.IcfactorReaderContract.IcfactorEntry;
import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.PictureReaderContract.PictureEntry;
import com.products.qc.ProductReaderContract.ProductEntry;
import com.products.qc.SamplingReaderContract.SamplingEntry;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends ActionBarActivity {
	
	public static String rslt="";
	QControlCaller c;
	TimerTask timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		int currentPallet = sharedPref.getInt(getString(R.string.saved_current_pallet), 0);
		
		ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
		
		Cursor products = QueryRepository.getAllProducts(this);
		
		for (int i = 0; i < products.getCount(); i++) {
			TableRow tr = new TableRow(this);
			TextView tv1 = new TextView(this);
			TextView tv2 = new TextView(this);
			
			int pid = QueryRepository.getPalletIdByCode(this, currentPallet);
			Cursor pallet = QueryRepository.getPalletById(this, pid);
			
			if(products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_ENTRY_ID)) == 
					pallet.getInt(pallet.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_PRODUCT))) {
				tv1.setBackgroundColor(Color.GRAY);		
				tv2.setBackgroundColor(Color.GRAY);
				tv1.setTextColor(Color.WHITE);
				tv2.setTextColor(Color.WHITE);
			}				
			tv2.setGravity(Gravity.RIGHT);
			
			String column1 =
					products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_NAMESN)) +
					" mmmmmmmmmmmmmmmmmmmmmmmm/ " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VARIETYSN)) +
					" / " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_STYLESN)) +
					" / " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SIZESN)) +
					" / " + products.getString(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_LABELSN));
					
			int productId = products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_ENTRY_ID));
			int count = QueryRepository.getSampledSamplingCountByProduct(this, productId);
			String column2 = "   " + count + "/" + products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_MIN));
			
			tv1.setText(column1);
			tv2.setText(column2);
			tr.addView(tv1);
			tr.addView(tv2);
			
			tableRows.add(tr);
			
			products.moveToNext();
		}
		
		products.close();
		LinearLayout ll = (LinearLayout)findViewById(R.id.ll);
		
		ScrollView scrollView = (ScrollView)ll.findViewById(R.id.scroll);
        TableLayout table = (TableLayout)scrollView.findViewById(R.id.table_status);
        for (TableRow row : tableRows) {
			table.addView(row);
		}
		
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(String.valueOf(currentPallet));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
	        case R.id.action_status:
	            ActionBarMethods.status(this);
	            return true;
	        case R.id.action_restart:
	        	RestartDialogFragment restart_dialog = new RestartDialogFragment(this);		
	    		restart_dialog.show(this.getFragmentManager(), "display");
	            return true;
	        case R.id.action_freight:
	        	ActionBarMethods.freight(this);
	        	return true;
			case R.id.action_signout:
				AppConstant.signout = true;
				this.finish();
				return true;
			case R.id.action_main_menu:
				AppConstant.mainMenu = true;
				this.finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	public void next(View view){
		Cursor products = QueryRepository.getAllProducts(this);
        boolean requiredSampled = false;
        
        for (int i = 0; i < products.getCount(); i++) {
        	int min = products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_MIN));
        	int productId = products.getInt(products.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_ENTRY_ID));
        	int sampled = QueryRepository.getSampledSamplingCountByProduct(this, productId);
        	
        	if (sampled < min) {
        		requiredSampled = true;
        		//SendDataDialogFragment sendDatadialog = new SendDataDialogFragment();						
           		//sendDatadialog.show(getFragmentManager(), "sendDatadialog");
        		break;
        	}
        	products.moveToNext();
		}
        
        if (!requiredSampled) {
        	FinishSamplingDialogFragment finishDatadialog = new FinishSamplingDialogFragment();						
        	finishDatadialog.show(getFragmentManager(), "finishDatadialog");
        }
        else {
        	//AppConstant.resampling = true;
        	//finish();
        	SendDataDialogFragment sendDatadialog = new SendDataDialogFragment();						
        	sendDatadialog.show(getFragmentManager(), "sendDatadialog");
        }
	}
	
	public void finish(View view){
		FinishSamplingDialogFragment finishDatadialog = new FinishSamplingDialogFragment();						
    	finishDatadialog.show(getFragmentManager(), "finishDatadialog");
	}
	
	public void back(View view) {
		finish();
	}
	@Override
	public void onStart()
	{
		super.onStart();
		if (AppConstant.restarting || AppConstant.freighting || AppConstant.resampling ||
				AppConstant.mainMenu || AppConstant.signout)
			finish();
	}
	
	public void startSendingData() {
			/*ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
			Button next = (Button) findViewById(R.id.button_next);
			Button back = (Button) findViewById(R.id.button_back);
			next.setVisibility(4);
			back.setVisibility(4);
			pb.setVisibility(0);*/
			c = new QControlCaller(this);
			c.start();
			//pgc = new ProgressBarCaller(this);
			//pgc.start();
			//pgc.interrupt();
	}
	
	public void stopSendingData() {
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		Button next = (Button) findViewById(R.id.button_next);
		Button back = (Button) findViewById(R.id.button_back);
		next.setVisibility(View.VISIBLE);
		back.setVisibility(View.VISIBLE);
		pb.setVisibility(View.VISIBLE);
		
	}
}

class QControlCaller extends Thread
{
	Activity activity;
	ProgressBarDialogFragment cp;
	
	public QControlCaller(Activity activity){
		this.activity = activity;
	}
    public void run() {
    	
        try {
        	cp = new ProgressBarDialogFragment();
        	cp.setCancelable(false);
        	cp.show(activity.getFragmentManager(), "sendingdata");
        	
        	SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        	SharedPreferences.Editor editor = sharedPref.edit();
        	
        	String response = "";
        	if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontrol), 0) == 0) {
	        	response = connectToWebService("saveQControl", CreateQControl());
	    	    editor.putInt(activity.getString(R.string.saved_saveqcontrol), 1);
	    	    editor.putString(activity.getString(R.string.saved_responsecontrol), response);
	    	    editor.commit();
        	} else {
        		response = sharedPref.getString(activity.getString(R.string.saved_responsecontrol), "");
        	}
        	boolean sendQcontroldetail = false, 
        			sendQcontroldetailFactor = false, 
        			sendQcontroldetailPicture = false;
            Cursor pallets = QueryRepository.getAllPallets(activity);
            for (int i = 0; i < pallets.getCount(); i++) {
            	int palletId = pallets.getInt(pallets.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_ENTRY_ID));
            	int productId = QueryRepository.getProductIdByPalletId(activity, palletId);
            	
            	Cursor samplings = QueryRepository.getAllSamplingByPallet(activity, palletId);
            	Cursor qcFactorTable = QueryRepository.getIcFactorsByProduct(activity, productId);
            	for (int j = 0; j < samplings.getCount(); j++) {
            		int samplingId = samplings.getInt(samplings.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_ENTRY_ID));
            		String qControlDetailString = CreateQControlDetail(response, pallets, samplings);
            		String response2 = "";
            		if (sendQcontroldetail || sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetail), -1) == -1) {
	            		response2 = connectToWebService("saveQcontrolDetail", qControlDetailString);
            			editor.putInt(activity.getString(R.string.saved_saveqcontroldetail), samplingId);
            			editor.putString(activity.getString(R.string.saved_responsecontroldetail), response2);
            			editor.commit();
            			sendQcontroldetail = true;
            		} else if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetail), -1) == samplingId) {
            			response2 = sharedPref.getString(activity.getString(R.string.saved_responsecontroldetail), "");
            			editor.putInt(activity.getString(R.string.saved_saveqcontroldetail), -1);
            			editor.commit();
            			sendQcontroldetail = true;
            		}
            		int tableNumber = QueryRepository.getTableNumberBySamplingId(activity, samplingId);
        			Cursor qcFactorDataTable = QueryRepository.getQCFactorTablesByNumberAndSamplingId(activity, samplingId, tableNumber);
        			for (int k = 0; k < qcFactorDataTable.getCount(); k++) {
        				int qcFactorDataTableId = qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataEntry._ID));
        				
        				if (sendQcontroldetailFactor || sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailfactor), -1) == -1) {
        					editor.putInt(activity.getString(R.string.saved_saveqcontroldetailfactor), qcFactorDataTableId);
	            			editor.commit();
	        				String qControlDetailFactor = CreateQControlDetailFactor(response2, qcFactorDataTable, qcFactorTable);
	        				String response3 = connectToWebService("saveQcontrolDetailFactor", qControlDetailFactor);
	        				sendQcontroldetailFactor = true;
	        			} else if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailfactor), -1) == qcFactorDataTableId) {
                			editor.putInt(activity.getString(R.string.saved_saveqcontroldetailfactor), -1);
                			editor.commit();
                			sendQcontroldetailFactor = true;
                		}
        				qcFactorDataTable.moveToNext();
    				}
        			Cursor pictures = QueryRepository.getAllPictureBySampling(activity, samplingId);        		
            		for (int k = 0; k < pictures.getCount(); k++) {
            			int pictureId = pictures.getInt(pictures.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_ENTRY_ID));
        				
        				if (sendQcontroldetailPicture || sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailpicture), -1) == -1) {
	            			String picName = pictures.getString(pictures.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_NAME));
	            			String qControlDetailPicture = CreateQControlDetailPicture(response2, pictures);
	            			
	            			Bitmap bitmap = scalePicture(picName);
							String qPicture = CreatePicture(pictures, bitmap);
							String response4 = connectToWebServicePicture("saveQcontrolDetailPicture", qControlDetailPicture, qPicture);
							editor.putInt(activity.getString(R.string.saved_saveqcontroldetailpicture), pictureId);
	            			editor.commit();
							sendQcontroldetailPicture = true;
        				} else if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailpicture), -1) == pictureId) {
                			editor.putInt(activity.getString(R.string.saved_saveqcontroldetailpicture), -1);
                			editor.commit();
                			sendQcontroldetailPicture = true;
                		}
            			pictures.moveToNext();
            		}
            		samplings.moveToNext();
				}
            	pallets.moveToNext();
			}
            AppConstant.restarting = true;
            AppConstant.dataSended = true;
            activity.finish();
            MainActivity.rslt=response.toString();
        } catch(Exception ex) {
        	MainActivity.rslt=ex.toString();
        	cp.dismiss();

			activity.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(activity, "Try again. Something was wrong", Toast.LENGTH_LONG).show();
				}
			});
    	}
    }
    
    public String connectToWebService(String OPERATION_NAME, String data){
    	String SOAP_ACTION = "http://tempuri.org/" + OPERATION_NAME;
        String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("objInput");
        pi.setValue(data);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception) {
        	cp.dismiss();
            response=exception.toString();
            ConnectionProblemDialogFragment cp = new ConnectionProblemDialogFragment(activity);
        	cp.show(activity.getFragmentManager(), "connproblem");
        	this.stop();
        }
        return response.toString();
    }
    
    public String connectToWebServicePicture(String OPERATION_NAME, String data, String picture){
    	String SOAP_ACTION = "http://tempuri.org/" + OPERATION_NAME;
        String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("objInput");
        pi.setValue(data);
        pi.setType(String.class);
        request.addProperty(pi);
        
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("str64Picture");
        pi2.setValue(picture);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception) {
        	cp.dismiss();
            response=exception.toString();
            ConnectionProblemDialogFragment cp = new ConnectionProblemDialogFragment(activity);
        	cp.show(activity.getFragmentManager(), "connproblem");
        	this.stop();
        }
        return response.toString();
    }
    
    public String CreateQControl() {
    	
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		//Build first XML ************************************************************************************
		//***************************************************************************************************
		String beginDateString = sharedPref.getString(activity.getString(R.string.saved_begin_date), "");
		String finishDateString = sharedPref.getString(activity.getString(R.string.saved_finish_date), "");
		String incomingIdString = sharedPref.getString(activity.getString(R.string.saved_incoming_id), "");
		String manifestIdString = sharedPref.getString(activity.getString(R.string.saved_manifest_id), "");
		
        return "<Qcontrol>"+
	            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
	            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"+
	            "<Id>0</Id>"+
	            "<BeginDateTime>" + beginDateString.substring(0, 10) + "T" + beginDateString.substring(11, 19) + "</BeginDateTime>"+
	            "<FinishDateTime>" + finishDateString.substring(0, 10) + "T" + finishDateString.substring(11, 19) + "</FinishDateTime>"+
	            "<IncomingId>" + incomingIdString + "</IncomingId>"+
	            "<ManifestId>" + manifestIdString + "</ManifestId>"+
	            "</Qcontrol>";
    }
    
    public String CreateQControlDetail(String qControlId, Cursor pallet, Cursor sampling) {
    				
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);        
        
        String tagIdString = String.valueOf(pallet.getInt(pallet.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_CODE)));
        
        String temperatureString = String.valueOf(sampling.getFloat(sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_TEMPERATURE)));
        String growerString = sampling.getString(sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_GROWER));
        String plusString = String.valueOf(sampling.getInt(sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_PLUS)));
        
        String brixString = String.valueOf(sampling.getFloat(sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_BRIX)));
        String pressureString = String.valueOf(sampling.getFloat(sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_PRESSURE)));
        String measurementsString = String.valueOf(sampling.getInt(sampling.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_MEASUREMENTS)));
        	
		String beginDateString = sharedPref.getString(activity.getString(R.string.saved_begin_date), "");
		String finishDateString = sharedPref.getString(activity.getString(R.string.saved_finish_date), "");
		
        return "<QcontrolDetail>" +
			        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
			        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
			        "<Id>0</Id>" +
			        "<QcontrolId>" + qControlId + "</QcontrolId>" +
			        "<TagPallet>" + tagIdString + "</TagPallet>" +
			        "<BeginDateTime>" + beginDateString.substring(0, 10) + "T" + beginDateString.substring(11, 19) + "</BeginDateTime>"+
			        "<FinishDateTime>" + finishDateString.substring(0, 10) + "T" + finishDateString.substring(11, 19) + "</FinishDateTime>"+
			        "<Temperature>" + temperatureString + "</Temperature>" +
			        "<Grower>" + growerString + "</Grower>" +
			        "<PlusPercent>" + plusString + "</PlusPercent>" +
			        "<Brix>" + brixString + "</Brix>" +
                    "<Pressure>" + pressureString + "</Pressure>" +
                    "<Measurements>" + measurementsString + "</Measurements>" +
			        "<QcontrolDetailFactorCollection />" +
			        "<QcontrolDetailPictureCollection />" +
			    "</QcontrolDetail>";
    }
    
    public String CreateQControlDetailFactor(String qControlDetailId, Cursor qcFactorDataTable, Cursor qcFactorTable) {
		
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
        String qCFactorDescriptionString = qcFactorDataTable.getString(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_NAME));
		String qCFactorValueString = qcFactorDataTable.getString(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_FACTOR));
		String qcFactorId = String.valueOf(qcFactorTable.getInt(qcFactorTable.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_QCFACTORID)));
		String order = String.valueOf(qcFactorTable.getInt(qcFactorTable.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_ORDER)));
		int qCFactorTypeInt = qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_BUTTON));
		String sl = String.valueOf(qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_SL)));
		String m = String.valueOf(qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_M)));
		String s = String.valueOf(qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataEntry.COLUMN_NAME_S)));
		
        return "<QcontrolDetailFactor>" +
			        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
			        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
			        "<Id>0</Id>" +
			        "<QcontrolDetailId>" + qControlDetailId + "</QcontrolDetailId>" +
			        "<QcFactorId>" + qcFactorId + "</QcFactorId>" +
			        "<QcFactorDescription>" + qCFactorDescriptionString + "</QcFactorDescription>" +
			        "<QcFactorValue1>" + sl + "</QcFactorValue1>" +
			        "<QcFactorValue2>" + (qCFactorTypeInt == 1 ? "" : m) + "</QcFactorValue2>" +
			        "<QcFactorValue3>" + (qCFactorTypeInt == 1 ? "" : s) + "</QcFactorValue3>" +
			        "<QcFactorType>" + (qCFactorValueString.equals("Quality") ? "Q" : (qCFactorValueString.equals("Condition") ? "C" : "Q&amp;C")) + "</QcFactorType>" +
			        "<Order>" + order + "</Order>" +
			    "</QcontrolDetailFactor>";
    }
    
	public String CreateQControlDetailPicture(String qControlDetailId, Cursor pictures) {
				
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);        
        
		String fileNameString = pictures.getString(pictures.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_NAME));
		String directory = activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + fileNameString;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(directory, options);
		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
	    String pictureString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
		
        return "<QcontrolDetailPicture>" +
			        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
			        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
			        "<Id>0</Id>" +				        
			        "<FileName>" + fileNameString + "</FileName>" +
			        "<Picture> </Picture>" +	
			        "<QcontrolDetailId>" + qControlDetailId + "</QcontrolDetailId>" +
			    "</QcontrolDetailPicture>";
    }

	/*public String CreatePicture(Cursor pictures) {
		
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);        
        
		String fileNameString = pictures.getString(pictures.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_NAME));
		String directory = activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + fileNameString;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(directory, options);
		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
	    String pictureString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
		
        return pictureString;
    }*/
	public String CreatePicture(Cursor pictures , Bitmap bitmap) {

		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

		String fileNameString = pictures.getString(pictures.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_NAME));
		String directory = activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + fileNameString;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		//Bitmap bitmap = BitmapFactory.decodeFile(directory, options);
		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
		String pictureString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);

		return pictureString;
	}
	
	/*private void scalePicture(String pictureName) throws IOException {
    	File f = new File(activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + pictureName);
    	
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        FileOutputStream fos = new FileOutputStream(f);
        
        // Scale Picture
        Bitmap scale = null;
        long fileSize = f.length() / 1024;
        while (fileSize > 200) {
        	scale = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 3 / 4, bitmap.getHeight() * 3 / 4, true);
        	fos = new FileOutputStream(f);
            scale.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            fileSize = f.length() / 1024;
        }
        fos.close();
    }*/
	private Bitmap scalePicture(String pictureName) throws IOException {
		// Rotate Picture
		File f = new File(activity.getDir("images", Context.MODE_PRIVATE) + File.separator + pictureName);
		Bitmap bitmap= decodeFile(f);

/*
	//*long fileSize = f.length() / 1024;
		Boolean existe = f.exists(); //Si es verdadero existe el fichero

        BitmapFactory.Options options = new BitmapFactory.Options();
        //Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
		Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);


        FileOutputStream fos = new FileOutputStream(f);

        existe = f.exists();
		existe = f.isFile();
		f.setReadable(true);
		f.setWritable(true);

        // Scale Picture
        Bitmap scale = null;
		long fileSize= f.length()/1024;
        while (fileSize > 200) {
        	scale = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 3 / 4, bitmap.getHeight() * 3 / 4, true);
        	fos = new FileOutputStream(f);
            scale.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            fileSize = f.length() / 1024;
        }
        fos.close();*/
		boolean deleted = f.delete();
		return bitmap;
	}

	private Bitmap decodeFile(File f){
		try {
			//decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);
			//Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE=120;
			int width_tmp=o.outWidth, height_tmp=o.outHeight;
			int scale=1;
			while(true){
				if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
					break;
				width_tmp/=2;
				height_tmp/=2;
				scale++;
			}

			//decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}
}