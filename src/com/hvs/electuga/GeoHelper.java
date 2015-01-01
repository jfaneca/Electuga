package com.hvs.electuga;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class GeoHelper {
	private LocationManager locationManager; 
	
	public GeoHelper(LocationManager locationManager) {
		this.locationManager = locationManager;
	}
	
	private Location getCurrentLocation() {
		Location curLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (curLocation == null)
			curLocation = getLastKnownLocation();
		
		return curLocation; 
	}

	private Location getLastKnownLocation() {
	    List<String> providers = this.locationManager.getProviders(true);
	    Location bestLocation = null;
	    for (String provider : providers) {
	        Location l = this.locationManager.getLastKnownLocation(provider);
	        if (l == null) {
	            continue;
	        }
	        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
	            // Found best last known location: %s", l);
	            bestLocation = l;
	        }
	    }
	    
	    return bestLocation;
	}

	public static String GetBearing(Location origin, Location destination) {
		String directions[] = {"N", "NE", "E", "SE", "S", "SW","W", "NW"};
		int position;
		String result = null;
		
		float bearing = origin.bearingTo(destination);
		if (bearing < 0)
			bearing = 360 + bearing;
		position = (int)Math.round((  ((double)bearing % 360) / 45));
		
		if (position >= 0 && position <= 7)
			result = directions[ position ];
		
		return result;
	}

	public void calcCPDistance(List<ChargingPoint> chargingPoints) {
		Location loc;
		ChargingPoint cp;
		Location curLocation = getCurrentLocation();		
		
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

	public void calcCPBearing(List<ChargingPoint> chargingPoints) {
		Location loc;
		ChargingPoint cp;
		Location curLocation = getCurrentLocation();		
		
		if (chargingPoints != null) {
			for(int i=0;i<chargingPoints.size();i++) {
				cp = chargingPoints.get(i);
				loc = new Location("");
				loc.setLatitude(cp.latitude);
				loc.setLongitude(cp.longitude);
				cp.bearing = GetBearing(curLocation, loc);
			}
		}
	}
}
