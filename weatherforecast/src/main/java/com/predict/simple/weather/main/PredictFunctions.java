package com.predict.simple.weather.main;

import com.predict.simple.weather.model.WeatherVariables;
import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

/**
 * The Main Class is the main method which invoked while jar run. The Class
 * contains the entire flow of weather data generation
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class PredictFunctions {

	public WeatherVariables getForecastWeatherVariables(WeatherVariables inputWeatherVariables, int forecastSize) {
		WeatherVariables forecastWeatherVariables = new WeatherVariables();
		forecastWeatherVariables.setTemperature(predictWeatherParams(inputWeatherVariables.getTemperature(), forecastSize));
		forecastWeatherVariables.setPressure(predictWeatherParams(inputWeatherVariables.getPressure(), forecastSize));
		forecastWeatherVariables.setRelativeHumidity(predictWeatherParams(inputWeatherVariables.getRelativeHumidity(), forecastSize));
		return forecastWeatherVariables;
    }
		
	public double[] predictWeatherParams(double[] inputData, int forecastSize) {

		// Set ARIMA model parameters.
		int p = 3;
		int d = 0;
		int q = 3;
		int P = 1;
		int D = 1;
		int Q = 0;
		int m = 0;

		ArimaParams arimaParams = new ArimaParams(p, d, q, P, D, Q, m);

		// Obtain forecast result. The structure contains forecasted values
		// and performance metric etc.
		ForecastResult forecastResult = Arima.forecast_arima(inputData, forecastSize, arimaParams);

		// Read forecast values
		double[] forecastData = forecastResult.getForecast();
		return forecastData;
	}
	
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
