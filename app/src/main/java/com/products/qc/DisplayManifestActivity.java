package com.products.qc;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DisplayManifestActivity extends AppCompatActivity {
    public Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_manifest);

        ListView manifestListView = (ListView) findViewById(R.id.manifest_listview);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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

        String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));

        ActionBar ab = getSupportActionBar();
        ab.setTitle(currentPallet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
        if (currentPallet.equals("0") || Utils.sampledCount(this) == 0)
            menu.removeItem(R.id.action_send_data);
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
            case R.id.action_signout:
                AppConstant.signout = true;
                this.finish();
                return true;
            case R.id.action_main_menu:
                AppConstant.mainMenu = true;
                this.finish();
                return true;
            case R.id.action_send_data:
                if (Utils.requiredSample(this)) {
                    final Activity activity = this;
                    new AlertDialog.Builder(this)
                            .setTitle("Send Data")
                            .setMessage("Quality Control uncompleted")
                            .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    QControlCaller c = new QControlCaller(activity);
                                    c.start();
                                }
                            })
                            .create()
                            .show();
                } else {
                    QControlCaller c = new QControlCaller(this);
                    c.start();
                }
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
    protected void onRestart() {
        super.onRestart();
        onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppConstant.restarting || AppConstant.resampling || AppConstant.mainMenu || AppConstant.signout)
            finish();

    }
}
