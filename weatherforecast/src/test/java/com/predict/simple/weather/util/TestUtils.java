/**
 * 
 */
package com.predict.simple.weather.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

/**
 * @author user
 *
 */
public class TestUtils {

	/**
	 * Test method for {@link com.predict.simple.weather.util.Utils#formatDecimal(java.lang.Double, java.lang.String)}.
	 */
	@Test
	public void testFormatDecimal() {
		Double number = 74.272223562356;
		String format = "0.0";
	    assertEquals("74.3", Utils.formatDecimal(number, format));
	}

	/**
	 * Test method for {@link com.predict.simple.weather.util.Utils#dateToString(java.util.Date, java.lang.String)}.
	 */
	@Test
	public void testDateToString() {
		Date date = new GregorianCalendar(2011, Calendar.DECEMBER, 17).getTime();
		String format = "YYYY-MM-dd";
	    assertEquals("2011-12-17", Utils.dateToString(date, format));
	}

	/**
	 * Test method for {@link com.predict.simple.weather.util.Utils#dateTimeToString(org.joda.time.DateTime, java.lang.String)}.
	 */
	@Test
	public void testDateTimeToString() {
		DateTimeZone timeZone = DateTimeZone.forID("Asia/Calcutta");
		DateTime dateTime = new DateTime(2011, 12, 17, 7, 30, timeZone);
		String format = "YYYY-MM-dd";
		assertEquals("2011-12-17", Utils.dateTimeToString(dateTime, format));
	}

	/**
	 * Test method for {@link com.predict.simple.weather.util.Utils#dateTimeToUTC(org.joda.time.DateTime)}.
	 */
	@Test
	public void testDateTimeToUTC() {
		DateTimeZone timeZone = DateTimeZone.forID("Asia/Calcutta");
		DateTime dateTime = new DateTime(2011, 12, 17, 7, 30, timeZone);
		assertEquals("2011-12-17T02:00:00Z", Utils.dateTimeToUTC(dateTime));
	}

	/**
	 * Test method for {@link com.predict.simple.weather.util.Utils#linkedListToPrimitiveArray(java.util.List)}.
	 */
	@Test
	public void testLinkedListToPrimitiveArray() {
		List<Double> inputList = new LinkedList<Double>();
		inputList.add(-34.4);
		inputList.add(70.7777);
		inputList.add(0.0);
		assertTrue(Arrays.equals(new double[] {-34.4, 70.7777, 0.0}, Utils.linkedListToPrimitiveArray(inputList)));
	}

	/**
	 * Test method for {@link com.predict.simple.weather.util.Utils#parseToDouble(java.lang.String)}.
	 */
	@Test
	public void testParseToDouble() {
		String input = "123.56E249";
		assertTrue(EqualsBuilder.reflectionEquals(0.0, Utils.parseToDouble(input)));
	}

}
