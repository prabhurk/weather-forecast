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

public class WeatherVariables {
	
	private double[] temperature;
	private double[] pressure;
	private double[] relativeHumidity;
	/**
	 * @return the temperature
	 */
	public double[] getTemperature() {
		return temperature;
	}
	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(double[] temperature) {
		this.temperature = temperature;
	}
	/**
	 * @return the pressure
	 */
	public double[] getPressure() {
		return pressure;
	}
	/**
	 * @param pressure the pressure to set
	 */
	public void setPressure(double[] pressure) {
		this.pressure = pressure;
	}
	/**
	 * @return the relativeHumidity
	 */
	public double[] getRelativeHumidity() {
		return relativeHumidity;
	}
	/**
	 * @param relativeHumidity the relativeHumidity to set
	 */
	public void setRelativeHumidity(double[] relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}
	
	
}
