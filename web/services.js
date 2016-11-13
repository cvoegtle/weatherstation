var WeatherData = (function () {
    function WeatherData() {
    }
    return WeatherData;
}());
var testdaten = [
    {
        timestamp: "Fri Apr 18 20:28:11 UTC 2014",
        localtime: "20:28",
        wind: 0,
        humidity: 85,
        raining: false,
        location: "Paderborn",
        rain_today: 5.015,
        rain: 2.065,
        temperature: 5.3,
        watt: null
    },
    {
        "timestamp": "Fri Apr 18 20:28:50 UTC 2014",
        "localtime": "20:28",
        "wind": 0,
        "humidity": 76,
        "raining": false,
        "location": "Bonn",
        "rain_today": 1.475,
        "rain": "",
        "temperature": 8.6,
        watt: null
    },
    {
        "timestamp": "Fri Apr 18 20:29:39 UTC 2014",
        "localtime": "20:29",
        "wind": 0,
        "humidity": 84,
        "raining": false,
        "location": "Freiburg",
        "rain_today": 2.065,
        "rain": "",
        "temperature": 4.6,
        watt: null
    }
];
var singleTestdaten = {
    "timestamp": "Fri Apr 18 20:28:11 UTC 2014",
    "wind": 0,
    "humidity": 85,
    "raining": false,
    "location": "Paderborn",
    "rain_today": 5.015,
    "rain": 2.065,
    "temperature": 5.3,
    watt: null
};
function fetchAllWeatherData(processWeatherData, reportConnectionProblem, urlParam) {
    var ajaxRequest = new XMLHttpRequest();
    ajaxRequest.onload = function () {
        var weatherData = JSON.parse(ajaxRequest.responseText);
        processWeatherData(weatherData);
    };
    ajaxRequest.onerror = reportConnectionProblem;
    ajaxRequest.open("get", "/weatherstation/read?" + urlParam + "&build=web&type=current", true);
    ajaxRequest.send();
}
function fetchWeatherData(processWeatherData, reportConnectionProblem, type) {
    var ajaxRequest = new XMLHttpRequest();
    ajaxRequest.onload = function () {
        var weatherData = JSON.parse(ajaxRequest.responseText);
        processWeatherData(weatherData);
    };
    ajaxRequest.onerror = reportConnectionProblem;
    ajaxRequest.open("get", "/weatherstation/query?type=" + type + "&new&build=web", true);
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
//# sourceMappingURL=services.js.map