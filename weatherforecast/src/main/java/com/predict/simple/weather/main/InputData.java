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
 * The Main Class is the main method which invoked while jar run. The Class
 * contains the entire flow of weather data generation
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class InputData {

	public LatLong getLatLongFromAPI(String locationName, String iataCode, String folderName) {
    	LatLong latLong = new LatLong();
		JsonObject jsonObject = new JsonObject();
		
		String latLongJsonPath = folderName + "/" + iataCode + "_latlong.json";
		File latLongJsonFile = new File(latLongJsonPath);
         
        if(!latLongJsonFile.exists()) {
//        	http://maps.googleapis.com/maps/api/geocode/json?address=perth
        	String url = Constants.API_BASE_GOOGLE_GEOCODE + "?address=" + locationName.replaceAll(" ", "%20");
        	FileOperations.urlToFile(url, latLongJsonFile);
        }
		
    	jsonObject = FileOperations.parseJson(latLongJsonFile);
		JsonObject latLongJsonObject = jsonObject.getAsJsonObject().getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location");
		latLong.setLatitude(latLongJsonObject.get("lat").getAsDouble());
		latLong.setLongitude(latLongJsonObject.get("lng").getAsDouble());
    	return latLong;
    }
	
	public Double getElevationFromLatLong(LatLong latLong, String iataCode, String folderName) {
		JsonObject jsonObject = new JsonObject();
		Double elevation = 0.0;
		
		String elevationJsonPath = folderName + "/" + iataCode + "_elevtn.json";
		File elevationJsonFile = new File(elevationJsonPath);
        
		if(!elevationJsonFile.exists()) {
//			http://maps.googleapis.com/maps/api/elevation/json?locations=-33.86,151.20
        	String url = Constants.API_BASE_GOOGLE_ELEVATION + "?locations=" + latLong.getLatitude() + "," + latLong.getLongitude();
        	FileOperations.urlToFile(url, elevationJsonFile);
        }

		jsonObject = FileOperations.parseJson(elevationJsonFile);
		elevation = jsonObject.getAsJsonObject().getAsJsonArray("results").get(0).getAsJsonObject().get("elevation").getAsDouble();
    	return elevation;
    }
	
	public String getTimeZoneFromLatLong(LatLong latLong, String iataCode, String folderName) {
		JsonObject jsonObject = new JsonObject();
		String timeZoneId = null;
		
		String timeZoneIdJsonPath = folderName + "/" + iataCode + "_timezn.json";
		File timeZoneIdJsonFile = new File(timeZoneIdJsonPath);
        
		if(!timeZoneIdJsonFile.exists()) {
//			http://api.geonames.org/timezoneJSON?lat=-33.8688197&lng=151.2092955&username=demo
//        	String url = Constants.API_BASE_GEONAME_TIMEZONE + "?lat=" + latLong.getLatitude() + "&lng=" + latLong.getLongitude() + "&username=demo";
			
//			https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=1331161200
        	String url = Constants.API_BASE_GOOGLE_TIMEZONE + "?location=" + latLong.getLatitude() + "," + latLong.getLongitude() + "&timestamp=" + (new Date().getTime()/1000);
        	FileOperations.urlToFile(url, timeZoneIdJsonFile);
        }

		jsonObject = FileOperations.parseJson(timeZoneIdJsonFile);
//		timeZoneId = jsonObject.getAsJsonObject().get("timezoneId").getAsString();
		timeZoneId = jsonObject.getAsJsonObject().get("timeZoneId").getAsString();
    	return timeZoneId;
    }
	
	public WeatherVariables getPastWeatherVariablesForLoc(String iataCode, DateTime date, String dirPath) {
		
		WeatherVariables weatherVariables = new WeatherVariables();
		
		List<Double> temperatureList = new LinkedList<Double>();
		List<Double> pressureList = new LinkedList<Double>();
		List<Double> relativeHumidityList = new LinkedList<Double>();
		
//		Date startDate = DateUtils.addMonths(date, -Constants.dataTill);
		DateTime startDate = date.minusMonths(Constants.AUBOM_DATA_MONTHS);
		
		for (int i = 0; i <= Constants.AUBOM_DATA_MONTHS; i++) {
//			Date downloadDataForDate = DateUtils.addMonths(startDate, i);
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

			Iterable<CSVRecord> records = null;
			try {
				records = CSVFormat.DEFAULT.withQuote(null).withDelimiter(',').parse(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}

			
			for (CSVRecord csvRecord : records) {
				try {
					if (csvRecord.size() == 22) {
						if(NumberUtils.isParsable(csvRecord.get(17))){
							temperatureList.add(Utils.parseToDouble(csvRecord.get(10)));
							relativeHumidityList.add(Utils.parseToDouble(csvRecord.get(11)));
							pressureList.add(Utils.parseToDouble(csvRecord.get(15)));
							temperatureList.add(Utils.parseToDouble(csvRecord.get(16)));
							relativeHumidityList.add(Utils.parseToDouble(csvRecord.get(17)));
							pressureList.add(Utils.parseToDouble(csvRecord.get(21)));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		double[] temperature = Utils.linkedListToPrimitiveArray(temperatureList);
		double[] pressure = Utils.linkedListToPrimitiveArray(pressureList);
		double[] relativeHumidity = Utils.linkedListToPrimitiveArray(relativeHumidityList);
		
		weatherVariables.setTemperature(temperature);
		weatherVariables.setPressure(pressure);
		weatherVariables.setRelativeHumidity(relativeHumidity);

		return weatherVariables;
    }

}
