package com.hvs.electuga;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
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
	public String errMsg = null;
	public String strData;
	public String filename = "chargingPoints.json";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		String fullFilePath = getFilesDir().getPath() + filename;
		
		strData = readChargingPoints(filename);
		
		if (isNetworkAvailable()) {
			strData = getChargingPointsData();
			writeChargingPoints(strData, filename);
		} else {
			strData = readChargingPoints(filename);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public String getChargingPointsData() {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    //HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");
	    HttpPost httppost = new HttpPost("http://www.mobie.pt/pt/postos-de-carregamento?p_p_id=googlemaps_WAR_mobiebusinessportlet_INSTANCE_sS4M&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=searchPoles&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=2");
	    String resp = null;

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("searchString", ""));
	        //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        resp = readHttpResponse(response);
	    } catch (ClientProtocolException ex) {
	    	errMsg = ex.getMessage();
	    } catch (IOException ex) {
	    	errMsg = ex.getMessage();
	    } catch (Exception ex) {
	    	errMsg = ex.getMessage();
	    }
	    
	    return resp;
	}
	
	public String readHttpResponse(HttpResponse response) {
		String result = null;
		
		try {
			InputStream ips  = response.getEntity().getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				errMsg = "HttpStatus is not 200 OK";
				return null;
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
        	errMsg = ex.getMessage();
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
			errMsg = ex.getMessage();
		}

		return networkAvailable;
	}
	
	private Boolean writeChargingPoints(String chargingPoints, String filename) {
		Boolean result = false;
		
		try {
			FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(chargingPoints.getBytes());
			fos.close();
			result = true;
		} catch (FileNotFoundException fnf) {
			errMsg = "Unable to write file: file not found";
		}
		catch (IOException ex) {
			errMsg = "Unable to write file: " + ex.getMessage();
		}
		
		return result;
	}
	
	private String readChargingPoints(String filename) {
		String result = null;
		try {
			FileInputStream in = openFileInput(filename);
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			
		    result = sb.toString();
		}
		catch (IOException e) {
			errMsg = "Error reading file";
		}
		return result;
	}
}
