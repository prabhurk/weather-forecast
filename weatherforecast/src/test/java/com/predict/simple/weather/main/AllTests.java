/**
 * @author Prabhu R K
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.predict.simple.weather.util.TestUtils;

@RunWith(Suite.class)
@SuiteClasses({ 
	TestInputData.class, 
	TestPredictWeatherParams.class, 
	TestWeatherCondition.class,
	TestFileOperations.class,
	TestUtils.class
})
public class AllTests {

}
