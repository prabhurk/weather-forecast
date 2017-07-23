/**
 * The Main class is the main method which invoked while jar run
 * The class contains the entire flow of weather data forecast
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.predict.simple.weather.model.LatLong;
import com.predict.simple.weather.model.Location;
import com.predict.simple.weather.model.WeatherOutput;
import com.predict.simple.weather.model.WeatherVariables;
import com.predict.simple.weather.util.Constants;
import com.predict.simple.weather.util.Utils;

public class Main {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	/**
	 * The main method is invoked while while jar/executable run
	 * executionStartDate is the date at which execution happens, i.e., now when the executable runs, and is used to mark up the output file name
	 * rootDir "" means the Constants.INPUT_LOCJSON and inputFolder & outputFolder are in the same location as the jar
	 * inputFolder is for holding input data such as lat/long, elevation & timezone jsons and BoM weather data
	 * outputFolder is for holding output forecast result file for all locations together
	 * outputFolderTemp is for holding output forecast result file for each location and is cleaned before every execution
	 * inputJson contains the list of locations and their codes for which weather has to be forecasted
	 * foreCastSize is the number of future forecast predictions for each location
	 * The method works by getting each location object from list of location objects in inputJson and iterating through each of them
	 * During the iteration, following steps are performed:
	 * Get locationName and iataCode specified in the inputJson corresponding to the location object
	 * dataFolder is for holding past weather data, preferably from BoM and the data is kept under each folder, folder name being the code given in inputJson
	 * Get latitude and longitude from location name, preferably through Google API
	 * Get elevation of the location from its latitude and longitude, preferably through Google API
	 * Get timezone id of the location from its latitude and longitude, preferably through Google API
	 * exeDateInLocTimeZone is the executionStartDate, in the local time of the location
	 * forecastFromDate is the date from which forecasting begin, preferably the next day of exeDateInLocTimeZone
	 * pastWeatherVariables is the past weather data of the location, preferably through BoM
	 * forecastWeatherVariables is the forecast output parameters (temperature, pressure and relative humidity) for the location
	 * and write forecast results of outputFolderTemp
	 * Finally, after all iterations,
	 * All files in outputFolderTemp (output forecast weather data for each individual location) are merged into a single file in outputFolder with name outputMergeFileName
	 * @param String[]
	 */
	public static void main(String[] args) {
		
		PropertyConfigurator.configure(Constants.INPUT_LOGCONFIG);

		Date executionStartDate = new Date();
		String rootDir = "";
		String inputFolder = rootDir + "input";
		String outputFolder = rootDir + "output";
		String outputFolderTemp = outputFolder + "/tmp";
		File inputJson = new File(rootDir + Constants.INPUT_LOCJSON);
		Iterator<JsonElement> iterator = null;
		
		JsonObject jsonObj = null;
		try {
			jsonObj = FileOperations.parseJson(inputJson);
			JsonArray locationArray = jsonObj.getAsJsonObject().getAsJsonArray("locations");
			iterator = locationArray.iterator();
			locationArray = null;
		} catch (FileNotFoundException e) {
			iterator = null;
			LOGGER.error("ERROR while parsing input json:" + inputJson);
			LOGGER.error(Utils.exceptionToString(e));
		}
		
		if(iterator!=null) {
			
			try {
				FileOperations.clearDirectory(outputFolderTemp);
			} catch (IOException e) {
				LOGGER.error("ERROR while cleaning temp forecast directory: " + outputFolderTemp);
				LOGGER.error(Utils.exceptionToString(e));
			}
			
			while (iterator.hasNext()) {
				InputData inputData = new InputData();
				PredictFunctions predictFunctions = new PredictFunctions();
				FileOperations fileOperations = new FileOperations();
				WeatherOutput weatherOutput = new WeatherOutput();
				Location location = new Location();
				WeatherVariables pastWeatherVariables = new WeatherVariables();
				WeatherVariables forecastWeatherVariables = new WeatherVariables();
				
				JsonObject jsonObject = iterator.next().getAsJsonObject();
				String locationName = jsonObject.get("name").getAsString();
				String iataCode = jsonObject.get("iatacode").getAsString();
				int foreCastSize = Constants.FORECAST_SIZE; 
				String dataFolder = inputFolder + "/data/" + iataCode + "/";
				
				try{
					LatLong latLong = inputData.getLatLongFromAPI(locationName, iataCode, inputFolder);
					Double elevation = inputData.getElevationFromLatLong(latLong, iataCode, inputFolder);
					String timeZoneId = inputData.getTimeZoneFromLatLong(latLong, iataCode, inputFolder);
					DateTime exeDateInLocTimeZone = new DateTime(executionStartDate.getTime(), DateTimeZone.forID(timeZoneId));
					DateTime forecastFromDate = exeDateInLocTimeZone.plusDays(1);
					pastWeatherVariables = inputData.getPastWeatherVariablesForLoc(iataCode, exeDateInLocTimeZone, dataFolder);
					forecastWeatherVariables = predictFunctions.getForecastWeatherVariables(pastWeatherVariables, foreCastSize);
					
					location.setIataCode(iataCode);
					location.setLatLong(latLong);
					location.setName(locationName);
					location.setElevation(elevation);
					location.setTimeZoneId(timeZoneId);
					weatherOutput.setDateTime(forecastFromDate);
					weatherOutput.setLocation(location);
					weatherOutput.setWeatherVariables(forecastWeatherVariables);
					
					fileOperations.writeForeCastResultToFile(weatherOutput, outputFolderTemp, foreCastSize);
				} catch (IOException e) {
					LOGGER.error("ERROR while processing location: " + locationName + " with code:" + iataCode);
					LOGGER.error(Utils.exceptionToString(e));
				} finally {
					jsonObject = null;
					inputData = null;
					predictFunctions = null;
					fileOperations = null;
					pastWeatherVariables = null;
					forecastWeatherVariables = null;
					location = null;
					weatherOutput = null;	
				}
			}
			
			FileOperations fileOperations = new FileOperations();
			String outputMergeFileName = "Forecast_" + Utils.dateToString(executionStartDate, "yyyy-MM-dd_HH-mm-ss") + Constants.OUTPUT_FILEEXTENSION;
			try {
				fileOperations.mergeLocOutputForecastFiles(outputFolderTemp, outputFolder + "/" + outputMergeFileName);
			} catch (IOException e) {
				LOGGER.error("ERROR while merging forecast files in:" + outputFolder);
				LOGGER.error(Utils.exceptionToString(e));
			} finally {
				fileOperations = null;
			}
		}
		
		inputJson = null;
		iterator = null;
		jsonObj = null;
	}

}
