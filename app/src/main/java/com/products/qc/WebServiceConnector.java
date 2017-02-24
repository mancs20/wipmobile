package com.products.qc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class WebServiceConnector {
	String url;
	HttpClient httpclient;
	HttpPost httpPost;
	String error;
	
	public WebServiceConnector(String url) {
		error = "";
		this.url = url;
		this.httpclient = new DefaultHttpClient();
		this.httpPost = new HttpPost(url);
	}
	
	public InputStream sendData(String xmlData) throws ClientProtocolException, IOException {
		String result;
		InputStream inputStream = null;
		StringEntity se = new StringEntity(xmlData);

		// 6. set httpPost Entity
		httpPost.setEntity(se);
		

		// 7. Set some headers to inform server about the type of the content   
		//httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setHeader("Content-Length", String.valueOf(xmlData.length()));
		//String s = MainActivity.convertInputStreamToString(httpPost.getEntity().getContent());
		//httpPost.setHeader("username", username);
		//httpPost.setHeader("telefono", json.toString());

		// 8. Execute POST request to the given URL
		HttpResponse httpResponse = null;
		try{
			httpResponse = httpclient.execute(httpPost);
		} catch(Exception e) {
			Log.e("", e.getMessage());
		}

		//8.1 
		StatusLine statusLine = httpResponse.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if(inputStream == null) {
				error = "Server Connection Time Out";
			}
				//return result = "El servidor no responde.";
		} else {
			error = "Server Internal Error";
			//return result = "Error interno del servidor.";
		}
		return inputStream;
	}
	
	public String getError() {
		return error;
	}
}
