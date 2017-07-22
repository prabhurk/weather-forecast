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
 * The FileOperations class contains methods for performing actions on/to file
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class FileOperations {	

	/**
	 * This method converts/downloads URL to given file
	 * @param String urlString, the URL address
	 * @param File file, the file
	 * @return boolean isUrlToFileSuccess, whether the conversion/download was successful or not (true if success, else failure)
	 */
	public static boolean urlToFile(String urlString, File file) {
		boolean isUrlToFileSuccess = false;
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			FileUtils.copyURLToFile(url, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.exists())
			isUrlToFileSuccess = true;
		url = null;
		return isUrlToFileSuccess;
	}
	
	/**
	 * This method parses given JSON file to a JSON object
	 * @param File json, the json file
	 * @return JsonObject jsonObject, the json object of json file
	 */
	public static JsonObject parseJson(File json) {
		JsonObject jsonObject = new JsonObject();
		Gson gson = new GsonBuilder().create();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(json);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		try {
			jsonObject = gson.fromJson(fileReader, JsonObject.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		}
		gson = null;
		return jsonObject;
	}
	
	/**
	 * This method writes forecast results to file
	 * @param WeatherOutput weatherOutput, the forecast values
	 * @param String folderName, the directory under which file has to be written
	 * @param int forecastSize, the expected number of forecast output
	 * @return boolean fileWriteStatus, the status of file write (true if success, else failure)
	 */
	public boolean writeForeCastResultToFile(WeatherOutput weatherOutput, String folderName, int forecastSize) {
		boolean fileWriteStatus = false;
		BufferedWriter bufferedWrter = null;
		FileWriter fileWriter = null;
		PredictFunctions predictFunctions = new PredictFunctions();

//		outputFileName is the name of forecast output file
		String outputFileName = weatherOutput.getLocation().getIataCode() + Constants.OUTPUT_FILEEXTENSION;
		
		Location location = weatherOutput.getLocation();
		double[] temperatureArray = weatherOutput.getWeatherVariables().getTemperature();
		double[] pressureArray = weatherOutput.getWeatherVariables().getPressure();
		double[] relativeHumidityArray = weatherOutput.getWeatherVariables().getRelativeHumidity();
		DateTime dateTime = weatherOutput.getDateTime();

//		outputFileName will be written under the directory folderName
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
				
//				Each forecast will be tagged to 9am and 3pm of the day, hence incrementing a day is only after two tags
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
//				Express dateTime in local time zone into UTC time zone
				String localDateTimeInUTC = Utils.dateTimeToUTC(dateTime);
				
//				The below if's are for handling unusual bugs of very large/small double values in ARIMA model
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

	/**
	 * This method merges individual location forecast result files to single file
	 * Individual location forecast result files will not be deleted upon merge
	 * @param String outputFolderTemp, the directory under which individual location forecast result files reside
	 * @param String outputMergeFile, the single merge output file
	 * @return boolean mergerStatus, the status of file merge (true if success, else failure)
	 */
	public boolean mergeLocOutputForecastFiles(String outputFolderTemp, String outputMergeFile) {
		boolean mergerStatus = false;
		File folder = new File(outputFolderTemp);

		if(folder.exists() && folder.isDirectory()) {
//			Get all files in the outputFolderTemp
			File[] listOfFiles = folder.listFiles();
			Charset charset = StandardCharsets.UTF_8;
			File mergeFile = new File(outputMergeFile);
//			Create mergeFile if not exists, with parent directories
			if(!mergeFile.exists()){
				try {
					mergeFile.getParentFile().mkdirs(); 
					mergeFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			Iterating through all files in the outputFolderTemp			
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	String fileName = listOfFiles[i].getName();
		    	if(fileName.endsWith(Constants.OUTPUT_FILEEXTENSION)){
		    		String fileString = null;
		    		try {
			    		fileString = FileUtils.readFileToString(listOfFiles[i], charset);
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    		try {
//		    			Appends each fileString to  mergeFile
						FileUtils.write(mergeFile, fileString, charset, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
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
