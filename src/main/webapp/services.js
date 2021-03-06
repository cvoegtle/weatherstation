var WeatherData = /** @class */ (function () {
    function WeatherData() {
    }
    return WeatherData;
}());
var testdaten = [
    {
        timestamp: "Fri Apr 18 20:28:11 UTC 2014",
        localtime: "20:28",
        wind: 0,
        barometer: 1000,
        humidity: 85,
        raining: false,
        location: "Paderborn",
        rainToday: 5.015,
        rainLastHour: 2.065,
        temperature: 5.3,
        solarradiation: null,
        powerProduction: null,
        powerFeed: null
    },
    {
        "timestamp": "Fri Apr 18 20:28:50 UTC 2014",
        "localtime": "20:28",
        "wind": 0,
        barometer: 1000,
        "humidity": 76,
        "raining": false,
        "location": "Bonn",
        "rainToday": 1.475,
        "rainLastHour": "",
        "temperature": 8.6,
        "solarradiation": null,
        "powerProduction": null,
        "powerFeed": null
    },
    {
        "timestamp": "Fri Apr 18 20:29:39 UTC 2014",
        "localtime": "20:29",
        "wind": 0,
        barometer: 1000,
        "humidity": 84,
        "raining": false,
        "location": "Freiburg",
        "rainToday": 2.065,
        "rainLastHour": "",
        "temperature": 4.6,
        "solarradiation": null,
        "powerProduction": null,
        "powerFeed": null
    }
];
var singleTestdaten = {
    "timestamp": "Fri Apr 18 20:28:11 UTC 2014",
    "wind": 0,
    barometer: 1000,
    "humidity": 85,
    "raining": false,
    "location": "Paderborn",
    "rainToday": 5.015,
    "rainLastHour": 2.065,
    "temperature": 5.3,
    "solarradiation": null,
    "powerProduction": null,
    "powerFeed": null
};
function fetchWeatherData(processWeatherData, reportConnectionProblem) {
    var ajaxRequest = new XMLHttpRequest();
    ajaxRequest.onload = function () {
        var weatherData = JSON.parse(ajaxRequest.responseText);
        processWeatherData(weatherData);
    };
    ajaxRequest.onerror = reportConnectionProblem;
    ajaxRequest.open("get", "/weatherstation/current?build=web", true);
    ajaxRequest.send();
}
function fetchStatisticsData(processStatistics, reportConnectionProblem) {
    var ajaxRequest = new XMLHttpRequest();
    ajaxRequest.onload = function () {
        var weatherData = JSON.parse(ajaxRequest.responseText);
        processStatistics(weatherData);
    };
    ajaxRequest.onerror = reportConnectionProblem;
    ajaxRequest.open("get", "/weatherstation/query?build=web", true);
    ajaxRequest.send();
}
function getOptionalNumber(value, unit) {
    if (value != null) {
        var result = value.toFixed(1);
        return result.replace(".", ",") + " " + unit;
    }
    else {
        return "";
    }
}
function getTimeFractionAsString(dateAsString) {
    var date = new Date(dateAsString);
    var hours = date.getHours();
    var minutes = date.getMinutes();
    return hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
}
