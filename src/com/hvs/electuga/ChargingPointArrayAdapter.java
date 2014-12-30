package com.hvs.electuga;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.ListActivity;

public class ChargingPointArrayAdapter extends ArrayAdapter<ChargingPoint> {
	private final Context context;
	private final List<ChargingPoint> values;
	
	public ChargingPointArrayAdapter(Context context, List<ChargingPoint> values) {
		super(context, R.layout.activity_main, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String cpDescr = "";
		ChargingPoint cp;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.activity_main, parent, false);
	    TextView label = (TextView) rowView.findViewById(R.id.cpLabel);
	    TextView distance = (TextView) rowView.findViewById(R.id.cpDistance);

	    cp = values.get(position);
	    
	    cpDescr = cp.street;
	    if (cpDescr == null)
	    	cpDescr = "";
	    
	    if (cp.number != null && cp.number.length() > 0) {
	    	cp.number = cp.number.replace("-","").trim();
	    	if (cp.number != null && cp.number.length() > 0)
	    		cpDescr += ", " + cp.number;
	    }
	    
	    if (cp.postalCode != null && cp.postalCode.length() > 0)
	    	cpDescr += " " + cp.postalCode;
	    
	    if (cp.city != null && cp.city.length() > 0)
	    	cpDescr += " " + cp.city;

	    label.setTag(cp);
	    label.setText(cpDescr);
	    distance.setText(cp.distanceLabel);
	    distance.setTag(cp);
	    
	    if (isOdd(position))
	    	rowView.setBackgroundColor(Color.parseColor("#96CFDF")); // Dark blue 
	    else
	    	rowView.setBackgroundColor(Color.parseColor("#C7E4EC")); // Light blue 
	    
	    if (!cp.available)
	    	label.setPaintFlags(label.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

	    return rowView;
	  }
	
	private boolean isOdd( int val ) { 
		return (val & 0x01) != 0; 
	}
}
