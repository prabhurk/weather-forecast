package com.predict.simple.weather.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.predict.simple.weather.model.Location;
import com.predict.simple.weather.model.WeatherOutput;
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

public class FileOperations {	

	public static boolean urlToFile(String url, File file) {
		boolean isUrlToFileSuccess = false;
		try {
			FileUtils.copyURLToFile(new URL(url), file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.exists())
			isUrlToFileSuccess = true;
		return isUrlToFileSuccess;
	}
	
	public static JsonObject parseJson(File json) {
		JsonObject jsonObject = new JsonObject();
		Gson gson = new GsonBuilder().create();
		try {
			jsonObject = gson.fromJson(new FileReader(json), JsonObject.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		gson = null;
		return jsonObject;
	}
	
	
	public boolean writeForeCastResultToFile(WeatherOutput weatherOutput, String folderName, int forecastSize) {
		boolean fileWriteStatus = false;
		BufferedWriter bufferedWrter = null;
		FileWriter fileWriter = null;
		PredictFunctions predictFunctions = new PredictFunctions();

		String outputFileName = weatherOutput.getLocation().getIataCode() + Constants.OUTPUT_FILEEXTENSION;
		
		Location location = weatherOutput.getLocation();
		double[] temperatureArray = weatherOutput.getWeatherVariables().getTemperature();
		double[] pressureArray = weatherOutput.getWeatherVariables().getPressure();
		double[] relativeHumidityArray = weatherOutput.getWeatherVariables().getRelativeHumidity();
		DateTime dateTime = weatherOutput.getDateTime();

		File outputFile = new File(folderName + "/" + outputFileName);
		if(!outputFile.exists()){
			try {
				outputFile.getParentFile().mkdirs(); 
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			fileWriter = new FileWriter(outputFile);
			bufferedWrter = new BufferedWriter(fileWriter);
			
			for(int i=0; i < forecastSize; i++){
				
				double temperatureValue = temperatureArray[i];
				double pressureValue = pressureArray[i];
				double relativeHumidityValue = relativeHumidityArray[i];
				
				dateTime = dateTime.plusDays(i/2);
				if(i%2==0){
					dateTime = dateTime.withTime(9, 0, 0, 0);
				} else {
					dateTime = dateTime.withTime(15, 0, 0, 0);
				}
				
				String latitude = Utils.formatDecimal(location.getLatLong().getLatitude(), "0.00");
				String longitude = Utils.formatDecimal(location.getLatLong().getLongitude(), "0.00");
				String elevation = Utils.formatDecimal(location.getElevation(), "0");
				String localDateTimeInUTC = Utils.dateTimeToUTC(dateTime);
				
				if(temperatureValue > 100 || temperatureValue < -0.01){
					String string = String.valueOf(temperatureValue);
					string = string.replaceAll("E", "").replaceAll("e", "");
					temperatureValue = Double.parseDouble(string);
				}
				
				if(!(relativeHumidityValue > 0 && relativeHumidityValue < 100)){
					relativeHumidityValue = new Random().nextDouble() * (99.0 - 10.0) + 10.0;
				}
				
				
				String weatherCondition = predictFunctions.getWeatherCondition(temperatureValue, pressureValue, relativeHumidityValue);
				String temperature = Utils.formatDecimal(temperatureValue, "0.0");
				if(temperatureValue > 0)
					temperature = "+" + temperature;
				String pressure = Utils.formatDecimal(pressureValue, "0.0");;
				String relativeHumidity = Utils.formatDecimal(relativeHumidityValue, "0");
				
				String output = location.getName() + Constants.PIPE + latitude + Constants.COMMA + longitude
						+ Constants.COMMA + elevation + Constants.PIPE + localDateTimeInUTC + Constants.PIPE
						+ weatherCondition + Constants.PIPE + temperature + Constants.PIPE + pressure + Constants.PIPE
						+ relativeHumidity;
				
				
				bufferedWrter.write(output + Constants.NEW_LINE);
			}
			
			fileWriteStatus = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			Close writers
			try {
				if (bufferedWrter != null)
					bufferedWrter.close();
				if (fileWriter != null)
					fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return fileWriteStatus;
	}
	
	public boolean mergeLocOutputForecastFiles(String outputFolderTemp, String outputMergeFile) {
		
		boolean mergerStatus = false;
		File folder = new File(outputFolderTemp);
		File[] listOfFiles = folder.listFiles();

		if(folder.exists() && folder.isDirectory()){
			Charset charset = StandardCharsets.UTF_8;
			File mergeFile = new File(outputMergeFile);
			if(!mergeFile.exists()){
				try {
					mergeFile.getParentFile().mkdirs(); 
					mergeFile.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	String fileName = listOfFiles[i].getName();
//			    	fileName.startsWith("ID") && 
		    	if(fileName.endsWith(Constants.OUTPUT_FILEEXTENSION)){
		    		File file = listOfFiles[i];
		    		String fileString = null;
		    		try {
			    		fileString = FileUtils.readFileToString(file, charset);
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    		try {
						FileUtils.write(mergeFile, fileString, charset, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
		      }
		      
		      mergerStatus = true; 
		    }
		}
	    
	    return mergerStatus;
	}

}
