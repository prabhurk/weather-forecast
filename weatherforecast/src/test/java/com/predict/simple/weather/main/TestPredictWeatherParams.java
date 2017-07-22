/**
 * 
 */
package com.predict.simple.weather.main;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Prabhu R K
 *
 */
public class TestPredictWeatherParams {
	
	private PredictFunctions predictFunctions;

	@Before
	public void initObject(){
		predictFunctions = new PredictFunctions();
	}
	
	/**
	 * Test method for {@link com.predict.simple.weather.main.PredictFunctions#predictWeatherParams(double[], int)}.
	 */
	@Test
	public void testPredictWeatherParams() {
		double[] inputData = new double[] {5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5};
		int forecastSize = 2;
		assertTrue(Arrays.equals(new double[] {5,5}, predictFunctions.predictWeatherParams(inputData, forecastSize)));
	}

	@After
	public void nullifyObject(){
		predictFunctions = null;
	}
}
