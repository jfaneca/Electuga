package com.hvs.electuga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient; 
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair; 
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (isNetworkAvailable()) {
			postData();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void postData() {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    //HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");
	    HttpPost httppost = new HttpPost("http://www.mobie.pt/pt/postos-de-carregamento?p_p_id=googlemaps_WAR_mobiebusinessportlet_INSTANCE_sS4M&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=searchPoles&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=2");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("searchString", ""));
	        //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        String resp = readHttpResponse(response);
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	String foo = "";
	    	foo += "";
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	String foo = "";
	    	foo += "";
	    } catch (Exception ex) {
	    	String foo = "";
	    	foo += "";	    	
	    }
	}
	
	public String readHttpResponse(HttpResponse response) {
		String result = null;
		
		try {
			InputStream ips  = response.getEntity().getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				//throw new Exception(response.getStatusLine().getReasonPhrase());
			}
			StringBuilder sb = new StringBuilder();
			String s;
			while(true )
			{
				s = buf.readLine();
				if(s==null || s.length()==0)
					break;
				sb.append(s);

			}
			buf.close();
			ips.close();
			result = sb.toString();
		}
        catch (Exception ex) {
        	//
        }
        
        return result;
	}
	
	private Boolean isNetworkAvailable() {		
		Boolean networkAvailable = false;
		
		try {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);		 
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				networkAvailable = true;
			}
		} catch (Exception ex) {
			//			
		}

		return networkAvailable;
	}
}
