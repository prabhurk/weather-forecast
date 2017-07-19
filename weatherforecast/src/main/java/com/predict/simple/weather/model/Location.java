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

public class Location {
	
	private String name;
	private String iataCode;
	private LatLong latLong;
	private Double elevation;
	private String timeZoneId;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the iataCode
	 */
	public String getIataCode() {
		return iataCode;
	}
	/**
	 * @param iataCode the iataCode to set
	 */
	public void setIataCode(String iataCode) {
		this.iataCode = iataCode;
	}
	/**
	 * @return the latLong
	 */
	public LatLong getLatLong() {
		return latLong;
	}
	/**
	 * @param latLong the latLong to set
	 */
	public void setLatLong(LatLong latLong) {
		this.latLong = latLong;
	}
	/**
	 * @return the elevation
	 */
	public Double getElevation() {
		return elevation;
	}
	/**
	 * @param elevation the elevation to set
	 */
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
	/**
	 * @return the timeZoneId
	 */
	public String getTimeZoneId() {
		return timeZoneId;
	}
	/**
	 * @param timeZoneId the timeZoneId to set
	 */
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}
}
