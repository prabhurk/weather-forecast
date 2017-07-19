package com.predict.simple.weather.main;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.predict.simple.weather.model.LatLong;
import com.predict.simple.weather.model.Location;
import com.predict.simple.weather.model.WeatherOutput;
import com.predict.simple.weather.model.WeatherVariables;
import com.predict.simple.weather.util.Constants;
import com.predict.simple.weather.util.Utils;

/**
 * The Main Class is the main method which invoked while jar run. The Class
 * contains the entire flow of weather data generation
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class Main {

	/**
	 * The main method is invoked while while jar run
	 * 
	 * @param String[]
	 *            
	 */
	public static void main(String[] args) {

		Date executionStartDate = new Date();

		String rootDir = "";
		String inputFolder = rootDir + "input";
		String outputFolder = rootDir + "output";
		String outputFolderTemp = outputFolder + "/tmp";
		
		File inputJson = new File(rootDir + "input_locations.json");
		JsonObject jsonObj = FileOperations.parseJson(inputJson);
		JsonArray locationArray = jsonObj.getAsJsonObject().getAsJsonArray("locations");
		Iterator<JsonElement> iterator = locationArray.iterator();

		File outputFolderTempDir = new File(outputFolderTemp);
		try {
			if(outputFolderTempDir.exists() && outputFolderTempDir.isDirectory()){
				FileUtils.cleanDirectory(new File(outputFolderTemp));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outputFolderTempDir = null;
		}

		while (iterator.hasNext()) {
			
			InputData inputData = new InputData();
			PredictFunctions predictFunctions = new PredictFunctions();
			FileOperations fileOperations = new FileOperations();
			WeatherOutput weatherOutput = new WeatherOutput();
			Location location = new Location();
			
			JsonObject jsonObject = iterator.next().getAsJsonObject();
			String locationName = jsonObject.get("name").getAsString();
			String iataCode = jsonObject.get("iatacode").getAsString();
			
			String dataFolder = inputFolder + "/data/" + iataCode + "/";

			LatLong latLong = inputData.getLatLongFromAPI(locationName, iataCode, inputFolder);
			Double elevation = inputData.getElevationFromLatLong(latLong, iataCode, inputFolder);
			String timeZoneId = inputData.getTimeZoneFromLatLong(latLong, iataCode, inputFolder);
			
			DateTime exeDateInLocTimeZone = new DateTime(executionStartDate.getTime(), DateTimeZone.forID(timeZoneId));
			DateTime forecastFromDate = exeDateInLocTimeZone.plusDays(1);
			
			WeatherVariables pastWeatherVariables = inputData.getPastWeatherVariablesForLoc(iataCode, exeDateInLocTimeZone, dataFolder);
			WeatherVariables forecastWeatherVariables = predictFunctions.getForecastWeatherVariables(pastWeatherVariables, Constants.FORECAST_SIZE);
			
			location.setIataCode(iataCode);
			location.setLatLong(latLong);
			location.setName(locationName);
			location.setElevation(elevation);
			location.setTimeZoneId(timeZoneId);
			weatherOutput.setDateTime(forecastFromDate);
			weatherOutput.setLocation(location);
			weatherOutput.setWeatherVariables(forecastWeatherVariables);
			
			fileOperations.writeForeCastResultToFile(weatherOutput, outputFolderTemp, Constants.FORECAST_SIZE);
			
			inputData = null;
			predictFunctions = null;
			fileOperations = null;
		}
		
		FileOperations fileOperations = new FileOperations();
		
		String outputMergeFileName = "Forecast_" + Utils.dateToString(executionStartDate, "yyyy-MM-dd_HH-mm-ss") + Constants.OUTPUT_FILEEXTENSION;
		fileOperations.mergeLocOutputForecastFiles(outputFolderTemp, outputFolder + "/" + outputMergeFileName);

		fileOperations = null;
	}

}
