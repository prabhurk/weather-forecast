# Weather-Forecast

Weather Forecast predicts weather corresponds to location based on past data

## How the program works

* Past weather data is based on [Bureau of Meteorology, Commonwealth of Australia](http://www.bom.gov.au/climate/dwo/)
* Name of the location along with its code for [BoM, Australian Government](http://www.bom.gov.au/climate/dwo/) has to be provided by the user
* Location parameters (latitude, longtitude, elevation, timezone) are based on Google API
* Prediction of weather parameters (temperature, pressure, relative humidity) are based on [ARIMA Model](https://github.com/Workday/timeseries-forecast)
* Weather data predicted at local time is expressed in [UTC timezone](https://en.wikipedia.org/wiki/Coordinated_Universal_Time) in output
