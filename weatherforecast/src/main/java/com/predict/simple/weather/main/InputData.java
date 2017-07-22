package com.predict.simple.weather.main;

import java.io.File;
import java.io.FileNotFoundException;
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

/**
 * The InputData class gets latitude, longitude, elevation, timezone id and past weather data for a location
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class InputData {

	/**
	 * This method gets latitude and longitude from location name
	 * The method writes the output JSON of Google API to a file and parses the JSON to get the latitude and longitude
	 * @param String locationName, the name of the location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return LatLong latLong, forecasted parameters
	 */
	public LatLong getLatLongFromAPI(String locationName, String iataCode, String folderName) {
    	LatLong latLong = new LatLong();
		JsonObject jsonObject = new JsonObject();
		
//		Converts the output JSON to latLongJsonFile in latLongJsonPath
		String latLongJsonPath = folderName + "/" + iataCode + "_latlong.json";
		File latLongJsonFile = new File(latLongJsonPath);
        if(!latLongJsonFile.exists()) {
//        	http://maps.googleapis.com/maps/api/geocode/json?address=sydney
        	String url = Constants.API_BASE_GOOGLE_GEOCODE + "?address=" + locationName.replaceAll(" ", "%20");
        	FileOperations.urlToFile(url, latLongJsonFile);
        }
		
//      Parse the latLongJsonFile JSON to get latLong
    	jsonObject = FileOperations.parseJson(latLongJsonFile);
		JsonObject latLongJsonObject = jsonObject.getAsJsonObject().getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location");
		latLong.setLatitude(latLongJsonObject.get("lat").getAsDouble());
		latLong.setLongitude(latLongJsonObject.get("lng").getAsDouble());
		jsonObject = null;
    	return latLong;
    }
	
	/**
	 * This method gets elevation from latitude and longitude of location
	 * The method writes the output JSON of Google API to a file and parses the JSON to get the elevation
	 * @param LatLong latLong, the latitude and longitude of location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return Double elevation, the elevation of the location
	 */
	public Double getElevationFromLatLong(LatLong latLong, String iataCode, String folderName) {
		JsonObject jsonObject = new JsonObject();
		Double elevation = 0.0;
		
//		Converts the output JSON to elevationJsonFile in elevationJsonPath
		String elevationJsonPath = folderName + "/" + iataCode + "_elevtn.json";
		File elevationJsonFile = new File(elevationJsonPath);
		if(!elevationJsonFile.exists()) {
//			http://maps.googleapis.com/maps/api/elevation/json?locations=-33.86,151.20
        	String url = Constants.API_BASE_GOOGLE_ELEVATION + "?locations=" + latLong.getLatitude() + "," + latLong.getLongitude();
        	FileOperations.urlToFile(url, elevationJsonFile);
        }

//      Parse the elevationJsonFile JSON to get elevation
		jsonObject = FileOperations.parseJson(elevationJsonFile);
		elevation = jsonObject.getAsJsonObject().getAsJsonArray("results").get(0).getAsJsonObject().get("elevation").getAsDouble();
		jsonObject = null;
    	return elevation;
    }

	/**
	 * This method gets time zone id from latitude and longitude of location
	 * The method writes the output JSON of Google API to a file and parses the JSON to get the time zone id
	 * @param LatLong latLong, the latitude and longitude of location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return String timeZoneId, the time zone id of the location
	 */
	public String getTimeZoneFromLatLong(LatLong latLong, String iataCode, String folderName) {
		JsonObject jsonObject = new JsonObject();
		String timeZoneId = null;

//		Converts the output JSON to timeZoneIdJsonFile in timeZoneIdJsonPath
		String timeZoneIdJsonPath = folderName + "/" + iataCode + "_timezn.json";
		File timeZoneIdJsonFile = new File(timeZoneIdJsonPath);
		if(!timeZoneIdJsonFile.exists()) {
//			The below API has good details but has a common username and limited requests for it
//			http://api.geonames.org/timezoneJSON?lat=-33.8688197&lng=151.2092955&username=demo
//        	String url = Constants.API_BASE_GEONAME_TIMEZONE + "?lat=" + latLong.getLatitude() + "&lng=" + latLong.getLongitude() + "&username=demo";
			
//			https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=1331161200
        	String url = Constants.API_BASE_GOOGLE_TIMEZONE + "?location=" + latLong.getLatitude() + "," + latLong.getLongitude() + "&timestamp=" + (new Date().getTime()/1000);
        	FileOperations.urlToFile(url, timeZoneIdJsonFile);
        }

//      Parse the timeZoneIdJsonFile JSON to get timeZoneId
		jsonObject = FileOperations.parseJson(timeZoneIdJsonFile);
//		timeZoneId = jsonObject.getAsJsonObject().get("timezoneId").getAsString();
		timeZoneId = jsonObject.getAsJsonObject().get("timeZoneId").getAsString();
		jsonObject = null;
    	return timeZoneId;
    }
	
	/**
	 * This method gets past pressure, temperature and reltive humidity data from Bureau of Meteorology, Commonwealth of Australia
	 * The method writes the output data of BoM Climate Data API to csv files and parses them to get required weather paramterts
	 * @param LatLong latLong, the latitude and longitude of location
	 * @param String iataCode, the code preferably for prefixing the output json file
	 * @param String folderName, the directory path where the output json file has to be stored
	 * @return WeatherVariables weatherVariables, the past pressure, temperature and reltive humidity data in the form of array
	 */
	public WeatherVariables getPastWeatherVariablesForLoc(String iataCode, DateTime date, String dirPath) {
		WeatherVariables weatherVariables = new WeatherVariables();
		
//		At first, the variables are hold in list, as we don't know how much data is available
		List<Double> temperatureList = new LinkedList<Double>();
		List<Double> pressureList = new LinkedList<Double>();
		List<Double> relativeHumidityList = new LinkedList<Double>();
		
//		startDate is from when past weather data is available in BoM
		DateTime startDate = date.minusMonths(Constants.AUBOM_DATA_MONTHS);
		
//		Start getting weather data from startDate and reach till date, ensuring ascending order for forward arithmetic progression
		for (int i = 0; i <= Constants.AUBOM_DATA_MONTHS; i++) {
			DateTime downloadDataForDate = startDate.plusMonths(i);
			String downloadDataForDateString = Utils.dateTimeToString(downloadDataForDate, "YYYYMM");
			String datFilePath = dirPath + iataCode + "." + downloadDataForDateString + ".csv" ;
			File dataFile = new File(datFilePath);
			if(!dataFile.exists()) {
//				http://www.bom.gov.au/climate/dwo/201607/text/IDCJDW8014.201607.csv
	        	String url = Constants.API_BASE_AUBOM_CLIMATE + downloadDataForDateString + "/text/" + iataCode + "." + downloadDataForDateString + ".csv";
	        	FileOperations.urlToFile(url, dataFile);
	        }
			
			FileReader reader = null;
			try {
				reader = new FileReader(datFilePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

//			records is the csv file downloaded for the location for the period downloadDataForDateString
			Iterable<CSVRecord> records = null;
			try {
				records = CSVFormat.DEFAULT.withQuote(null).withDelimiter(',').parse(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}

//			Parse each csvRecord in CSVRecord and get the required value
			for (CSVRecord csvRecord : records) {
				try {
//					Ideal csvRecord has 22 values
					if (csvRecord.size() == 22) {
//						For checking if the csvRecord record has description or parsable/required data
						if(NumberUtils.isParsable(csvRecord.get(17))){
//							9am Temperature (°C)
							temperatureList.add(Utils.parseToDouble(csvRecord.get(10)));
							
//							9am relative humidity (%)
							relativeHumidityList.add(Utils.parseToDouble(csvRecord.get(11)));
							
//							9am MSL pressure (hPa)
							pressureList.add(Utils.parseToDouble(csvRecord.get(15)));
							
//							3pm Temperature (°C)
							temperatureList.add(Utils.parseToDouble(csvRecord.get(16)));
							
//							3pm relative humidity (%)
							relativeHumidityList.add(Utils.parseToDouble(csvRecord.get(17)));
							
//							3pm MSL pressure (hPa)
							pressureList.add(Utils.parseToDouble(csvRecord.get(21)));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			records = null;
			reader = null;
		}

//		Converting to primitive as ARIMA model can process only primitive values
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
