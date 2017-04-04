package com.products.qc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.client.ClientProtocolException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.products.qc.IcfactorDataReaderContract.IcfactorDataEntry;
import com.products.qc.IcfactorReaderContract.IcfactorEntry;
import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.PictureReaderContract.PictureEntry;
import com.products.qc.ProductReaderContract.ProductEntry;
import com.products.qc.SamplingReaderContract.SamplingEntry;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	EditText codeEditText;
	Menu menu;
	public static String rslt="";
	SharedPreferences sharedPref;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  
        codeEditText = (EditText) findViewById(R.id.edit_code);

		codeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_NEXT) {
					sendCode(null);
				}
				return false;
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        this.menu = menu;
        Restart();
        
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
    
    public void sendCode(View view) {
		
		String code = codeEditText.getText().toString();
		if (code.equals("")) {
			Toast.makeText(MainActivity.this, "Enter Code", Toast.LENGTH_LONG).show();
		}
		else {
			sharedPref = this.getSharedPreferences(
	                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
	    	
	    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
	    	if(currentPallet.equals("0")){
	    		String beginDate = sharedPref.getString(getString(R.string.saved_begin_date), "");
	    		if (beginDate == "") {
		    		//palletCorrect(code);
		    		rslt="START";
					Caller c = new Caller(this, code);
					c.start();
	    		}
	    		else {
	    			palletInDatabase(code);
	    		}
	    	} else {
	    		palletInDatabase(code);
	    	}
		}
	}

	public void back(View view){
		this.finish();
	}
    
    public void palletCorrect(String code, final Activity thisActivity) {
		int valid = saveManifestInDataBase(MainActivity.rslt, this);
    	if (valid == 1) {
    		PalletNotFoundDialogFragment pnf = new PalletNotFoundDialogFragment(this);
    		pnf.show(this.getFragmentManager(), "connproblem");

    	} else if (valid == 2) {
			ActionBarMethods.restart(thisActivity);
			thisActivity.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(thisActivity, "There is no QC template for this product pallet.", Toast.LENGTH_LONG).show();
				}
			});

		}
		else
			palletInDatabase(code);
    }
    
    public void palletInDatabase(String code) {
    	if(!QueryRepository.containPalletIdInDataBase(this, Integer.parseInt(code.trim()))) {
			Toast.makeText(MainActivity.this, "Pallet not found.", Toast.LENGTH_LONG).show();
		}
		else {
			int samplingId = QueryRepository.getSamplingIdByCode(this, Integer.parseInt(code));
			
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.saved_current_sampling), samplingId);
    	    editor.putInt(getString(R.string.saved_current_pallet), Integer.parseInt(code));
    	    editor.apply();
			
			Intent intent = new Intent(this, DisplayManifestActivity.class);
		    startActivity(intent);
		}
    }
	
	public void restartApp(View view) {
		//ActionBarMethods.restart(this);
		RestartDialogFragment restart_dialog = new RestartDialogFragment(this);		
		restart_dialog.show(this.getFragmentManager(), "display");
		//Restart();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (AppConstant.restarting){
			if (AppConstant.dataSended) {
				Toast.makeText(MainActivity.this, "Quality control successfully saved", Toast.LENGTH_LONG).show();
				AppConstant.dataSended = false;
			}
			ActionBarMethods.restart(this);
			AppConstant.restarting = false;
		}
		else if (AppConstant.resampling) {
			//SharedPreferences.Editor editor = sharedPref.edit();
			//editor.remove(getString(R.string.saved_current_pallet));
			//editor.commit();
			AppConstant.resampling = false;
			codeEditText.setText("");
		}
		if (AppConstant.mainMenu || AppConstant.signout)
			finish();
		Restart();		
	}
	
	public void Restart(){
		SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        
		if(menu != null){
	        if(sharedPref.contains(getString(R.string.saved_current_pallet))){
	        	menu.getItem(1).setEnabled(true);
	        	menu.getItem(2).setEnabled(true);
	        }
	        else{
	        	menu.getItem(1).setEnabled(false);
	        	menu.getItem(2).setEnabled(false);
	        }
		}
	}
	

	
	public InputStream getInitialXML() throws TransformerException, ParserConfigurationException, ClientProtocolException, IOException {
		
		String result = "";
		//String url = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx/getIncomingbyTag";
		String url = "http://10.26.24.171/ssl/post.php";	
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		
		String code = codeEditText.getText().toString();
		
		/*Element tag = doc.createElement("TagId");
		tag.appendChild(doc.createTextNode(code));
		doc.appendChild(tag);
		
		// write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        DOMSource source = new DOMSource(doc.getDocumentElement());

        transformer.transform(source, sr);
        String xmlString = sw.toString();*/
        
        WebServiceConnector wsc = new WebServiceConnector(url);
        
        //RetrieveTask rt = new RetrieveTask(wsc, code, this);
        
        //rt.execute(url);
        /*if (inputStream == null) {
        	Toast.makeText(MainActivity.this, wsc.getError(), Toast.LENGTH_LONG).show();
        } else {
        	//Intent intent = new Intent(this, DisplayManifestActivity.class);
		    //startActivity(intent);
        }*/
        return new InputStream() {
			
			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}
	
	public static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}
	
	public int saveManifestInDataBase(String in, Activity activity){
		AssetManager assetManager = getAssets();
		try {
			//InputStream inn = assetManager.open("incoming3.xml");
	        //ListView manifestListView = (ListView) findViewById(R.id.manifest_listview);
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(in)));
	        //Document document = builder.parse(inn);
            
            //XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
            //NodeList leafs = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
            
            SharedPreferences sharedPref = activity.getSharedPreferences(
            		activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            
            //Calendar calendar = Calendar.getInstance();
            //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            editor.putString(activity.getString(R.string.saved_begin_date), format.format(date));
            
            Node node = ((NodeList) document.getElementsByTagName("ManifestId")).item(0);
            String manifestIdValue = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_manifest_id), manifestIdValue);
            
            /*ArrayList<String> data = new ArrayList<String>();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
            manifestListView.setAdapter(adapter);*/
            
            node = ((NodeList) document.getElementsByTagName("IncomingTruck")).item(0);
            NodeList nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Id")) {
    				String incomingIdValue = n.getTextContent().trim();
    				if (incomingIdValue.equals("") || incomingIdValue.equals("0"))
    					return 1;
    				editor.putString(activity.getString(R.string.saved_incoming_id), incomingIdValue);
    				break;
    			}
    		}
    		
    		/*node = ((NodeList) document.getElementsByTagName("IncomingTruck")).item(0);
            nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Description")) {
    				productName = n.getTextContent().trim();
    			}
    			else if (n.getNodeName().equals("ShortName")) {
    				productNameSN = n.getTextContent().trim();
    			}
    		}*/
            
            node = ((NodeList) document.getElementsByTagName("WarehouseIdSource")).item(0);
            nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Description")) {
    				String warehouse_value = n.getTextContent().trim();
    				editor.putString(activity.getString(R.string.saved_warehouse), warehouse_value);
    				//data.add("Warehouse: " + warehouse_value);
    				break;
    			}
    		}
            
    		node = ((NodeList) document.getElementsByTagName("WarehouseCustomerIdSource")).item(0);
            nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Name")){
    				String warehouse_customer_value = n.getTextContent().trim();
    				editor.putString(activity.getString(R.string.saved_warehouse_customer), warehouse_customer_value);
    				//data.add("Warehouse Customer: " + warehouse_customer_value);
    				break;
    			}
    		}
            
            node = ((NodeList) document.getElementsByTagName("ReferenceNo")).item(0);
            String referenceno_value = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_referenceno), referenceno_value);
            //data.add(node.getNodeName() + ": " + referenceno_value);
            
            node = ((NodeList) document.getElementsByTagName("Lot")).item(0);
            String lot_value = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_lot), lot_value);
            //data.add(node.getNodeName() + ": " + lot_value);
            
            node = ((NodeList) document.getElementsByTagName("Description")).item(0);
            String description_value = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_description), description_value);
            //data.add(node.getNodeName() + ": " + description_value);
            
            node = ((NodeList) document.getElementsByTagName("AuxiliarReference")).item(0);
            String auxiliarreference_value = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_auxiliarreference), auxiliarreference_value);
            //data.add(node.getNodeName() + ": " + auxiliarreference_value);
            
            node = ((NodeList) document.getElementsByTagName("ShipperId")).item(0);
            String shipperid_value = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_shipperid), shipperid_value);
            //data.add(node.getNodeName() + ": " + shipperid_value);
            
            node = ((NodeList) document.getElementsByTagName("TruckNo")).item(0);
            String truckno_value = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_truckno), truckno_value);
            //data.add(node.getNodeName() + ": " + truckno_value);
            
            node = ((NodeList) document.getElementsByTagName("DateReceived")).item(0);
            String receiptdate_value = node.getTextContent();
            editor.putString(activity.getString(R.string.saved_receiptdate), receiptdate_value);
            //data.add(node.getNodeName() + ": " + receiptdate_value);
            
            editor.apply();
            
            ProductReaderDbHelper mDbProductHelper = new ProductReaderDbHelper(activity);
            SQLiteDatabase productdb = mDbProductHelper.getWritableDatabase();
            productdb.execSQL(ProductEntry.SQL_CREATE_ENTRIES);
            
            PictureReaderDbHelper mDbPictureHelper = new PictureReaderDbHelper(activity);
            SQLiteDatabase picturedb = mDbPictureHelper.getWritableDatabase();
            picturedb.execSQL(PictureEntry.SQL_CREATE_ENTRIES);
            
            SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
            SQLiteDatabase samplingdb = mDbSamplingHelper.getWritableDatabase();
            samplingdb.execSQL(SamplingEntry.SQL_CREATE_ENTRIES);
            
            IcfactorDataReaderDbHelper mDbIcfactorDataHelper = new IcfactorDataReaderDbHelper(activity);
            SQLiteDatabase icfactorDatadb = mDbIcfactorDataHelper.getWritableDatabase();
            icfactorDatadb.execSQL(IcfactorDataEntry.SQL_CREATE_ENTRIES);

            NodeList products = (NodeList) document.getElementsByTagName("ManifestDetail");
            for (int i = 0; i < products.getLength(); i++) {
    			NodeList nodes = products.item(i).getChildNodes();
        		String productName = "";
        		String productNameSN = "";
    			String variety = "";
    			String varietySN = "";
    			String size = "";
    			String sizeSN = "";
    			String style = "";
    			String styleSN = "";
    			String label = "";
    			String labelSN = "";
    			int sampleRule = 0;
    			int invQty = 0;
    			boolean roundUp = false;
    			
    			for (int j = 0; j < nodes.getLength(); j++) {
    				Node n = nodes.item(j);
    				if (n.getNodeName().equals("ProductIdSource")) {
    					NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					productName = n2.getTextContent().trim();
    	    				}
    	    				else if (n2.getNodeName().equals("ShortName")) {
    	    					productNameSN = n2.getTextContent().trim();
    	    				}
    					}
    				}
    				else if (n.getNodeName().equals("VarietyIdSource")) {
    					NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					variety = n2.getTextContent().trim();
    	    				}
    	    				else if (n2.getNodeName().equals("ShortName")) {
    	    					varietySN = n2.getTextContent().trim();
    	    				}
    					}
    				}
        			else if (n.getNodeName().equals("SizeIdSource")) {
        				NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					size = n2.getTextContent().trim();
    	    				}
    	    				else if (n2.getNodeName().equals("ShortName")) {
    	    					sizeSN = n2.getTextContent().trim();
    	    				}
    					}
        			}
        			else if (n.getNodeName().equals("StyleIdSource")) {
        				NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					style = n2.getTextContent().trim();
    	    				}
    	    				if (n2.getNodeName().equals("ShortName")) {
    	    					styleSN = n2.getTextContent().trim();
    	    				}
    					}
        			}
        			else if (n.getNodeName().equals("LabelIdSource")) {
        				NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					label = n2.getTextContent().trim();
    	    				}
    	    				if (n2.getNodeName().equals("ShortName")) {
    	    					labelSN = n2.getTextContent().trim();
    	    				}
    					}
        			}
        			else if (n.getNodeName().equals("ArrayOfQcRules")) {
        				NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("QcRules")) {
    	    					NodeList nodes3 = n2.getChildNodes();
    	    					for (int l = 0; l < nodes3.getLength(); l++) {
    	    	    				Node n3 = nodes3.item(l);
    	    	    				if (n3.getNodeName().equals("OnePerQty")) {
										String sampleRuleXml = n3.getTextContent().trim();
    	    	    					sampleRule = !sampleRuleXml.equalsIgnoreCase("") ? Integer.parseInt(sampleRuleXml) : 0;
    	    	    				}
    	    	    				else if (n3.getNodeName().equals("RoundUp") && n3.getTextContent().trim().equals("true")) {
    	    	    					roundUp = true;
    	    	    				}
    	    					}
    	    					break;
    	    				}
    					}
        			}
        			else if (n.getNodeName().equals("InvQty"))
        				invQty = Integer.parseInt(n.getTextContent().trim());
    			}
    			
    			int min = 0;
				if (sampleRule != 0) {
					min = invQty / sampleRule;
					if (roundUp) min++;
				}
    			
    			// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_NAME_VARIETY, variety);
                values.put(ProductEntry.COLUMN_NAME_VARIETYSN, varietySN);
                values.put(ProductEntry.COLUMN_NAME_SIZE, size);
                values.put(ProductEntry.COLUMN_NAME_SIZESN, sizeSN);
                values.put(ProductEntry.COLUMN_NAME_STYLE, style);
                values.put(ProductEntry.COLUMN_NAME_STYLESN, styleSN);
                values.put(ProductEntry.COLUMN_NAME_LABEL, label);
                values.put(ProductEntry.COLUMN_NAME_LABELSN, labelSN);
                values.put(ProductEntry.COLUMN_NAME_MIN, min);
                values.put(ProductEntry.COLUMN_NAME_NAME, productName);
                values.put(ProductEntry.COLUMN_NAME_NAMESN, productNameSN);

                // Insert the new row, returning the primary key value of the new row
                long productId = productdb.insert(ProductEntry.TABLE_NAME, null, values);
                int button = 0;
				String icFactor = "";
				int table = 0;
				String factor = "";
				int qcfactorid = 0;
				int order = 0;
				
                for (int j = 0; j < nodes.getLength(); j++) {
    				Node n = nodes.item(j);
    				if (n.getNodeName().equals("ArrayOfQcReport")) {
    					NodeList icnodes = n.getChildNodes();
						if (icnodes.getLength() == 0)
							return 2;
    					for (int k = 0; k < icnodes.getLength(); k++) {
    						Node nodek = icnodes.item(k);
    						if (nodek.getNodeName().equals("QcReport")) {
    							NodeList icnodesl = nodek.getChildNodes();
    							for (int l = 0; l < icnodesl.getLength(); l++) {
    	    						Node nodel = icnodesl.item(l);
    	    						if (nodel.getNodeName().equals("QcReportDetailCollection")){
    	    							NodeList gg = nodel.getChildNodes();
    	    							for (int m = 0; m < gg.getLength(); m++) {
    	    	    						Node nodel1 = gg.item(m);
    	    	    						if (nodel1.getNodeName().equals("QcReportDetail")){
    	    	    							NodeList gg2 = nodel1.getChildNodes();
    	    	    							for (int o = 0; o < gg2.getLength(); o++) {
    	    	    	    						Node nodel2 = gg2.item(o);
    	    	    	    						if (nodel2.getNodeName().equals("QcButtonTypeId")){
    	    	    	    							button = Integer.parseInt(nodel2.getTextContent().trim());
    	    	    	    						}
    	    	    	    						else if (nodel2.getNodeName().equals("QcFactorId")){
    	    	    	    							qcfactorid = Integer.parseInt(nodel2.getTextContent().trim());
    	    	    	    						}
    	    	    	    						else if (nodel2.getNodeName().equals("Order")){
    	    	    	    							order = Integer.parseInt(nodel2.getTextContent().trim());
    	    	    	    						}
    	    	    	    						else if (nodel2.getNodeName().equals("QcFactorIdSource")) {
    	    	    	    							NodeList gg3 = nodel2.getChildNodes();
    	    	    	    							for (int p = 0; p < gg3.getLength(); p++) {
    	    	    	    	    						Node nodel3 = gg3.item(p);
    	    	    	    	    						if (nodel3.getNodeName().equals("Description")){
    	    	    	    	    							icFactor = nodel3.getTextContent().trim();
    	    	    	    	    						} else if (nodel3.getNodeName().equals("QcFactorTypeIdSource")) {
    	    	    	    	    							NodeList gg4 = nodel3.getChildNodes();
    	    	    	    	    							for (int q = 0; q < gg4.getLength(); q++) {
    	    	    	    	    	    						Node nodel4 = gg4.item(q);
    	    	    	    	    	    						if (nodel4.getNodeName().equals("Description")){
    	    	    	    	    	    							factor = nodel4.getTextContent().trim();
    	    	    	    	    	    							break;
    	    	    	    	    	    						}
    	    	    	    	    							}
    	    	    	    	    						}
    	    	    	    							}
    	    	    	    							IcfactorReaderDbHelper mDbIcfactorHelper = new IcfactorReaderDbHelper(activity);
    	    	    	    							
    	    	    	    				            // Gets the data repository in write mode
    	    	    	    				            SQLiteDatabase icfactordb = mDbIcfactorHelper.getWritableDatabase();
    	    	    	    							
    	    	    	                				values = new ContentValues();
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_PRODUCT, productId);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_NAME, icFactor);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_BUTTON, button);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_TABLE, table);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_FACTOR, factor);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_QCFACTORID, qcfactorid);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_ORDER, order);
    	    	    	                                
    	    	    	                                icfactordb.execSQL(IcfactorEntry.SQL_CREATE_ENTRIES);
    	    	    	                                icfactordb.insert(IcfactorEntry.TABLE_NAME, null, values);
    	    	    	                                icfactordb.close();
    	    	    	                                mDbIcfactorHelper.close();
    	    	    	    						}
    	    	    							}
    	    	    						}
    	    							}
    	    							break;
    	    						}
    							}
    						}
    					}
    					table++;
    				}
    				else if (n.getNodeName().equals("TagCollection")) {
						NodeList icnodes2 = n.getChildNodes();
						for (int k = 0; k < icnodes2.getLength(); k++) {
							if (icnodes2.item(k).getNodeName().equals("Tag")) {
								NodeList nl = icnodes2.item(k).getChildNodes();
								for (int l = 0; l < nl.getLength(); l++) {
									Node nodel = nl.item(l);
									if (nodel.getNodeName().equals("Id")) {
										int palletId = Integer.parseInt(nodel.getTextContent().trim());
										
										PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		    				            
		    				            // Gets the data repository in write mode
		    				            SQLiteDatabase palletdb = mDbPalletHelper.getWritableDatabase();
		    				            palletdb.execSQL(PalletEntry.SQL_CREATE_ENTRIES);
		    				            
										values = new ContentValues();
			                            values.put(PalletEntry.COLUMN_NAME_PRODUCT, productId);
			                            values.put(PalletEntry.COLUMN_NAME_CODE, palletId);
			                            
			                            palletdb.insert(PalletEntry.TABLE_NAME, null, values);
			                            palletdb.close();
			                            mDbPalletHelper.close();
			                            break;
									}
								}
							}
						}
		            }
                }
            }
            productdb.close();
            mDbProductHelper.close();
         	productdb.close();
            //dfs(document, leafs, data, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return 0;
	}
}

/*class RetrieveTask extends AsyncTask<String, Void, InputStream> {

    private Exception exception;
    WebServiceConnector wsc;
    String code;
    Activity activity;
    
    public RetrieveTask(WebServiceConnector wsc, String code, Activity activity) {
    	this.wsc = wsc;
    	this.code = code;
    	this.activity = activity;
    }
    protected InputStream doInBackground(String... urls) {
    	InputStream inputStream = null;
    	try {
            URL url= new URL(urls[0]);
            inputStream = wsc.sendData("Tag=" + code);
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
        return inputStream;
    }

    protected void onPostExecute(InputStream is) {
    	MainActivity.saveManifestInDataBase(is, activity);
		Intent intent = new Intent(activity, DisplayManifestActivity.class);
		activity.startActivity(intent);
    }
}*/

class Caller extends Thread
{
	Activity activity;
	String code;
	public Caller(Activity activity, String code) {
		this.activity = activity;
		this.code = code;
	}
    public void run() {
    	ProgressBarDialogFragment cp = new ProgressBarDialogFragment();
        try {
        	cp.setCancelable(false);
        	cp.show(activity.getFragmentManager(), "sendingdata");
        	
            String SOAP_ACTION = "http://tempuri.org/getIncomingbyTag";
            String OPERATION_NAME = "getIncomingbyTag";
            String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
            String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx";

            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
            PropertyInfo pi=new PropertyInfo();
            pi.setName("Tag");
            pi.setValue(code);
            pi.setType(Integer.class);
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
                ConnectionProblemDialogFragment cp2 = new ConnectionProblemDialogFragment(activity);
            	cp2.show(activity.getFragmentManager(), "connproblem");
            	this.stop();
            }
            MainActivity.rslt=response.toString();
            ((MainActivity)activity).palletCorrect(code, activity);
            cp.dismiss();
        } catch(Exception ex) {
        	//MainActivity.rslt=ex.toString();
        	cp.dismiss();

			activity.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(activity, "Try again. Something was wrong", Toast.LENGTH_LONG).show();
				}
			});
    	}
    }
}
