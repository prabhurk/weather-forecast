/**
 * The WeatherVariables is model class representing the array of weather parameters
 * The model is used to hold past weather data parameters and forecast results
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.model;

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
