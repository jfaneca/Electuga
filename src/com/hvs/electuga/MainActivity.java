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

import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient; 
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair; 
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MainActivity extends ListActivity  {
	public String errMsg = null;
	public String savedData,pulledData;
	public String filename = "chargingPoints.json";
	public List<ChargingPoint> savedChargingPoints, pulledChargingPoints, currentChargingPoints;
	public int savedChargingPointersCounter = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		savedData = readChargingPoints(filename);
		savedChargingPoints = parseChargingPointData(savedData);
		if (savedChargingPoints != null)
			savedChargingPointersCounter = savedChargingPoints.size();
		
		if (isNetworkAvailable()) {
			pulledData = pullChargingPointsData();
			pulledChargingPoints = parseChargingPointData(pulledData);
			if (pulledChargingPoints != null && pulledChargingPoints.size() >= savedChargingPointersCounter) {
				writeChargingPoints(pulledData, filename);
				currentChargingPoints = pulledChargingPoints;
			}
			else
				currentChargingPoints = savedChargingPoints;
		}
		
		if (currentChargingPoints != null) {
			calcCPDistance(currentChargingPoints);
		}

	    ChargingPointArrayAdapter adapter = new ChargingPointArrayAdapter(this, currentChargingPoints);
	    setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void navigate2ChargingPoint(View view){
		ChargingPoint cp;
		cp = (ChargingPoint) view.getTag();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +cp.latitude+","+cp.longitude));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void calcCPDistance(List<ChargingPoint> chargingPoints) {
		LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location curLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location loc;
		ChargingPoint cp;
		
		if (chargingPoints != null) {
			for(int i=0;i<chargingPoints.size();i++) {
				cp = chargingPoints.get(i);
				loc = new Location("");
				loc.setLatitude(cp.latitude);
				loc.setLongitude(cp.longitude);
				cp.distance = curLocation.distanceTo(loc);
				if (cp.distance >= 1000) {
					cp.distanceLabel = String.format("%.2f", cp.distance / 1000) + " kms";
				}
				else {
					cp.distanceLabel = String.format("%d",(long)cp.distance) + " mts";
				}
			}
		}
	}
	
	public String pullChargingPointsData() {
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
	
	private List<ChargingPoint> parseChargingPointData(String data) {
		List<ChargingPoint> result = null;
		ChargingPoint cp;
		JSONObject jObj;
		String status;
		Object objAux;
		JSONArray jChargingPointsArray = null;
		
		try {
			JSONObject jTopStructure = (JSONObject) new JSONTokener(data).nextValue();
			JSONObject jResponse = (JSONObject) jTopStructure.get("response");
			Boolean operationSuccess = false;
			
			operationSuccess = jResponse.getBoolean("operationSuccess");

			if (operationSuccess)
				jChargingPointsArray = jResponse.getJSONArray("data");
			if (operationSuccess && jChargingPointsArray != null) {
				result = new ArrayList<ChargingPoint>();
				for(int i = 0; i < jChargingPointsArray.length(); i++) {
					cp = new ChargingPoint();
					jObj = (JSONObject) jChargingPointsArray.get(i);
					status = (String) jObj.get("status");
					if (status == "Available")
						cp.available = true;
					else 
						cp.available = false;
					cp.street = (String) jObj.get("street");
					cp.number = (String) jObj.get("number");
					cp.city = (String) jObj.get("city");
					objAux = (Object) jObj.get("longitude");
					if (tryParseDouble(objAux))
						cp.longitude = Double.parseDouble((String) objAux);
					objAux = (Object) jObj.get("latitude");
					if (tryParseDouble(objAux))
						cp.latitude = Double.parseDouble((String) objAux);
					
					result.add(cp);
				}
			}
	    } catch (JSONException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		return result;
	}

	private boolean tryParseDouble(Object obj) {
		boolean result = false;
		try {
			Double d = Double.parseDouble((String) obj);
		    result = true;
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		return result;
	}
}
