/**
 * 
 */
package com.predict.simple.weather.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.predict.simple.weather.model.LatLong;
import com.predict.simple.weather.model.Location;
import com.predict.simple.weather.model.WeatherOutput;
import com.predict.simple.weather.model.WeatherVariables;
import com.predict.simple.weather.util.Constants;

/**
 * @author Prabhu R K
 *
 */
public class TestFileOperations {
	
	private FileOperations fileOperations;
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testFolder.create();
		fileOperations = new FileOperations();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		testFolder.delete();
		fileOperations = null;
	}

	/**
	 * Test method for {@link com.predict.simple.weather.main.FileOperations#urlToFile(java.lang.String, java.io.File)}.
	 * @throws IOException 
	 */
	@Test
	public void testUrlToFile() throws IOException {
		File tempFile = testFolder.newFile("IDCJDW2124.201707.csv");
		assertTrue(FileOperations.urlToFile("http://www.bom.gov.au/climate/dwo/201707/text/IDCJDW2124.201707.csv", tempFile));
	}

	/**
	 * Test method for {@link com.predict.simple.weather.main.FileOperations#writeForeCastResultToFile(com.predict.simple.weather.model.WeatherOutput, java.lang.String, int)}.
	 * @throws IOException 
	 */
	@Test
	public void testWriteForeCastResultToFile() throws IOException {
		String locationName = "Sydney";
		String iataCode = "IDCJDW2124";
		Double latitude = -33.8688197;
		Double longitude = 151.2092955;
		Double elevation = 24.5399284362793;
		String timeZoneId = "Australia/Sydney";
		DateTime exeDateInLocTimeZone = new DateTime(2017, 7, 1, 13, 0, 0, 0, DateTimeZone.forID(timeZoneId));
		double[] temperature = {16.8935,-2.2252,17.7012,-1.2477,18.1986,-3.8203,18.6312,-1.3916,-9.7377,22.3474,-5.2052,21.6241,-2.7121,21.1444};
		double[] pressure = {1014.21,1013.14,1014.29,1012.07,1014.44,1014.42,1014.40,1019.01,1017.68,1014.32,1016.73,1014.41,1016.03,1014.38};
		double[] relativeHumidity = {55.0,69.1,55.3,37.9,52.4,69.1,50.2,41.4,67.1,40.2,37.1,43.4,16.0,44.8};
		
		LatLong latLong = new LatLong();
		latLong.setLatitude(latitude);
		latLong.setLongitude(longitude);
		Location location = new Location();
		location.setName(locationName);
		location.setIataCode(iataCode);
		location.setElevation(elevation);
		location.setTimeZoneId(timeZoneId);
		location.setLatLong(latLong);
		WeatherVariables weatherVariables = new WeatherVariables();
		weatherVariables.setTemperature(temperature);
		weatherVariables.setPressure(pressure);
		weatherVariables.setRelativeHumidity(relativeHumidity);
		WeatherOutput weatherOutput = new WeatherOutput();
		weatherOutput.setDateTime(exeDateInLocTimeZone);
		weatherOutput.setLocation(location);
		weatherOutput.setWeatherVariables(weatherVariables);
		
		String folderName = testFolder.getRoot().getPath();
		String fileName = iataCode + Constants.OUTPUT_FILEEXTENSION;
		assertTrue(fileOperations.writeForeCastResultToFile(weatherOutput, folderName, 14));
		
		File outputFile = new File(folderName + "\\" + fileName);
		assertTrue(outputFile.exists());
		
		String outputFileContents = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
		String[] outputLine = outputFileContents.split(Constants.NEW_LINE);
		assertEquals("Sydney|-33.87,151.21,25|2017-07-05T23:00:00Z|Snow|-9.7|1017.7|67", outputLine[8]);
		assertEquals("Sydney|-33.87,151.21,25|2017-07-08T05:00:00Z|Sunny|+21.1|1014.4|45", outputLine[13]);
	}

	/**
	 * Test method for {@link com.predict.simple.weather.main.FileOperations#mergeLocOutputForecastFiles(java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testMergeLocOutputForecastFiles() throws IOException {
		Charset charset = StandardCharsets.UTF_8;
		File file1 = testFolder.newFile("file1" + Constants.OUTPUT_FILEEXTENSION);
		File file2 = testFolder.newFile("file2" + Constants.OUTPUT_FILEEXTENSION);
		String path = file1.getParent();
		String mergeFileName = path + "file3" + Constants.OUTPUT_FILEEXTENSION;
		FileUtils.write(file1, "ABC", charset, false);
		FileUtils.write(file2, "XYZ", charset, false);
		assertTrue(fileOperations.mergeLocOutputForecastFiles(path, mergeFileName));
		assertEquals("ABCXYZ", FileUtils.readFileToString(new File(mergeFileName), charset));
	}
}
