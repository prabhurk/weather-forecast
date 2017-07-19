/**
 * 
 */
package com.predict.simple.weather.main;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * @author user
 *
 */
@RunWith(Theories.class)
public class TestWeatherCondition {
	
	PredictFunctions predictFunctions;

	@Before
	public void initObject(){
		predictFunctions = new PredictFunctions();
	}
	
	@DataPoints
	public static List<Object[]> candidates() {
		List<Object[]> list = new ArrayList<Object[]>();
        list.add(new Object[] {-4.0, 1001.0, 82.0, "Snow"});
        list.add(new Object[] {0.0, 1010.0, 82.0, "Rain"});
        list.add(new Object[] {44.0, 998.0, 45.0, "Sunny"});
        return list;
	}
	
	/**
	 * Test method for {@link com.predict.simple.weather.main.PredictFunctions#getWeatherCondition(double, double, double)}.
	 */
	@Test
	@Theory
    public void testWeatherCondition(Object[] candidate) {
          assertEquals(candidate[3], predictFunctions.getWeatherCondition((Double) candidate[0], (Double) candidate[1], (Double) candidate[2]));
    }
	
	@After
	public void nullifyObject(){
		predictFunctions = null;
	}
}
