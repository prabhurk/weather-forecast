/**
 * The PredictFunctions class contains weather prediction methods to output forecast results based on input data
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.main;

import com.predict.simple.weather.model.WeatherVariables;
import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

public class PredictFunctions {

	/**
	 * This method generates WeatherVariables (composed of arrays of temperature, pressure and relative humidity), based input WeatherVariables
	 * Size of each array is the given forecast size
	 * @param WeatherVariables inputWeatherVariables, each of the previous/past weather parameters expressed as an array
	 * @param int forecastSize, the expected number of forecast output
	 * @return WeatherVariables forecastWeatherVariables, forecasted parameters
	 */
	public WeatherVariables getForecastWeatherVariables(WeatherVariables inputWeatherVariables, int forecastSize) {
		WeatherVariables forecastWeatherVariables = new WeatherVariables();
		forecastWeatherVariables.setTemperature(predictWeatherParams(inputWeatherVariables.getTemperature(), forecastSize));
		forecastWeatherVariables.setPressure(predictWeatherParams(inputWeatherVariables.getPressure(), forecastSize));
		forecastWeatherVariables.setRelativeHumidity(predictWeatherParams(inputWeatherVariables.getRelativeHumidity(), forecastSize));
		return forecastWeatherVariables;
    }
	
	/**
	 * This method generates forecast values based input values
	 * The prediction model is based on ARIMA
	 * Refer: https://github.com/Workday/timeseries-forecast
	 * ARIMA model parameters are passed to obtain forecast result
	 * The structure contains forecasted values
	 * @param double[] inputData, input data
	 * @param int forecastSize, the expected number of forecast output
	 * @return double[] forecastData, forecasted output
	 */		
	public double[] predictWeatherParams(double[] inputData, int forecastSize) {
		int p = 3;
		int d = 0;
		int q = 3;
		int P = 1;
		int D = 1;
		int Q = 0;
		int m = 0;
		ArimaParams arimaParams = new ArimaParams(p, d, q, P, D, Q, m);
		ForecastResult forecastResult = Arima.forecast_arima(inputData, forecastSize, arimaParams);
		double[] forecastData = forecastResult.getForecast();
		arimaParams = null;
		return forecastData;
	}
	
	/**
	 * This method gives the weather condition based on weather conditions such as temperature, pressure and relative humidity
	 * Weather condition can be Sunny (default) or Rain or Snow
	 * @param double temperature, atmospheric temperature in °C
	 * @param double pressure, atmospheric pressure in hPa
	 * @param double relativeHumidity, relative humidity in %
	 * @return String weatherCondition, weather condition
	 */
	public String getWeatherCondition(double temperature, double pressure, double relativeHumidity) {
		String weatherCondition = "Sunny";
		if(temperature < 0)
			weatherCondition = "Snow";
		else if (relativeHumidity > 80)
			weatherCondition = "Rain";
		else
			weatherCondition = "Sunny";
		return weatherCondition;
    }

}
