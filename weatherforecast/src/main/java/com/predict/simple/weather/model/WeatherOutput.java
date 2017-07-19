package com.predict.simple.weather.model;

import org.joda.time.DateTime;

/**
 * The LocationPositionBean is model class representing the Location and Position to where weather corresponds to.
 * Location is an optional label describing one or more positions.
 * Position is a comma-separated triple containing latitude, longitude, and elevation in metres above sea level
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class WeatherOutput {
	
	private Location location;
	private WeatherVariables weatherVariables;
	private DateTime dateTime;
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	/**
	 * @return the weatherVariables
	 */
	public WeatherVariables getWeatherVariables() {
		return weatherVariables;
	}
	/**
	 * @param weatherVariables the weatherVariables to set
	 */
	public void setWeatherVariables(WeatherVariables weatherVariables) {
		this.weatherVariables = weatherVariables;
	}
	/**
	 * @return the dateTime
	 */
	public DateTime getDateTime() {
		return dateTime;
	}
	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	
}
