/**
 * @author Prabhu R K
 * Copyright (c) 2017, Prabhu R K. All rights reserved.
 */
package com.predict.simple.weather.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.predict.simple.weather.model.LatLong;
import com.predict.simple.weather.util.Constants;

public class TestInputData {
	
	private InputData inputData;
	private static File file;
	private static File tempRootFolder;
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		file = new File(new TestInputData().getClass().getClassLoader().getResource(Constants.INPUT_LOCJSON).getFile());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		file = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		inputData = new InputData();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		inputData = null;
		tempRootFolder = null;
	}
	
	
	/**
	 * Test method for {@link com.predict.simple.weather.main.InputData#getLatLongFromAPI(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCheckInputJsonAvailable() {
		assertTrue(file.exists());
		assertTrue(file.isFile());
	}
	

	/**
	 * Test method for {@link com.predict.simple.weather.main.InputData#getLatLongFromAPI(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetLatLongFromAPI() throws IOException {
		tempRootFolder = testFolder.newFolder("weatherforescat_tmp");
		LatLong expectedLatLong = new LatLong();
		expectedLatLong.setLatitude(-33.8688197);
		expectedLatLong.setLongitude(151.2092955);
		LatLong actualLatLong = inputData.getLatLongFromAPI("Sydney", "IDCJDW2124", tempRootFolder.getAbsolutePath());
		assertEquals(expectedLatLong.toString(), actualLatLong.toString());
	}

	/**
	 * Test method for {@link com.predict.simple.weather.main.InputData#getElevationFromLatLong(com.predict.simple.weather.model.LatLong, java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetElevationFromLatLong() throws IOException {
		tempRootFolder = testFolder.newFolder("weatherforescat_tmp");
		LatLong latLong = new LatLong();
		latLong.setLatitude(-33.8688197);
		latLong.setLongitude(151.2092955);
		Double expectedElevation = 24.5399284362793;
		Double actualElevation = inputData.getElevationFromLatLong(latLong, "IDCJDW2124", tempRootFolder.getAbsolutePath());
		assertEquals(expectedElevation, actualElevation);
	}

	/**
	 * Test method for {@link com.predict.simple.weather.main.InputData#getTimeZoneFromLatLong(com.predict.simple.weather.model.LatLong, java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetTimeZoneFromLatLong() throws IOException {
		tempRootFolder = testFolder.newFolder("weatherforescat_tmp");
		LatLong latLong = new LatLong();
		latLong.setLatitude(-33.8688197);
		latLong.setLongitude(151.2092955);
		String expectedTimeZone = "Australia/Sydney";
		String actualTimeZone = inputData.getTimeZoneFromLatLong(latLong, "IDCJDW2124", tempRootFolder.getAbsolutePath());
		assertEquals(expectedTimeZone, actualTimeZone);
	}

}
