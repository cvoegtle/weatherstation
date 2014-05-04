function WeatherArea() {
  this.labelLocation = document.getElementById("location");
  this.labelTime = document.getElementById("time");
  this.labelProblem = document.getElementById("problem");
  this.labelTemperature = document.getElementById("temperature");
  this.labelHumidity = document.getElementById("humidity");
  this.labelWind = document.getElementById("wind");
  this.labelRain = document.getElementById("rain");
  this.labelRainText = document.getElementById("rainText");
}

WeatherArea.prototype.update = function (weatherData) {
  this.updateLabel(this.labelTime, getTimeFractionAsString(weatherData.timestamp));
  this.updateLabel(this.labelTemperature, weatherData.temperature + " °C");
  this.updateLabel(this.labelHumidity, weatherData.humidity + " %");
  this.updateLabel(this.labelWind, weatherData.wind + " km/h");
  if (weatherData.rain_today != "") {
    this.updateLabel(this.labelRainText, "Regen:");
    this.updateLabel(this.labelRain, this.combineRainValues(weatherData.rain, weatherData.rain_today));
  }
};

WeatherArea.prototype.setup = function (weatherData) {
  this.updateLabel(this.labelLocation, weatherData.location);
  this.update(weatherData);
};

WeatherArea.prototype.reportProblem = function(problem) {
  this.updateLabel(this.labelProblem, problem);
};

WeatherArea.prototype.clear = function () {
  for (var label in this) {
    if (this.hasOwnProperty(label)) {
      var myType = typeof this[label];
      if (myType != 'function' && label != 'labelLocation') {
        this.updateLabel(this[label], "");
      }
    }
  }
};

WeatherArea.prototype.updateLabel = function (label, text) {
  label.innerHTML = text;
};

WeatherArea.prototype.combineRainValues = function (rain, rainToday) {
  var result = getOptionalNumber(rainToday, " l");
  if (rain != "") {
    result += " / " + getOptionalNumber(rain, " l");
  }
  return result;
};

function init() {
  var weatherArea = new WeatherArea();

  function updateWeatherArea(weatherData) {
    weatherArea.clear();
    weatherArea.update(weatherData);
  }

  function initializeWeatherArea(weatherData) {
    document.title = "Wetter in " + weatherData.location;
    weatherArea.setup(weatherData);
  }

  function reportConnectionProblem() {
    weatherArea.reportProblem("Verbindung gestört");
  }

  fetchWeatherData(initializeWeatherArea, reportConnectionProblem, "current");
  window.setInterval(fetchWeatherData, 3 * 60 * 1000, updateWeatherArea, reportConnectionProblem, "current");
}

