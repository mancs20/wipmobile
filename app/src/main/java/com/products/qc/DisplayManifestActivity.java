package com.products.qc;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DisplayManifestActivity extends ActionBarActivity {

	InputStream result;
	
	/*public DisplayManifestActivity(InputStream result) {
		this.result = result;
	}*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_manifest);
		
		AssetManager assetManager = getAssets();
		InputStream in = null;
		//try {
			//in = assetManager.open("incoming1.xml");
	        ListView manifestListView = (ListView) findViewById(R.id.manifest_listview);
	        
	        /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);*/
            
            //XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
            //NodeList leafs = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
            
            SharedPreferences sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            
            //Calendar calendar = Calendar.getInstance();
            //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            /*Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            editor.putString(getString(R.string.saved_begin_date), format.format(date));
            
            Node node = ((NodeList) document.getElementsByTagName("ManifestId")).item(0);
            String manifestIdValue = node.getTextContent();
            editor.putString(getString(R.string.saved_manifest_id), manifestIdValue);*/
            
            ArrayList<String> data = new ArrayList<String>();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
            manifestListView.setAdapter(adapter);
            
            String warehouse_value = sharedPref.getString(getString(R.string.saved_warehouse), "");
    		data.add("Warehouse: " + warehouse_value);        
    		
    		String warehouse_customer_value = sharedPref.getString(getString(R.string.saved_warehouse_customer), "");		
    		data.add("Warehouse Customer: " + warehouse_customer_value);

            String referenceno_value = sharedPref.getString(getString(R.string.saved_referenceno), "");
            data.add("ReferenceNo: " + referenceno_value);
            
            String lot_value = sharedPref.getString(getString(R.string.saved_lot), "");
            data.add("Lot: " + lot_value);
            
            String description_value = sharedPref.getString(getString(R.string.saved_description), "");
            data.add("Description: " + description_value);
            
            String auxiliarreference_value = sharedPref.getString(getString(R.string.saved_auxiliarreference), "");
            data.add("AuxiliarReference: " + auxiliarreference_value);
            
            String shipperid_value = sharedPref.getString(getString(R.string.saved_shipperid), "");
            data.add("ShipperId: " + shipperid_value);
            
            String truckno_value = sharedPref.getString(getString(R.string.saved_truckno), "");
            data.add("TruckNo: " + truckno_value);
            
            String receiptdate_value = sharedPref.getString(getString(R.string.saved_receiptdate), "");
            data.add("DateReceived: " + receiptdate_value);
            
            /*node = ((NodeList) document.getElementsByTagName("IncomingTruck")).item(0);
            NodeList nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Id")) {
    				String incomingIdValue = n.getTextContent().trim();
    				editor.putString(getString(R.string.saved_incoming_id), incomingIdValue);
    				break;
    			}
    		}
    		
    		String productName = "";
    		
    		node = ((NodeList) document.getElementsByTagName("IncomingTruck")).item(0);
            nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Description")) {
    				productName = n.getTextContent().trim();
    				break;
    			}
    		}
            
            node = ((NodeList) document.getElementsByTagName("WarehouseIdSource")).item(0);
            nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Description")) {
    				String warehouse_value = n.getTextContent().trim();
    				editor.putString(getString(R.string.saved_warehouse), warehouse_value);
    				data.add("Warehouse: " + warehouse_value);
    				break;
    			}
    		}
            
    		node = ((NodeList) document.getElementsByTagName("WarehouseCustomerIdSource")).item(0);
            nodeList = node.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().equals("Name")){
    				String warehouse_customer_value = n.getTextContent().trim();
    				editor.putString(getString(R.string.saved_warehouse_customer), warehouse_customer_value);
    				data.add("Warehouse Customer: " + warehouse_customer_value);
    				break;
    			}
    		}
            
            node = ((NodeList) document.getElementsByTagName("ReferenceNo")).item(0);
            String referenceno_value = node.getTextContent();
            editor.putString(getString(R.string.saved_referenceno), referenceno_value);
            data.add(node.getNodeName() + ": " + referenceno_value);
            
            node = ((NodeList) document.getElementsByTagName("Lot")).item(0);
            String lot_value = node.getTextContent();
            editor.putString(getString(R.string.saved_lot), lot_value);
            data.add(node.getNodeName() + ": " + lot_value);
            
            node = ((NodeList) document.getElementsByTagName("Description")).item(0);
            String description_value = node.getTextContent();
            editor.putString(getString(R.string.saved_description), description_value);
            data.add(node.getNodeName() + ": " + description_value);
            
            node = ((NodeList) document.getElementsByTagName("AuxiliarReference")).item(0);
            String auxiliarreference_value = node.getTextContent();
            editor.putString(getString(R.string.saved_auxiliarreference), auxiliarreference_value);
            data.add(node.getNodeName() + ": " + auxiliarreference_value);
            
            node = ((NodeList) document.getElementsByTagName("ShipperId")).item(0);
            String shipperid_value = node.getTextContent();
            editor.putString(getString(R.string.saved_shipperid), shipperid_value);
            data.add(node.getNodeName() + ": " + shipperid_value);
            
            node = ((NodeList) document.getElementsByTagName("TruckNo")).item(0);
            String truckno_value = node.getTextContent();
            editor.putString(getString(R.string.saved_truckno), truckno_value);
            data.add(node.getNodeName() + ": " + truckno_value);
            
            node = ((NodeList) document.getElementsByTagName("DateReceived")).item(0);
            String receiptdate_value = node.getTextContent();
            editor.putString(getString(R.string.saved_receiptdate), receiptdate_value);
            data.add(node.getNodeName() + ": " + receiptdate_value);
            
            editor.commit();
            
            ProductReaderDbHelper mDbProductHelper = new ProductReaderDbHelper(this);
            
            // Gets the data repository in write mode
            SQLiteDatabase productdb = mDbProductHelper.getWritableDatabase();
            productdb.execSQL(ProductEntry.SQL_CREATE_ENTRIES);
            
            PictureReaderDbHelper mDbPictureHelper = new PictureReaderDbHelper(this);
            SQLiteDatabase picturedb = mDbPictureHelper.getWritableDatabase();
            picturedb.execSQL(PictureEntry.SQL_CREATE_ENTRIES);

            NodeList products = (NodeList) document.getElementsByTagName("ManifestDetail");
            for (int i = 0; i < products.getLength(); i++) {
    			NodeList nodes = products.item(i).getChildNodes();
    			String variety = "";
    			String size = "";
    			String style = "";
    			String label = "";
    			int sampleRule = 0;
    			int unitsPallet = 0;
    			int invQty = 0;
    			boolean roundUp = false;
    			
    			for (int j = 0; j < nodes.getLength(); j++) {
    				Node n = nodes.item(j);
    				if (n.getNodeName().equals("VarietyIdSource")) {
    					NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					variety = n2.getTextContent().trim();
    	    					break;
    	    				}
    					}
    				}
        			else if (n.getNodeName().equals("SizeIdSource")) {
        				NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					size = n2.getTextContent().trim();
    	    					break;
    	    				}
    					}
        			}
        			else if (n.getNodeName().equals("StyleIdSource")) {
        				NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					style = n2.getTextContent().trim();
    	    					break;
    	    				}
    					}
        			}
        			else if (n.getNodeName().equals("LabelIdSource")) {
        				NodeList nodes2 = n.getChildNodes();
    					for (int k = 0; k < nodes2.getLength(); k++) {
    	    				Node n2 = nodes2.item(k);
    	    				if (n2.getNodeName().equals("Description")) {
    	    					label = n2.getTextContent().trim();
    	    					break;
    	    				}
    					}
        			}
        			else if (n.getNodeName().equals("OnePerQty"))
        				sampleRule = Integer.parseInt(n.getTextContent().trim());
        			else if (n.getNodeName().equals("UnitsPallet"))
        				unitsPallet = Integer.parseInt(n.getTextContent().trim());
        			else if (n.getNodeName().equals("InvQty"))
        				invQty = Integer.parseInt(n.getTextContent().trim());
        			else if (n.getNodeName().equals("RoundUp") && n.getTextContent().trim().equals("true"))
        				roundUp = true;
        			else if (n.getNodeName().equals("OnePerQty"))
        				sampleRule = Integer.parseInt(n.getTextContent().trim());
    			}
    			
    			int min = unitsPallet / sampleRule;
    			if (roundUp) min++;
    			// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_NAME_VARIETY, variety);
                values.put(ProductEntry.COLUMN_NAME_SIZE, size);
                values.put(ProductEntry.COLUMN_NAME_STYLE, style);
                values.put(ProductEntry.COLUMN_NAME_LABEL, label);
                values.put(ProductEntry.COLUMN_NAME_MIN, min);
                values.put(ProductEntry.COLUMN_NAME_NAME, productName);

                // Insert the new row, returning the primary key value of the new row
                long productId = productdb.insert(ProductEntry.TABLE_NAME, null, values);
                int button = 0;
				String icFactor = "";
				int table = 0;
				String factor = "";
				
                for (int j = 0; j < nodes.getLength(); j++) {
    				Node n = nodes.item(j);
    				if (n.getNodeName().equals("ArrayOfQcReport")) {
    					NodeList icnodes = n.getChildNodes();
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
    	    	    	    							IcfactorReaderDbHelper mDbIcfactorHelper = new IcfactorReaderDbHelper(this);
    	    	    	    							
    	    	    	    				            // Gets the data repository in write mode
    	    	    	    				            SQLiteDatabase icfactordb = mDbIcfactorHelper.getWritableDatabase();
    	    	    	    							
    	    	    	                				values = new ContentValues();
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_PRODUCT, productId);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_NAME, icFactor);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_BUTTON, button);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_TABLE, table);
    	    	    	                                values.put(IcfactorEntry.COLUMN_NAME_FACTOR, factor);
    	    	    	                                
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
										
										PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(this);
		    				            
		    				            // Gets the data repository in write mode
		    				            SQLiteDatabase palletdb = mDbPalletHelper.getWritableDatabase();
		    				            palletdb.execSQL(PalletEntry.SQL_CREATE_ENTRIES);
		    				            
										values = new ContentValues();
			                            values.put(PalletEntry.COLUMN_NAME_PRODUCT, productId);
			                            values.put(PalletEntry.COLUMN_NAME_PALLETID, palletId);
			                            
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
		} /*catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
    	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));

    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(currentPallet);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_manifest, menu);
		
		return true;		
	}
	
	private void dfs(Node parent, NodeList leafs, ArrayList<String> data, int depth) {
		if (depth != 0 && parent != null) {
			if (contains(leafs, parent))
				data.add(tabs(depth) + parent.getNodeName() + ": " + parent.getTextContent());
			else data.add(tabs(depth) + parent.getNodeName());
			/*if (contains(leafs, parent))
				manifestTextView.setText(manifestTextView.getText() + tabs(depth) + parent.getNodeName() + ": " + parent.getTextContent() + "\n");
			else manifestTextView.setText(manifestTextView.getText() + tabs(depth) + parent.getNodeName() + ":\n");*/
		}
		NodeList nodeList = parent.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getChildNodes().getLength() > 0)
				dfs(node, leafs, data, depth + 1);
		}
	}
	
	private boolean contains(NodeList leafs, Node node) {
		for (int i = 0; i < leafs.getLength(); i++)
			if (leafs.item(i).equals(node)) return true;
		return false;
	}
	
	private String tabs(int depth) {
		String str = "";
		for (int i = 0; i < depth; i++)
			str += "\t";
		return str;
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
        	//AppConstant.restarting = true;
            //finish();
        	RestartDialogFragment restart_dialog = new RestartDialogFragment(this);		
    		restart_dialog.show(this.getFragmentManager(), "display");
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}

    }
	
	public void back(View view) {
		this.finish();
	}
	
	public void next(View view) {
		Intent intent = new Intent(this, TemperatureGrowerPlusActivity.class);
	    startActivity(intent);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (AppConstant.restarting || AppConstant.resampling)
			finish();
		
	}
}
