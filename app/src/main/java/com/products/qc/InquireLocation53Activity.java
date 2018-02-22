package com.products.qc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class InquireLocation53Activity extends AppCompatActivity {
    TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_location53);
        tv1=(TextView)findViewById(R.id.textView1);
        loadLot();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
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
    @Override
    public void onStart()
    {
        super.onStart();
        if (AppConstant.mainMenu || AppConstant.signout)
            finish();
    }

    public void loadLot()
    {

        String test="";


        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            StringReader sr = new StringReader( AppConstant.currentLot);
            InputSource is = new InputSource(sr);
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("LineLot");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    test = test + getValue("description", (Element) node) + " \n";

                    NodeList tagList = ((Element) node).getElementsByTagName("LineTag");
                    for (int j = 0; j < tagList.getLength(); j++) {

                        Node nodetag = tagList.item(j);
                        if (nodetag.getNodeType() == Node.ELEMENT_NODE) {
                            test = test + "     Tag: " + getValue("tagid", (Element) nodetag) + "   ";
                            test = test + "     InvQty:" + getValue("invqty", (Element) nodetag) + "   ";
                            test = test + "     BalanceQty: " + getValue("balanceqty", (Element) nodetag) + "   ";
                            test = test + "     Lot:" + getValue("lot", (Element) nodetag) + "   ";
                            test = test + "     Loc:" + getValue("loc", (Element) nodetag) + "   ";

                            test = test+"\n";
                        }

                    }
                }
                test=test+"--------------------------------------------\n";
            }tv1.setText(test);




        }catch(Exception ex)
        {

        }
    }

    private String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        if (nodeList.getLength()>0) {
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }
        return "";
    }
    public void back(View view){
        this.finish();
    }


}
