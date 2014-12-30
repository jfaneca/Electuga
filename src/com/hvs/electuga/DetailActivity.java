package com.hvs.electuga;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_detail, menu);
		return true;
	}
	
	@Override 
	protected void onResume() {
		ChargingPoint cp;
		cp = Globals.detailChargingPoint;
	    super.onResume();
		TextView tvUpdatetime = (TextView) findViewById(R.id.updatetime);
		if (tvUpdatetime != null)
			tvUpdatetime.setText(cp.updatetime);
		TextView tvNumberOfSattelites = (TextView) findViewById(R.id.numberOfSattelites);
		if (tvNumberOfSattelites != null)
			tvNumberOfSattelites.setText(cp.numberOfSattelites);
		TextView tvType = (TextView) findViewById(R.id.type);
		if (tvType != null)
			tvType.setText(cp.type);
		TextView tvOperator = (TextView) findViewById(R.id.operator);
		if (tvOperator != null)
			tvOperator.setText(cp.operator);
		
		Button buttonNavigate = (Button)findViewById(R.id.buttonNavigate);       
		buttonNavigate.setOnClickListener(navigateListener); // Register the onClick listene
	}
	
	private OnClickListener navigateListener = new OnClickListener() {
        public void onClick(View v) {
    		ChargingPoint cp;
    		cp = Globals.detailChargingPoint;
    		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +cp.latitude+","+cp.longitude));
    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(intent);
        }
    };
}
