/// <reference path='services.ts'/>
var WeatherArea = /** @class */ (function () {
    function WeatherArea() {
        this.labelLocation = document.getElementById("location");
        this.labelTime = document.getElementById("time");
        this.labelProblem = document.getElementById("problem");
        this.labelTemperature = document.getElementById("temperature");
        this.labelHumidity = document.getElementById("humidity");
        this.labelBarometer = document.getElementById("barometer");
        this.labelWindText = document.getElementById("windText");
        this.labelWind = document.getElementById("wind");
        this.labelRain = document.getElementById("rain");
        this.labelRainText = document.getElementById("rainText");
        this.labelSolarradiationText = document.getElementById("solarradiationText");
        this.labelSolarradiation = document.getElementById("solarradiation");
    }
    WeatherArea.prototype.update = function (weatherData) {
        this.updateLabel(this.labelTime, weatherData.localtime);
        this.updateLabel(this.labelTemperature, getOptionalNumber(weatherData.temperature, "°C"));
        this.updateLabel(this.labelHumidity, weatherData.humidity + " %");
        this.updateLabel(this.labelBarometer, getOptionalNumber(weatherData.barometer, "hPa"));
        if (this.labelWind !== undefined) {
            if (weatherData.wind != null) {
                this.updateLabel(this.labelWindText, "Wind:");
                this.updateLabel(this.labelWind, getOptionalNumber(weatherData.wind, "km/h"));
            }
        }
        if (weatherData.rain_today != null) {
            this.updateLabel(this.labelRainText, "Regen:");
            this.updateLabel(this.labelRain, this.combineRainValues(weatherData.rain, weatherData.rain_today));
        }
        if (weatherData.hasOwnProperty("solarradiation") && weatherData.solarradiation > 0) {
            this.updateLabel(this.labelSolarradiationText, "Sonneneinstrahlung:");
            this.updateLabel(this.labelSolarradiation, getOptionalNumber(weatherData.solarradiation, "W/qm"));
        }
        else {
            this.updateLabel(this.labelSolarradiationText, "");
            this.updateLabel(this.labelSolarradiation, "");
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
        if (rainToday != null) {
            result = getOptionalNumber(rainToday, " l");
            if (rain != null) {
                result += " / " + getOptionalNumber(rain, " l");
            }
        }
        return result;
    };
    return WeatherArea;
}());
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
        weatherArea.reportProblem("Verbindung gestört!");
    }
    fetchWeatherData(initializeWeatherArea, reportConnectionProblem);
    window.setInterval(fetchWeatherData, 3 * 60 * 1000, updateWeatherArea, reportConnectionProblem);
}
