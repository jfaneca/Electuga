package com.hvs.electuga;

public class ChargingPoint implements Comparable {
	Boolean available;
	String street, number, city;
	double longitude, latitude;
	float distance;
	String distanceLabel;
	String postalCode;
	String updatetime;
	String numberOfSattelites;
	String type;
	String operator;
	
	public Boolean getAvailable() {
		return available; 
	}
	  
	public void setAvailable(Boolean available) {
		this.available = available; 
	}

	public String getStreet() {
		return street; 
	}
	  
	public void setStreet(String street) {
		this.street = street; 
	}

	public String getNumber() {
		return number; 
	}
	  
	public void setNumber(String number) {
		this.number = number; 
	}

	public String getCity() {
		return city; 
	}
	  
	public void setCity(String city) {
		this.city = city; 
	}

	public Double getLongitude() {
		return longitude; 
	}
	  
	public void setLongitude(Double longitude) {
		this.longitude = longitude; 
	}

	public Double getLatitude() {
		return latitude; 
	}
	  
	public void setLatitude(Double latitude) {
		this.latitude = latitude; 
	}

	public float getDistance() {
		return distance; 
	}
	  
	public void setDistance(float distance) {
		this.distance = distance;
	}

	public String getDistanceLabel() {
		return distanceLabel; 
	}
	  
	public void setDistanceLabel(String distanceLabel) {
		this.distanceLabel = distanceLabel;
	}

	public String getPostalCode() {
		return postalCode; 
	}
	  
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getUpdatetime() {
		return updatetime; 
	}
	  
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getNumberOfSattelites() {
		return numberOfSattelites; 
	}
	  
	public void setNumberOfSattelites(String numberOfSattelites) {
		this.numberOfSattelites = numberOfSattelites;
	}

	public String getType() {
		return type; 
	}
	  
	public void setType(String type) {
		this.type = type;
	}

	public String getOperator() {
		return operator; 
	}
	  
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	@Override
    public int compareTo(Object o) {
		ChargingPoint cp = (ChargingPoint)o;

        if (this.distance > cp.distance) {
            return 1;
        }
        else if (this.distance <  cp.distance) {
            return -1;
        }
        else {
            return 0;
        }
    }
}