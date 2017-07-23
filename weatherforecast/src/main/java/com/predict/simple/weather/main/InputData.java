/**
 * The InputData class gets latitude, longitude, elevation, timezone id and past weather data for a location
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;

import com.google.gson.JsonObject;
import com.predict.simple.weather.model.LatLong;
import com.predict.simple.weather.model.WeatherVariables;
import com.predict.simple.weather.util.Constants;
import com.predict.simple.weather.util.Utils;

public class InputData {

	/**
	 * This method gets latitude and longitude from location name
	 * The method writes the output JSON of Google API to a file (latLongJsonFile in latLongJsonPath) and parses the JSON to get the latitude and longitude (latLong)
	 * Sample url:
	 * http://maps.googleapis.com/maps/api/geocode/json?address=sydney
	 * @param String locationName, the name of the location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return LatLong latLong, forecasted parameters
	 * @throws IOException 
	 */
	public LatLong getLatLongFromAPI(String locationName, String iataCode, String folderName) throws IOException {
    	LatLong latLong = new LatLong();
		JsonObject jsonObject = new JsonObject();
		
		String latLongJsonPath = folderName + "/" + iataCode + "_latlong.json";
		File latLongJsonFile = new File(latLongJsonPath);
        if(!latLongJsonFile.exists()) {
        	String url = Constants.API_BASE_GOOGLE_GEOCODE + "?address=" + locationName.replaceAll(" ", "%20");
        	FileOperations.urlToFile(url, latLongJsonFile);
        }
		
    	jsonObject = FileOperations.parseJson(latLongJsonFile);
		JsonObject latLongJsonObject = jsonObject.getAsJsonObject().getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location");
		latLong.setLatitude(latLongJsonObject.get("lat").getAsDouble());
		latLong.setLongitude(latLongJsonObject.get("lng").getAsDouble());
		jsonObject = null;
    	return latLong;
    }
	
	/**
	 * This method gets elevation from latitude and longitude of location
	 * The method writes the output JSON of Google API to a file (elevationJsonFile in elevationJsonPath) and parses the JSON to get the elevation
	 * Sample url:
	 * http://maps.googleapis.com/maps/api/elevation/json?locations=-33.86,151.20
	 * @param LatLong latLong, the latitude and longitude of location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return Double elevation, the elevation of the location
	 * @throws IOException 
	 */
	public Double getElevationFromLatLong(LatLong latLong, String iataCode, String folderName) throws IOException {
		JsonObject jsonObject = new JsonObject();
		Double elevation = 0.0;
		
		String elevationJsonPath = folderName + "/" + iataCode + "_elevtn.json";
		File elevationJsonFile = new File(elevationJsonPath);
		if(!elevationJsonFile.exists()) {
        	String url = Constants.API_BASE_GOOGLE_ELEVATION + "?locations=" + latLong.getLatitude() + "," + latLong.getLongitude();
        	FileOperations.urlToFile(url, elevationJsonFile);
        }

		jsonObject = FileOperations.parseJson(elevationJsonFile);
		elevation = jsonObject.getAsJsonObject().getAsJsonArray("results").get(0).getAsJsonObject().get("elevation").getAsDouble();
		jsonObject = null;
    	return elevation;
    }

	/**
	 * This method gets time zone id from latitude and longitude of location
	 * The method writes the output JSON of Google API to a file (timeZoneIdJsonFile timeZoneIdJsonPath) and parses the JSON to get the time zone id (timeZoneId)
	 * Sample url:
	 * https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=1331161200
	 * @param LatLong latLong, the latitude and longitude of location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return String timeZoneId, the time zone id of the location
	 * @throws IOException 
	 */
	public String getTimeZoneFromLatLong(LatLong latLong, String iataCode, String folderName) throws IOException {
		JsonObject jsonObject = new JsonObject();
		String timeZoneId = null;

		String timeZoneIdJsonPath = folderName + "/" + iataCode + "_timezn.json";
		File timeZoneIdJsonFile = new File(timeZoneIdJsonPath);
		if(!timeZoneIdJsonFile.exists()) {
        	String url = Constants.API_BASE_GOOGLE_TIMEZONE + "?location=" + latLong.getLatitude() + "," + latLong.getLongitude() + "&timestamp=" + (new Date().getTime()/1000);
        	FileOperations.urlToFile(url, timeZoneIdJsonFile);
        }

		jsonObject = FileOperations.parseJson(timeZoneIdJsonFile);
		timeZoneId = jsonObject.getAsJsonObject().get("timeZoneId").getAsString();
		jsonObject = null;
    	return timeZoneId;
    }
	
	/**
	 * This method gets past pressure, temperature and reltive humidity data from Bureau of Meteorology, Commonwealth of Australia
	 * Sample url:
	 * http://www.bom.gov.au/climate/dwo/201607/text/IDCJDW8014.201607.csv
	 * The method writes the output data of BoM Climate Data API to csv files and parses them to get required weather parameters
	 * startDate is from when past weather data is available in BoM
	 * Weather data is got from startDate till date, ensuring ascending order for forward arithmetic progression
	 * At first, weather variables are hold in list, as we don't know how much data is available. Finally, they are converted to primitive as ARIMA model can process only primitive values
	 * records is the csv file downloaded for the location for the period downloadDataForDateString
	 * Each csvRecord in CSVRecord is parsed to get the required value
	 * Ideal csvRecord has 22 values
	 * csvRecord.get(10) = 9am Temperature (°C)
	 * csvRecord.get(11) = 9am relative humidity (%)
	 * csvRecord.get(15) = 9am MSL pressure (hPa)
	 * csvRecord.get(16) = 3pm Temperature (°C)
	 * csvRecord.get(17) = 3pm relative humidity (%)
	 * csvRecord.get(21) = 3pm MSL pressure (hPa)
	 * @param LatLong latLong, the latitude and longitude of location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return WeatherVariables weatherVariables, the past pressure, temperature and reltive humidity data in the form of array
	 * @throws IOException 
	 */
	public WeatherVariables getPastWeatherVariablesForLoc(String iataCode, DateTime date, String dirPath) throws IOException {
		WeatherVariables weatherVariables = new WeatherVariables();
		List<Double> temperatureList = new LinkedList<Double>();
		List<Double> pressureList = new LinkedList<Double>();
		List<Double> relativeHumidityList = new LinkedList<Double>();
		
		DateTime startDate = date.minusMonths(Constants.AUBOM_DATA_MONTHS);
		
		for (int i = 0; i <= Constants.AUBOM_DATA_MONTHS; i++) {
			DateTime downloadDataForDate = startDate.plusMonths(i);
			String downloadDataForDateString = Utils.dateTimeToString(downloadDataForDate, "YYYYMM");
			String datFilePath = dirPath + iataCode + "." + downloadDataForDateString + ".csv" ;
			File dataFile = new File(datFilePath);
			if(!dataFile.exists()) {
	        	String url = Constants.API_BASE_AUBOM_CLIMATE + downloadDataForDateString + "/text/" + iataCode + "." + downloadDataForDateString + ".csv";
	        	FileOperations.urlToFile(url, dataFile);
	        }
			
			FileReader reader = new FileReader(datFilePath);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withQuote(null).withDelimiter(',').parse(reader);
			for (CSVRecord csvRecord : records) {
				if (csvRecord.size() == 22) {
//					For checking if the csvRecord record has description or parsable/required data
					if(NumberUtils.isParsable(csvRecord.get(17))){
						temperatureList.add(Utils.parseToDouble(csvRecord.get(10)));
						relativeHumidityList.add(Utils.parseToDouble(csvRecord.get(11)));
						pressureList.add(Utils.parseToDouble(csvRecord.get(15)));
						temperatureList.add(Utils.parseToDouble(csvRecord.get(16)));
						relativeHumidityList.add(Utils.parseToDouble(csvRecord.get(17)));
						pressureList.add(Utils.parseToDouble(csvRecord.get(21)));
					}
				}
			}
			records = null;
			reader = null;
		}

		double[] temperature = Utils.linkedListToPrimitiveArray(temperatureList);
		double[] pressure = Utils.linkedListToPrimitiveArray(pressureList);
		double[] relativeHumidity = Utils.linkedListToPrimitiveArray(relativeHumidityList);
		
		weatherVariables.setTemperature(temperature);
		weatherVariables.setPressure(pressure);
		weatherVariables.setRelativeHumidity(relativeHumidity);
		
		temperatureList = null;
		pressureList = null;
		relativeHumidityList = null;
		temperature = null;
		pressure = null;
		relativeHumidity = null;

		return weatherVariables;
    }

}
