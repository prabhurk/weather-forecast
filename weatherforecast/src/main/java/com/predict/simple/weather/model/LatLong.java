package com.predict.simple.weather.model;

/**
 * The LatLong is model class representing the latitude and longitude corresponds to a location
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class LatLong {
	
	private Double latitude;
	private Double longitude;
	
	@Override
	public String toString() {
		return this.latitude + "," + this.longitude;
	}
	
	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
