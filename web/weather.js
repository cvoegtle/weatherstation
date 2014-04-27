
var testdaten = [

    {
        "timestamp": "Fri Apr 18 20:28:11 UTC 2014",
        "wind": 0,
        "humidity": 85,
        "raining": false,
        "location": "Paderborn",
        "rain_today": 5.015,
        "rain": 2.065,
        "temperature": 5.3
    },
    {
        "timestamp": "Fri Apr 18 20:28:50 UTC 2014",
        "wind": 0,
        "humidity": 76,
        "raining": false,
        "location": "Bonn",
        "rain_today": 1.475,
        "rain": "",
        "temperature": 8.6
    },
    {
        "timestamp": "Fri Apr 18 20:29:39 UTC 2014",
        "wind": 0,
        "humidity": 84,
        "raining": false,
        "location": "Freiburg",
        "rain_today": 2.065,
        "rain": "",
        "temperature": 4.6
    }

];

function getTimeFractionAsString(dateAsString) {
    var date = new Date(dateAsString);
    var hours = date.getHours();
    var minutes = date.getMinutes();

    return hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
}



var WorkingArea = function() {
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
            caption: "Temperatur",
            style: "temperature",
            visible: false,
            value: []
        },
        humidity: {
            caption: "Luftfeuchtigkeit",
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
            caption: "Regen heute",
            style: "rain_today",
            visible: false,
            value: []
        }
    };

    this.weatherData =  [];

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

WorkingArea.prototype.updateColumns = function () {
    for (var i in this.columns) {
        this.columns[i].visible = false;
        this.columns[i].value = [];
    }

    for (i in this.weatherData) {
        this.columns.timestamp.value[i] = getTimeFractionAsString(this.weatherData[i].timestamp);
        this.columns.location.value[i] = new Location(this.weatherData[i]);
        this.columns.temperature.value[i] = this.weatherData[i].temperature + " °C";
        this.columns.humidity.value[i] = this.weatherData[i].humidity + " %";
        this.columns.rain.value[i] = this.getOptionalNumber(this.weatherData[i].rain, " l");
        this.columns.rain_today.value[i] = this.getOptionalNumber(this.weatherData[i].rain_today, " l");
    }

    for (i in this.columns) {
        this.columns[i].visible = this.detectFilledColumn(this.columns[i].value);
    }
};

WorkingArea.prototype.detectFilledColumn = function (value) {
    var filled = false;
    for (var i in value) {
        if (value[i] !== "") {
            filled = true;
        } else {
            value[i] = "&nbsp;"
        }
    }
    return filled;
};

WorkingArea.prototype.getOptionalNumber = function (value, unit) {
    if (value !== "") {
        return value.toFixed(1) + unit;
    } else {
        return "";
    }
};

WorkingArea.prototype.createCaption = function () {
    var caption = document.createElement("div");
    caption.className = "caption";

    for (var i in this.columns) {
        var element = this.createWeatherElement(this.columns[i].caption, this.columns[i].style);
        this.appendChild(caption, this.columns[i].visible, element);
    }

    return caption;
};

WorkingArea.prototype.createRow = function (rowNumber) {
    var row = document.createElement("div");
    row.className = "row";

    for (var i in this.columns) {
        var element = this.createWeatherSpan(this.columns[i].value[rowNumber], this.columns[i].style);
        this.appendChild(row, this.columns[i].visible, element);
    }
    return row;
};


WorkingArea.prototype.appendChild = function (parent, visible, child) {
    if (visible) {
        parent.appendChild(child);
    }
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
        link.onclick =  function() {
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

function handleClickOnLink() {

}

function updateWeatherView(weatherData) {
    workingArea.clear();
    workingArea.update(weatherData);
}

function fetchWeatherData() {
    var ajaxRequest = new XMLHttpRequest();
    ajaxRequest.onload = function () {
        var weatherData = JSON.parse(ajaxRequest.responseText);
        updateWeatherView(weatherData);


    };
    ajaxRequest.open("get", "/weatherstation/query?type=all&ext", true);
    ajaxRequest.send();

//    updateWeatherView(testdaten);

};

var Location = function(weatherDataset) {
    this.description = weatherDataset.location;
    this.forecast = weatherDataset.forecast;
}


function init() {
    workingArea = new WorkingArea();

    fetchWeatherData();
    window.setInterval(fetchWeatherData, 3 * 60 * 1000);
};

