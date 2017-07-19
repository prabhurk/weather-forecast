package com.predict.simple.weather.model;

/**
 * The LocationPositionBean is model class representing the Location and Position to where weather corresponds to.
 * Location is an optional label describing one or more positions.
 * Position is a comma-separated triple containing latitude, longitude, and elevation in metres above sea level
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class LatLong {
	
	private Double latitude;
	private Double longitude;
	
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
