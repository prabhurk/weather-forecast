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
 * The Main class is the main method which invoked while jar run
 * The class contains the entire flow of weather data forecast
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class Main {

	/**
	 * The main method is invoked while while jar/executable run
	 * 
	 * @param String[]
	 */
	public static void main(String[] args) {

//		executionStartDate is the date at which execution happens, i.e., now when the executable runs, and is used to mark up the output file name
		Date executionStartDate = new Date();
		
//		rootDir "" means the Constants.INPUT_LOCJSON and inputFolder & outputFolder are in the same location as the jar
		String rootDir = "";
		
//		inputFolder is for holding input data such as lat/long, elevation & timezone jsons and BoM weather data
		String inputFolder = rootDir + "input";
		
//		outputFolder is for holding output forecast result file for all locations together
		String outputFolder = rootDir + "output";
		
//		outputFolderTemp is for holding output forecast result file for each location
		String outputFolderTemp = outputFolder + "/tmp";
		
//		inputJson contains the list of locations and their codes for which weather has to be forecasted
		File inputJson = new File(rootDir + Constants.INPUT_LOCJSON);
		
//		Getting each location object from list of location objects in inputJson
		JsonObject jsonObj = FileOperations.parseJson(inputJson);
		JsonArray locationArray = jsonObj.getAsJsonObject().getAsJsonArray("locations");
		Iterator<JsonElement> iterator = locationArray.iterator();

//		Cleaning outputFolderTempDir
		File outputFolderTempDir = new File(outputFolderTemp);
		try {
			if(outputFolderTempDir.exists() && outputFolderTempDir.isDirectory())
				FileUtils.cleanDirectory(new File(outputFolderTemp));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outputFolderTempDir = null;
		}

//		Iterating through each location object from the list of location objects in inputJson
		while (iterator.hasNext()) {
			InputData inputData = new InputData();
			PredictFunctions predictFunctions = new PredictFunctions();
			FileOperations fileOperations = new FileOperations();
			WeatherOutput weatherOutput = new WeatherOutput();
			Location location = new Location();
			
//			foreCastSize is the number of future forecast predictions for each locatio
			int foreCastSize = Constants.FORECAST_SIZE;
			
//			Get locationName and iataCode specified in the inputJson corresponding to the location object
			JsonObject jsonObject = iterator.next().getAsJsonObject();
			String locationName = jsonObject.get("name").getAsString();
			String iataCode = jsonObject.get("iatacode").getAsString();
			
//			dataFolder is for holding past weather data, preferably from BoM and the data is kept under each folder, folder name being the code given in inputJson 
			String dataFolder = inputFolder + "/data/" + iataCode + "/";

//			Get latitude and longitude from location name, preferably through Google API
			LatLong latLong = inputData.getLatLongFromAPI(locationName, iataCode, inputFolder);
			
//			Get elevation of the location from its latitude and longitude, preferably through Google API 
			Double elevation = inputData.getElevationFromLatLong(latLong, iataCode, inputFolder);
			
//			Get timezone id of the location from its latitude and longitude, preferably through Google API
			String timeZoneId = inputData.getTimeZoneFromLatLong(latLong, iataCode, inputFolder);
			
//			exeDateInLocTimeZone is the executionStartDate, in the local time of the location
			DateTime exeDateInLocTimeZone = new DateTime(executionStartDate.getTime(), DateTimeZone.forID(timeZoneId));
			
//			forecastFromDate is the date from which forecasting begin, preferably the next day of exeDateInLocTimeZone
			DateTime forecastFromDate = exeDateInLocTimeZone.plusDays(1);
			
//			pastWeatherVariables is the past weather data of the location, preferably through BoM
			WeatherVariables pastWeatherVariables = inputData.getPastWeatherVariablesForLoc(iataCode, exeDateInLocTimeZone, dataFolder);
			
//			forecastWeatherVariables is the forecast output parameters (temperature, pressure and relative humidity) for the location
			WeatherVariables forecastWeatherVariables = predictFunctions.getForecastWeatherVariables(pastWeatherVariables, foreCastSize);
			
			location.setIataCode(iataCode);
			location.setLatLong(latLong);
			location.setName(locationName);
			location.setElevation(elevation);
			location.setTimeZoneId(timeZoneId);
			weatherOutput.setDateTime(forecastFromDate);
			weatherOutput.setLocation(location);
			weatherOutput.setWeatherVariables(forecastWeatherVariables);
			
//			Write forecast results of outputFolderTemp
			fileOperations.writeForeCastResultToFile(weatherOutput, outputFolderTemp, foreCastSize);
			
			jsonObject = null;
			inputData = null;
			predictFunctions = null;
			fileOperations = null;
			pastWeatherVariables = null;
			forecastWeatherVariables = null;
			location = null;
			weatherOutput = null;
		}
		
		FileOperations fileOperations = new FileOperations();
//		Merge all files in outputFolderTemp (output forecast weather data for each individual location) into a single file in outputFolder with name outputMergeFileName
		String outputMergeFileName = "Forecast_" + Utils.dateToString(executionStartDate, "yyyy-MM-dd_HH-mm-ss") + Constants.OUTPUT_FILEEXTENSION;
		fileOperations.mergeLocOutputForecastFiles(outputFolderTemp, outputFolder + "/" + outputMergeFileName);
		fileOperations = null;
	}
}
