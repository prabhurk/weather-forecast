/**
 * The FileOperations class contains methods for performing actions on/to file
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.predict.simple.weather.model.Location;
import com.predict.simple.weather.model.WeatherOutput;
import com.predict.simple.weather.util.Constants;
import com.predict.simple.weather.util.Utils;

public class FileOperations {	

	/**
	 * This method converts/downloads URL to given file
	 * @param String urlString, the URL address
	 * @param File file, the file
	 * @return boolean isUrlToFileSuccess, whether the conversion/download was successful or not (true if success, else failure)
	 * @throws IOException 
	 */
	public static boolean urlToFile(String urlString, File file) throws IOException {
		boolean isUrlToFileSuccess = false;
		URL url = new URL(urlString);
		FileUtils.copyURLToFile(url, file);
		if (file.exists())
			isUrlToFileSuccess = true;
		url = null;
		return isUrlToFileSuccess;
	}
	
	/**
	 * This method parses given JSON file to a JSON object
	 * @param File json, the json file
	 * @return JsonObject jsonObject, the json object of json file
	 * @throws FileNotFoundException 
	 */
	public static JsonObject parseJson(File json) throws FileNotFoundException {
		JsonObject jsonObject = new JsonObject();
		Gson gson = new GsonBuilder().create();
		FileReader fileReader = new FileReader(json);
		jsonObject = gson.fromJson(fileReader, JsonObject.class);
		gson = null;
		return jsonObject;
	}
	
	/**
	 * This method cleans the given directory
	 * @param String directoryPath, the path of the directory
	 * @return boolean isClearSuccess, whether the directory clean was successful or not (true if success, else failure)
	 * @throws IOException 
	 */
	public static boolean clearDirectory(String directoryPath) throws IOException {
		boolean isClearSuccess = false;
		File outputFolderTempDir = new File(directoryPath);
		if(outputFolderTempDir.exists() && outputFolderTempDir.isDirectory()) {
			FileUtils.cleanDirectory(outputFolderTempDir);
			isClearSuccess = true;
		}
		outputFolderTempDir = null;
		return isClearSuccess;
	}
	
	/**
	 * This method writes forecast results to file
	 * outputFileName is the name of forecast output file, written under the directory folderName
	 * Each forecast will be tagged to 9am and 3pm of the day (in local time zone), hence incrementing a day is only after two tags (expressed in UTC)
	 * @param WeatherOutput weatherOutput, the forecast values
	 * @param String folderName, the directory under which file has to be written
	 * @param int forecastSize, the expected number of forecast output
	 * @return boolean fileWriteStatus, the status of file write (true if success, else failure)
	 * @throws IOException 
	 */
	public boolean writeForeCastResultToFile(WeatherOutput weatherOutput, String folderName, int forecastSize) throws IOException {
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
			outputFile.getParentFile().mkdirs(); 
			outputFile.createNewFile();
		}
		
		try {
			fileWriter = new FileWriter(outputFile);
			bufferedWrter = new BufferedWriter(fileWriter);
			
			for(int i=0; i < forecastSize; i++){
				double temperatureValue = temperatureArray[i];
				double pressureValue = pressureArray[i];
				double relativeHumidityValue = relativeHumidityArray[i];
				
				if(i%2==0){
					if(i==0) {
						dateTime = dateTime.plusDays(1);
						dateTime = dateTime.withTime(9, 0, 0, 0);
					} else {
						dateTime = dateTime.plusHours(18);
					}
				} else {
					dateTime = dateTime.plusHours(6);
				}
				
				String latitude = Utils.formatDecimal(location.getLatLong().getLatitude(), "0.00");
				String longitude = Utils.formatDecimal(location.getLatLong().getLongitude(), "0.00");
				String elevation = Utils.formatDecimal(location.getElevation(), "0");				
				String localDateTimeInUTC = Utils.dateTimeToUTC(dateTime);
				
//				The below two if's are for handling unusual bugs of very large/small double values in ARIMA model
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
				String pressure = Utils.formatDecimal(pressureValue, "0.0");
				String relativeHumidity = Utils.formatDecimal(relativeHumidityValue, "0");
				
				String output = location.getName() + Constants.PIPE + latitude + Constants.COMMA + longitude
						+ Constants.COMMA + elevation + Constants.PIPE + localDateTimeInUTC + Constants.PIPE
						+ weatherCondition + Constants.PIPE + temperature + Constants.PIPE + pressure + Constants.PIPE
						+ relativeHumidity;
				bufferedWrter.write(output + Constants.NEW_LINE);
			}
			fileWriteStatus = true;
		} catch (IOException e) {
			throw e;
		} finally {
			if (bufferedWrter != null)
				bufferedWrter.close();
			if (fileWriter != null)
				fileWriter.close();
		}
		return fileWriteStatus;
	}

	/**
	 * This method merges (appends) individual location forecast result files (in outputFolderTemp) to a single file
	 * Individual location forecast result files will not be deleted upon merge
	 * Output mergeFile will be created along with parent directories, if not exists
	 * @param String outputFolderTemp, the directory under which individual location forecast result files reside
	 * @param String outputMergeFile, the single merge output file
	 * @return boolean mergerStatus, the status of file merge (true if success, else failure)
	 * @throws IOException 
	 */
	public boolean mergeLocOutputForecastFiles(String outputFolderTemp, String outputMergeFile) throws IOException {
		boolean mergerStatus = false;
		File folder = new File(outputFolderTemp);
		if(folder.exists() && folder.isDirectory()) {
			File[] listOfFiles = folder.listFiles();
			Charset charset = StandardCharsets.UTF_8;
			File mergeFile = new File(outputMergeFile);
			if(!mergeFile.exists()){
				mergeFile.getParentFile().mkdirs(); 
				mergeFile.createNewFile();
			}
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	String fileName = listOfFiles[i].getName();
		    	if(fileName.endsWith(Constants.OUTPUT_FILEEXTENSION)){
		    		String fileString = FileUtils.readFileToString(listOfFiles[i], charset);
					FileUtils.write(mergeFile, fileString, charset, true);
					fileString = null;
		    	}
		      }
		      mergerStatus = true; 
		    }
		    charset = null;
		    mergeFile = null;
		    listOfFiles = null;
		}
		folder = null;
	    return mergerStatus;
	}

}
