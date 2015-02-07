var WorkingArea = function () {
  this.area = document.getElementById("working_area");

  this.columns = {
    timestamp: {
      caption: "Zeit",
      style: "time",
      visible: false,
      value: []
    },
    location: {
      caption: "Ort",
      style: "location",
      visible: false,
      value: []
    },
    temperature: {
      caption: "Temp.",
      style: "temperature",
      visible: false,
      value: []
    },
    humidity: {
      caption: "Luftf.",
      style: "humidity",
      visible: false,
      value: []
    },
    rain: {
      caption: "Regen 1h",
      style: "rain",
      visible: false,
      value: []
    },
    rain_today: {
      caption: "Regen",
      style: "rain_today",
      visible: false,
      value: []
    }
  };

  this.weatherData = [];

};

WorkingArea.prototype.update = function (newData) {
  this.weatherData = newData;
  this.updateColumns();
  this.area.appendChild(this.createCaption());
  for (var row = 0; row < this.weatherData.length; row++) {
    this.area.appendChild(this.createRow(row));
  }
};

WorkingArea.prototype.clear = function () {
  while (this.area.hasChildNodes()) {
    this.area.removeChild(this.area.lastChild);
  }
};

WorkingArea.prototype.reportProblem = function (problemDescription) {
  var errorLine = document.getElementById("errorLine");
  if (errorLine != undefined) {
    errorLine.innerHTML = problemDescription;
  } else {
    errorLine = this.createErrorLine(problemDescription);
    errorLine.id = "errorLine";
    this.prependChild(this.area, errorLine);
  }
};

WorkingArea.prototype.updateColumns = function () {
  for (var i in this.columns) {
    if (this.columns.hasOwnProperty(i)) {
      this.columns[i].visible = false;
      this.columns[i].value = [];
    }
  }

  for (i in this.weatherData) {
    if (this.weatherData.hasOwnProperty(i)) {
      this.columns.timestamp.value[i] = getTimeFractionAsString(this.weatherData[i].timestamp);
      this.columns.location.value[i] = new Location(this.weatherData[i]);
      this.columns.temperature.value[i] = getOptionalNumber(this.weatherData[i].temperature, "°C");
      this.columns.humidity.value[i] = this.weatherData[i].humidity + " %";
      this.columns.rain.value[i] = getOptionalNumber(this.weatherData[i].rain, "l");
      this.columns.rain_today.value[i] = getOptionalNumber(this.weatherData[i].rain_today, "l");
    }
  }

  for (i in this.columns) {
    if (this.columns.hasOwnProperty(i)) {
      this.columns[i].visible = this.detectFilledColumn(this.columns[i].value);
    }
  }
};

WorkingArea.prototype.detectFilledColumn = function (value) {
  var filled = false;
  for (var i in value) {
    if (value.hasOwnProperty(i)) {
      if (value[i] !== "") {
        filled = true;
      } else {
        value[i] = "&nbsp;"
      }
    }
  }
  return filled;
};

WorkingArea.prototype.createErrorLine = function (errorMessage) {
  var errorLine = document.createElement("div");
  errorLine.className = "error";
  errorLine.id = "errorLine";
  errorLine.innerHTML = errorMessage;
  return errorLine;
};

WorkingArea.prototype.createCaption = function () {
  var caption = document.createElement("div");
  caption.className = "caption";

  for (var i in this.columns) {
    if (this.columns.hasOwnProperty(i)) {
      var element = this.createWeatherElement(this.columns[i].caption, this.columns[i].style);
      this.appendChild(caption, this.columns[i].visible, element);
    }
  }

  return caption;
};

WorkingArea.prototype.createRow = function (rowNumber) {
  var row = document.createElement("div");
  row.className = "row";

  for (var i in this.columns) {
    if (this.columns.hasOwnProperty(i)) {
      var element = this.createWeatherSpan(this.columns[i].value[rowNumber], this.columns[i].style);
      this.appendChild(row, this.columns[i].visible, element);
    }
  }
  return row;
};


WorkingArea.prototype.appendChild = function (parent, visible, child) {
  if (visible) {
    parent.appendChild(child);
  }
};

WorkingArea.prototype.prependChild = function (parent, child) {
  parent.insertBefore(child, parent.firstChild);
};

WorkingArea.prototype.createWeatherElement = function (text, style) {
  var element = document.createElement("div");
  element.className = "column " + style;
  element.innerHTML = text;
  return element;
};

WorkingArea.prototype.createWeatherSpan = function (value, style) {
  var element = document.createElement("div");
  element.className = "column " + style;

  var span = document.createElement("span");
  span.className = "value";
  if (value instanceof Location) {
    var link = document.createElement("a");
    link.onclick = function () {
      window.open(value.forecast, "_blank");
    };
    link.innerHTML = value.description;
    element.appendChild(link);
  } else {
    span.innerHTML = value;
  }

  element.appendChild(span);
  return element;
};

var Location = function (weatherDataset) {
  this.description = weatherDataset.location;
  this.forecast = weatherDataset.forecast;
};


function init() {
  function updateWeatherView(weatherData) {
    workingArea.clear();
    workingArea.update(weatherData);
  }

  function reportConnectionProblem() {
    workingArea.reportProblem("Verbindung gestört");
  }

  var workingArea = new WorkingArea();

  fetchAllWeatherData(updateWeatherView, reportConnectionProblem, "all");
  window.setInterval(fetchAllWeatherData, 3 * 60 * 1000, updateWeatherView, reportConnectionProblem, "all");
}

