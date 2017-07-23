/**
 * The Utils Class has generic or commonly used functions
 *
 * @author Prabhu R K
 * @version 0.0.1
 * @since July 18, 2017
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Utils {

	/**
	 * This method formats the given Double number to given format and returns as String
	 * @param Double number, number to be formatted.
	 * @return String numberString,  formatted number in String
	 */
	public static String formatDecimal(Double number, String format) {
		DecimalFormat decimalFormat = new DecimalFormat(format);
		String numberString = decimalFormat.format(number);
		return numberString;
	}
		
	/**
	 * This method converts the given Date object to string based on the given date format
	 * @param Date date, date to be formatted.
	 * @param String dateFormatString, required format of date.
	 * @return String dateString,  formatted date in String
	 */
	public static String dateToString(Date date, String dateFormatString) {
		String dateString = null;
		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		dateString = dateFormat.format(date);
		return dateString;
	}
	
	/**
	 * This method converts the given DateTime object to string based on the given date format
	 * @param DateTime dateTime, DateTime to be formatted.
	 * @param String dateFormatString, required format of dateTime.
	 * @return String dateString,  formatted date in String
	 */
	public static String dateTimeToString(DateTime dateTime, String dateFormatString) {
		String dateString = null;
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateFormatString);
		dateString = dateTimeFormatter.print(dateTime);
		return dateString;
	}
	
	/**
	 * This method converts the given DateTime to string based on the UTC format
	 * @param DateTime dateTime, DateTime to be converted.
	 * @return String dateTimeUTCString,  converted date in String
	 */
	public static String dateTimeToUTC(DateTime dateTime) {
		String dateTimeUTCString = null;
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();  
		dateTimeUTCString = formatter.print(dateTime.toDateTime(DateTimeZone.UTC));
		return dateTimeUTCString;
	}
	
	/**
	 * This method converts the given Exception object to string
	 * @param Exception exception, to be converted to string.
	 * @return String exceptionString,  exception message in string
	 */
	public static String exceptionToString(Exception exception) {
		String exceptionString = null;
//		exceptionString = exception.getMessage();
		exceptionString = ExceptionUtils.getStackTrace(exception);
		return exceptionString;
	}
	
	/**
	 * This method converts the given List of Double object to primitive Array type
	 * @param List<Double> inputList, list to be converted.
	 * @return double[] outputArray,  converted data
	 */
	public static double[] linkedListToPrimitiveArray(List<Double> inputList) {
		double[] outputArray = ArrayUtils.toPrimitive(inputList.toArray(new Double[inputList.size()]));
		return outputArray;
	}
	
	/**
	 * This method checks if the given String object can be parsed to Double and if eligible for parse, parse and give output, else defauls to output 0.0 
	 * @param String input, input data to be checked for/parsed.
	 * @return Double output,  output result
	 */
	public static Double parseToDouble(String input) {
		Double output = 0.0;
		if(NumberUtils.isParsable(input))
			output = Double.parseDouble(input);
		return output;
	}

}
