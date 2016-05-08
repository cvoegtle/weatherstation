/// <reference path='services.ts'/>
var Column = (function () {
    function Column() {
    }
    return Column;
})();
var Columns = (function () {
    function Columns() {
    }
    return Columns;
})();
var WorkingArea = (function () {
    function WorkingArea() {
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
    }
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
        }
        else {
            errorLine = WorkingArea.createErrorLine(problemDescription);
            errorLine.id = "errorLine";
            WorkingArea.prependChild(this.area, errorLine);
        }
    };
    WorkingArea.prototype.updateColumns = function () {
        for (var i in this.columns) {
            if (this.columns.hasOwnProperty(i)) {
                this.columns[i].visible = false;
                this.columns[i].value = [];
            }
        }
        for (var i in this.weatherData) {
            if (this.weatherData.hasOwnProperty(i)) {
                this.columns.timestamp.value[i] = getTimeFractionAsString(this.weatherData[i].timestamp);
                this.columns.location.value[i] = new WeatherLocation(this.weatherData[i]);
                this.columns.temperature.value[i] = getOptionalNumber(this.weatherData[i].temperature, "°C");
                this.columns.humidity.value[i] = this.weatherData[i].humidity + " %";
                this.columns.rain.value[i] = getOptionalNumber(this.weatherData[i].rain, "l");
                this.columns.rain_today.value[i] = getOptionalNumber(this.weatherData[i].rain_today, "l");
            }
        }
        for (var i in this.columns) {
            if (this.columns.hasOwnProperty(i)) {
                this.columns[i].visible = WorkingArea.detectFilledColumn(this.columns[i].value);
            }
        }
    };
    WorkingArea.detectFilledColumn = function (value) {
        var filled = false;
        for (var i in value) {
            if (value.hasOwnProperty(i)) {
                if (value[i] !== "") {
                    filled = true;
                }
                else {
                    value[i] = "&nbsp;";
                }
            }
        }
        return filled;
    };
    WorkingArea.createErrorLine = function (errorMessage) {
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
                var element = WorkingArea.createWeatherElement(this.columns[i].caption, this.columns[i].style);
                WorkingArea.appendChild(caption, this.columns[i].visible, element);
            }
        }
        return caption;
    };
    WorkingArea.createWeatherElement = function (text, style) {
        var element = document.createElement("div");
        element.className = "column " + style;
        element.innerHTML = text;
        return element;
    };
    WorkingArea.prototype.createRow = function (rowNumber) {
        var row = document.createElement("div");
        row.className = "row";
        for (var i in this.columns) {
            if (this.columns.hasOwnProperty(i)) {
                var element = this.createWeatherSpan(this.columns[i].value[rowNumber], this.columns[i].style);
                WorkingArea.appendChild(row, this.columns[i].visible, element);
            }
        }
        return row;
    };
    WorkingArea.appendChild = function (parent, visible, child) {
        if (visible) {
            parent.appendChild(child);
        }
    };
    WorkingArea.prependChild = function (parent, child) {
        parent.insertBefore(child, parent.firstChild);
    };
    WorkingArea.prototype.createWeatherSpan = function (value, style) {
        var element = document.createElement("div");
        element.className = "column " + style;
        var span = document.createElement("span");
        span.className = "value";
        if (value instanceof WeatherLocation) {
            var link = document.createElement("a");
            link.onclick = function () {
                window.open(value.forecast, "_blank");
            };
            link.innerHTML = value.description;
            element.appendChild(link);
        }
        else {
            span.innerHTML = value;
        }
        element.appendChild(span);
        return element;
    };
    return WorkingArea;
})();
var WeatherLocation = (function () {
    function WeatherLocation(weatherDataset) {
        this.description = weatherDataset.location;
        this.forecast = weatherDataset.forecast;
    }
    return WeatherLocation;
})();
function init() {
    var workingArea = new WorkingArea();
    function updateWeatherView(weatherData) {
        workingArea.clear();
        workingArea.update(weatherData);
    }
    function reportConnectionProblem() {
        workingArea.reportProblem("Verbindung gestört");
    }
    var urlParam = window.location.search.substring(1);
    fetchAllWeatherData(updateWeatherView, reportConnectionProblem, urlParam);
    window.setInterval(fetchAllWeatherData, 3 * 60 * 1000, updateWeatherView, reportConnectionProblem, urlParam);
}
//# sourceMappingURL=allstations.js.map