function WeatherArea() {
  this.labelLocation = document.getElementById("location");
  this.labelTime = document.getElementById("time");
  this.labelProblem = document.getElementById("problem");
  this.labelTemperature = document.getElementById("temperature");
  this.labelHumidity = document.getElementById("humidity");
  this.labelWind = document.getElementById("wind");
  this.labelRain = document.getElementById("rain");
  this.labelRainText = document.getElementById("rainText");
  this.labelWattText = document.getElementById("wattText");
  this.labelWatt = document.getElementById("watt");

  this.rainInfoButton = this.createMoreInfoButton();
}

WeatherArea.prototype.update = function (weatherData) {
  this.updateLabel(this.labelTime, getTimeFractionAsString(weatherData.timestamp));
  this.updateLabel(this.labelTemperature, getOptionalNumber(weatherData.temperature, "°C"));
  this.updateLabel(this.labelHumidity, weatherData.humidity + " %");
  this.updateLabel(this.labelWind, getOptionalNumber(weatherData.wind , "km/h"));
  this.updateLabel(this.labelRainText, "Regen:");
  if (weatherData.rain_today != "") {
    this.updateLabel(this.labelRain, this.combineRainValues(weatherData.rain, weatherData.rain_today));
  }
  this.labelRain.appendChild(this.rainInfoButton);

  if (this.labelWatt !== undefined) {
    this.updateLabel(this.labelWattText, "");
    this.updateLabel(this.labelWatt, "");
    if (weatherData.hasOwnProperty("watt") && weatherData.watt > 0) {
      this.updateLabel(this.labelWattText, "Leistung:");
      this.updateLabel(this.labelWatt, getOptionalNumber(weatherData.watt, "W"));
    }
  }
};

WeatherArea.prototype.setup = function (weatherData) {
  this.updateLabel(this.labelLocation, weatherData.location);
  this.update(weatherData);
};

WeatherArea.prototype.reportProblem = function (problem) {
  this.updateLabel(this.labelProblem, problem);
};

WeatherArea.prototype.clear = function () {
  for (var label in this) {
    if (this.hasOwnProperty(label)) {
      var myType = typeof this[label];
      if (myType != 'function' && label != 'labelLocation' && label != 'rainInfoButton') {
        this.updateLabel(this[label], "");
      }
    }
  }
};

WeatherArea.prototype.updateLabel = function (label, text) {
  if (label != null) {
    label.innerHTML = text;
  }
};

WeatherArea.prototype.combineRainValues = function (rain, rainToday) {
  var result = "";

  if (rainToday != "") {
    result = getOptionalNumber(rainToday, " l");
    if (rain != "") {
      result += " / " + getOptionalNumber(rain, " l");
    }
  }
  return result;
};

WeatherArea.prototype.createMoreInfoButton = function () {
  var button = document.createElement("button");

  button.innerHTML = "?";
  button.className = "rain_button";
  button.id = "rain_button";
  button.onclick = function () {
    this.disabled = true;
    showMoreRain();
  };

  return button;
};

function RainArea() {
  this.div = document.createElement("div");
  this.div.className = "rain_div hidden";
  this.div.id = "rain_div";
  this.div.onclick = function () {
    var root = document.getElementById("content");
    root.removeChild(this);
    var rainInfoButton = document.getElementById("rain_button");
    rainInfoButton.disabled = false;
  };

  var caption = document.createElement("div");
  caption.className = "rain_caption";
  caption.innerHTML = "Regen";
  this.div.appendChild(caption);

  var root = document.getElementById("content");
  root.appendChild(this.div);

  window.setTimeout(makeVisible, 20, this.div);

}

RainArea.prototype.setup = function (rainData) {
  this.addRow("letzte Stunde:", rainData.lastHour);
  this.addRow("heute:", rainData.today);
  this.addRow("gestern:", rainData.yesterday);
  this.addRow("letzte Woche:", rainData.lastWeek);
  this.addRow("letzte 30 Tage:", rainData.last30days);
};

RainArea.prototype.addRow = function (labelText, value) {
  var labelDiv = document.createElement("div");
  labelDiv.className = "rain_label";
  labelDiv.innerHTML = labelText;
  this.div.appendChild(labelDiv);


  var valueDiv = document.createElement("div");
  valueDiv.className = "rain_value";
  valueDiv.innerHTML = getOptionalNumber(value, " l");
  this.div.appendChild(valueDiv);
};

function makeVisible (div) {
  div.className = "rain_div transition";
}

function showMoreRain() {
  var rainArea = new RainArea();

  function showRain(rainData) {
    rainArea.setup(rainData);
  }

  fetchWeatherData(showRain, null, "rain");

}



function repairinit() {
  init();

  var today = new Date();

  var inputStart = document.getElementById("starttime");
  var inputValue = today.getFullYear() + "-" + (today.getMonth()+1) + "-" + today.getDate() + "-00:00:00";
  inputStart.value= inputValue;
  inputStart.setSelectionRange(0, inputValue.length);
  inputStart.focus();
}

function onKeyDownOnSecret(event) {
  if (event.keyCode === 13) {
    repairData();
  }
}

function repairData() {
  var inputStart = document.getElementById("starttime");
  var location = "/weatherstation/repair?begin=" + inputStart.value;

  var endinput = document.getElementById("endtime");
  var endValue = endinput.value;
  if (endValue !== "") {
    location += "&end="+endValue;
  }
  var inputSecret = document.getElementById("secret");
  var secretValue = inputSecret.value;
  if (secretValue !== "") {
    location += "&secret=" +secretValue;
  }

  window.location.href=location;
}

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

