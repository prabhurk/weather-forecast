package com.predict.simple.weather.util;

/**
 * The Constants Class has unchanged constants used in various places in the program.
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 */

public class Constants {

//	Below constants represent the delimiters and symbols
	public static final String PIPE = "|";
	public static final String COMMA = ",";	
	public static final String UNDER_SCORE = "_";	
	public static final String NEW_LINE = "\r\n";

	public static final String API_BASE_GOOGLE_GEOCODE = "http://maps.googleapis.com/maps/api/geocode/json";
	public static final String API_BASE_GOOGLE_ELEVATION = "http://maps.googleapis.com/maps/api/elevation/json";
	public static final String API_BASE_GOOGLE_TIMEZONE = "https://maps.googleapis.com/maps/api/timezone/json";
	public static final String API_BASE_GEONAME_TIMEZONE = "http://api.geonames.org/timezoneJSON";
	public static final String API_BASE_AUBOM_CLIMATE = "http://www.bom.gov.au/climate/dwo/";
	
	public static final int AUBOM_DATA_MONTHS = 13;
	public static final int FORECAST_SIZE = 7;
	
	public static final String OUTPUT_FILEEXTENSION = ".txt";
	
}
