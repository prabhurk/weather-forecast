# Weather-Forecast

Weather Forecast predicts weather corresponds to location based on past data

Supported locations (default): [Sydney](http://www.bom.gov.au/climate/dwo/IDCJDW2124.latest.shtml), [Canberra](http://www.bom.gov.au/climate/dwo/IDCJDW2801.latest.shtml), [Melbourne](http://www.bom.gov.au/climate/dwo/IDCJDW3033.latest.shtml), [Brisbane](http://www.bom.gov.au/climate/dwo/IDCJDW4019.latest.shtml), [Gold Coast](http://www.bom.gov.au/climate/dwo/IDCJDW4050.latest.shtml), [Adelaide](http://www.bom.gov.au/climate/dwo/IDCJDW5002.latest.shtml), [Perth](http://www.bom.gov.au/climate/dwo/IDCJDW6111.latest.shtml), [Townsville](http://www.bom.gov.au/climate/dwo/IDCJDW4128.latest.shtml), [Darwin](http://www.bom.gov.au/climate/dwo/IDCJDW8014.latest.shtml) & [Casey Antarctica](http://www.bom.gov.au/climate/dwo/IDCJDW9203.latest.shtml)

Supported timeframe: At 9am and 3pm, each day for a week, starting from the very next day of program execution (time based on local time of the corresponding location)

> As far as valid past weather data is available in [Bureau of Meteorology, Commonwealth of Australia](http://www.bom.gov.au/climate/dwo/), the locations can be customized in [_input_locations.json_](weatherforecast/target/input_locations.json)

## Build

### Prerequisites

* [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 or higher ([```JAVA_HOME```](https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/) and [```PATH```](https://en.wikipedia.org/wiki/PATH_(variable)) set) for compile and execution
* [Apache Maven](https://maven.apache.org/download.cgi) 3.3 or higher ([```MVN_HOME```](https://maven.apache.org/install.html) and [```PATH```](https://en.wikipedia.org/wiki/PATH_(variable)) set) for build

> Connection to Internet is required if past weather data is to be downloaded

### Installation

Goto the project base directory, [_weatherforecast_](weatherforecast/), the directory where [_pom.xml_](weatherforecast/pom.xml) for [_weather-forecast_](weatherforecast) is present. 

Execute the command:

<pre>
<b>mvn clean install</b>
</pre>


The following required outputs (*there will be other stuffs as well, along with the required*) will be generated in the [_target_](weatherforecast/target) directory:

* [_weatherdataforecast-jar-with-dependencies.jar_](weatherforecast/target/weatherdataforecast-jar-with-dependencies.jar)
* [_input_locations.json_](weatherforecast/target/input_locations.json)
* [_input_](weatherforecast/target/input)

>Before executing the command, make sure to close all opened files from [_target_](weatherforecast/target) directory. Also exit from [_target_](weatherforecast/target) if command prompt or shell is presently locked in

>If any issues in junit testcases or if you want to simply by-pass test execution, you can always skip the unit testcases by executing below mentioned command:
>
><pre>
><b>mvn clean install -Dmaven.test.skip=true</b>
></pre>

## Configuration / Input

### input_locations.json

The [_input_locations.json_](weatherforecast/target/input_locations.json) file contains list of locations, with name and respective code, for which the weather prediction is required. The [_input_locations.json_](weatherforecast/target/input_locations.json) file has to be in the same location as the executable, [_weatherdataforecast-jar-with-dependencies.jar_](weatherforecast/target/weatherdataforecast-jar-with-dependencies.jar)

>The code for each location can be referred from [Bureau of Meteorology, Commonwealth of Australia](http://www.bom.gov.au/climate/dwo/) and is required for downloading past weather data for locations

### input

The [_input_](weatherforecast/target/input) directory contains necessary location parameters in the form of JSON files, prefixed by the location code given in [_input_locations.json_](weatherforecast/target/input_locations.json). The [_input_](weatherforecast/target/input) directory has to be in the same location as the executable, [_weatherdataforecast-jar-with-dependencies.jar_](weatherforecast/target/weatherdataforecast-jar-with-dependencies.jar)

* [**`latlong.json`**](weatherforecast/target/input) is the [latitude](https://en.wikipedia.org/wiki/Latitude) & [longitude](https://en.wikipedia.org/wiki/Longitude) of location obtained from [Google Geocoding API](https://developers.google.com/maps/documentation/geocoding/start) and is based on location name given in [_input_locations.json_](weatherforecast/target/input_locations.json)

* [**`elevtn.json`**](weatherforecast/target/input) is the [elevation](https://en.wikipedia.org/wiki/Elevation) of location obtained from [Google Elevation API](https://developers.google.com/maps/documentation/elevation/start) and is based on latitude & longitude of the location

* [**`timezn.json`**](weatherforecast/target/input) is the [timezone id](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones) of location obtained from [Google TimeZone API](https://developers.google.com/maps/documentation/timezone/start) and is based on latitude & longitude of the location

* [**`data`**](weatherforecast/target/input/data) directory is a collection of past weather data for each location, demarcated by the overlying directory with name as the code of the location. The data is obtained from [Bureau of Meteorology, Commonwealth of Australia](http://www.bom.gov.au/climate/dwo/) and is based on code. Presently, data upto 14 months prior to now can be downloaded


>The program should be able to download the whole [_input_](weatherforecast/target/input) through internet connection. But the APIs have usage limits, hence recommended to use it directly downloading from [here](weatherforecast/target/input)

>During the _clean_ stage of _build_, in case [_input_](weatherforecast/target/input) gets deleted from [_target_](weatherforecast/target) directory, download [_input_](weatherforecast/target/input) from [here](weatherforecast/src/main/resources/input)

## Execution

The program execution requires all three _build_ outputs ([_weatherdataforecast-jar-with-dependencies.jar_](weatherforecast/target/weatherdataforecast-jar-with-dependencies.jar), [_input_locations.json_](weatherforecast/target/input_locations.json), [_input_](weatherforecast/target/input)) to be in the same location, hence can be run from either:

1. from [_target_](weatherforecast/target) directory, right after the _build_ or
2. copy the three artifacts to a common location and run from there


The following command is to be used for the execution of the program:

<pre>
<b>java -jar weatherdataforecast-jar-with-dependencies.jar</b>
</pre>

## Forecast Result

After execution of the program, result, the forecast weather data, will be generated in a text file in the directory **`output`**. The **`output`** directory is of same location as in the executable, [_weatherdataforecast-jar-with-dependencies.jar_](weatherforecast/target/weatherdataforecast-jar-with-dependencies.jar) is present.

>Previously generated results will not be replaced/removed. However, if you are running the program from [_target_](weatherforecast/target), then on subsequent _clean_ stage of _build_, [_target_](weatherforecast/target) directory will be cleaned up.

The name of weather data output text file will be:

**`Forecast_`** + program execution date in **`yyyy-MM-dd_HH-mm-ss`** format + [**`OUTPUT_FILEEXTENSION`**](weatherforecast/src/main/java/com/predict/simple/weather/util/Constants.java)

>Example weather data output text file name: *`Forecast_2017-06-25_08-00-16.txt`*

Format of data in the file: ```Location|Position|Local Time|Conditions|Temperature|Pressure|Humidity```

where 
* Location is an optional label describing one or more positions,
* Position is a comma-separated triple containing latitude, longitude, and elevation in metres above sea level,
* Local time is an [ISO8601](https://en.wikipedia.org/wiki/ISO_8601) date time,
* Conditions is either Snow, Rain, Sunny,
* Temperature is in °C,
* Pressure is in hPa, and
* Relative humidity is a %

>Example data: *`Brisbane|-27.47,153.03,22|2017-07-28T23:00:00Z|Rain|+24.0|1027.6|80`*

>Presently, the program tries to replicate [Bureau of Meteorology, Commonwealth of Australia](http://www.bom.gov.au/climate/dwo/) by predicting weather at 9am and 3pm of local time of location for a week. Hence the forecast size given in [_FORECAST_SIZE_](weatherforecast/src/main/java/com/predict/simple/weather/util/Constants.java) is 7 X 2 = 14

>_After execution of the program, latest individual locaion-wise weather forecast results will be generated as text file in the directory **`output\tmp`**. The name of the file will be the code of the location. The directory is cleaned upon each forecast execution_

## How the program works

* The location names are the respective codes are to be user-given as [_input_locations.json_](weatherforecast/target/input_locations.json) and are parsed in the program
* For each location from the above list of locations, the below actions are performed:

	* Response from [Google Geocoding API](https://developers.google.com/maps/documentation/geocoding/start) (JSON), triggered based on location name, is written to [latlong.json](weatherforecast/target/input) and is parsed to get latitude & longitude
	* Response from [Google Elevation API](https://developers.google.com/maps/documentation/elevation/start) (JSON), triggered based on the obtained latitude & longitude, is written to [elevtn.json](weatherforecast/target/input) and is parsed to get elevation
	* Response from [Google TimeZone API](https://developers.google.com/maps/documentation/timezone/start) (JSON), triggered based on the obtained latitude & longitude and the current timestamp in default timezone of program execution environment, is written to [timezn.json](weatherforecast/target/input) and is parsed to get timezone id
	
	>
	
	> All three above reponse JSON files are demarcated by prefixing the file name with the code of the location
	
	* Response from [Bureau of Meteorology, Commonwealth of Australia](http://www.bom.gov.au/climate/dwo/) (CSV), triggered based on the location code and timeperiod (in *YYYYMM* format) is downloaded to [data](weatherforecast/target/input/data), demarcated by the overlying directory with the code of the location, and is parsed to get the past weather data
	
	>
	
	> 14 months (from 13 months before to present) of past weather data is downloaded in ascending order of time for better prediction (time is calculated based on location time zone id)

	> If the above files are already present in [input](weatherforecast/target/input), assuming the execution happens from [target](weatherforecast/target), the API requests will not be polled and the required values are just directly parsed from the respective files
	
	* Prediction of weather parameters (temperature, pressure & relative humidity) is performed based on [ARIMA Model](https://github.com/Workday/timeseries-forecast), respective past weather paramters obtained in above step and the required number of [_FORECAST_SIZE_](weatherforecast/src/main/java/com/predict/simple/weather/util/Constants.java)
	
	* Each prediction is tagged alternatively to 9am and 3pm weather forecast of day, starting from the very next day of program execution date (calculated on location time zone id) and is progressively repeated for each next day until the last member of predicted parameters
	
	* Weather condition for each forecast is based on the respective weather parameters
	
	> Weather condition is deemed to be *snow* if the temperature is less than zero. *Rain* if relative humidity is greater than 80. For anything else and by default, the weather condition will be *sunny*
	
	* The results are written to a file, with name as the code of the location, in the directory **output\tmp**
	
	>
	
	> Each result include name of the location, its position (combination of its latitude, longitude & elevation), prediction-tagged time (expressed in [UTC timezone](https://en.wikipedia.org/wiki/Coordinated_Universal_Time)), weather condition, temperature, pressure and relative humidity

	> Obviously, there will be as many results as [_FORECAST_SIZE_](weatherforecast/src/main/java/com/predict/simple/weather/util/Constants.java) (two predictions for each day)
	
	> Before writing the first result file in **output\tmp**, the directory will be cleaned up
	
* Finally, the files present within **output\tmp** (individual locaion-wise predictions), will be merged together to output a single file in **output** directory, file name being suffixed with program execution date in default timezone of the program execution environment

## Author / Contribution

Prabhu R K
 
## Version

0.0.1 - Initial and complete release version

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) for details
