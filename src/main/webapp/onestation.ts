/// <reference path='services.ts'/>

class WeatherArea {
  labelLocation: HTMLElement = document.getElementById("location");
  labelTime: HTMLElement = document.getElementById("time");
  labelProblem: HTMLElement = document.getElementById("problem");
  labelTemperature: HTMLElement = document.getElementById("temperature");
  labelHumidity: HTMLElement = document.getElementById("humidity");
  labelBarometer: HTMLElement = document.getElementById("barometer");
  labelWindText: HTMLElement = document.getElementById("windText");
  labelWind: HTMLElement = document.getElementById("wind");
  labelWindgustText = document.getElementById("windgustText");
  labelWindgust = document.getElementById("windgust");
  labelRain: HTMLElement = document.getElementById("rain");
  labelRainText: HTMLElement = document.getElementById("rainText");
  labelSolarradiationText: HTMLElement = document.getElementById("solarradiationText");
  labelSolarradiation: HTMLElement = document.getElementById("solarradiation");
  labelPowerProductionText: HTMLElement = document.getElementById("powerProductionText");
  labelPowerProduction:HTMLElement = document.getElementById("powerProduction");
  labelPowerFeedText: HTMLElement = document.getElementById("powerFeedText");
  labelPowerFeed:HTMLElement = document.getElementById("powerFeed");

  update(weatherData: WeatherData): void {
    this.updateLabel(this.labelTime, weatherData.localtime);
    this.updateLabel(this.labelTemperature, getOptionalNumber(weatherData.temperature, "°C"));
    this.updateLabel(this.labelHumidity, weatherData.humidity + " %");
    this.updateLabel(this.labelBarometer, getOptionalNumber(weatherData.barometer, "hPa"));
    if (this.labelWind !== undefined ) {
      if (weatherData.windspeed != null) {
        this.updateLabel(this.labelWindText, "Wind:");
        this.updateLabel(this.labelWind, getOptionalNumber(weatherData.windspeed, "km/h"));
      }
    }
    if (this.labelWindgust !== undefined) {
      if (weatherData.windgust != null) {
        this.updateLabel(this.labelWindgustText, "Böen:");
        this.updateLabel(this.labelWindgust, getOptionalNumber(weatherData.windgust, "km/h"));
      }
    }

    if (weatherData.rainToday != null) {
      this.updateLabel(this.labelRainText, "Regen:");
      this.updateLabel(this.labelRain, this.combineRainValues(weatherData.rainLastHour, weatherData.rainToday));
    }

    if (weatherData.hasOwnProperty("solarradiation") && weatherData.solarradiation > 0) {
      this.updateLabel(this.labelSolarradiationText, "Sonneneinstrahlung:");
      this.updateLabel(this.labelSolarradiation, getOptionalNumber(weatherData.solarradiation, "W/qm"));
    } else {
      this.updateLabel(this.labelSolarradiationText, "");
      this.updateLabel(this.labelSolarradiation, "");
    }

    if (weatherData.hasOwnProperty("powerProduction") && weatherData.powerProduction > 0) {
      this.updateLabel(this.labelPowerProductionText, "Leistung Solaranlage: ");
      this.updateLabel(this.labelPowerProduction, getOptionalNumber(weatherData.powerProduction, "W"))
    } else {
      this.updateLabel(this.labelPowerProductionText, "");
      this.updateLabel(this.labelPowerProduction, "");
    }

    if (weatherData.hasOwnProperty("powerFeed") && weatherData.powerFeed > 0) {
      this.updateLabel(this.labelPowerFeedText, "Einspeisung: ");
      this.updateLabel(this.labelPowerFeed, getOptionalNumber(weatherData.powerFeed, "W"))
    } else {
      this.updateLabel(this.labelPowerFeedText, "");
      this.updateLabel(this.labelPowerFeed, "");
    }
  }

  setup(weatherData: WeatherData): void {
    this.updateLabel(this.labelLocation, weatherData.location);
    this.update(weatherData);
  }

  reportProblem(problem: string): void {
    this.updateLabel(this.labelProblem, problem);
  }

  clear(): void {
    for (let label in this) {
      if (this.hasOwnProperty(label)) {
        let myType = typeof this[label];
        if (myType != 'function' && label != 'labelLocation' && label != 'rainInfoButton') {
          this.updateLabel(this[label], "");
        }
      }
    }
  }

  updateLabel(label: HTMLElement, text: string) {
    if (label != null) {
      label.innerHTML = text;
    }
  }

  combineRainValues(rain: number, rainToday: number): string {
    let result: string = "";

    if (rainToday != null) {
      result = getOptionalNumber(rainToday, " l");
      if (rain != null) {
        result += " / " + getOptionalNumber(rain, " l");
      }
    }
    return result;
  }


}

function repairinit() {
  init();

  let today:Date = new Date();

  let inputStart:HTMLInputElement = <HTMLInputElement>document.getElementById("starttime");
  let inputValue:string = today.getFullYear() + "-" + (today.getMonth() + 1) + "-" + today.getDate() + "-00:00:00";
  inputStart.value = inputValue;
  inputStart.setSelectionRange(0, inputValue.length);
  inputStart.focus();
}

function onKeyDownOnSecret(event) {
  if (event.keyCode === 13) {
    repairData();
  }
}

function repairData():void {
  let inputStart:HTMLInputElement = <HTMLInputElement>document.getElementById("starttime");
  let location:string = "/weatherstation/repair?begin=" + inputStart.value;

  let endinput:HTMLInputElement = <HTMLInputElement>document.getElementById("endtime");
  let endValue = endinput.value;
  if (endValue !== "") {
    location += "&end=" + endValue;
  }
  let inputSecret:HTMLInputElement = <HTMLInputElement>document.getElementById("secret");
  let secretValue:string = inputSecret.value;
  if (secretValue !== "") {
    location += "&secret=" + secretValue;
  }

  window.location.href = location;
}

function init() {
  let weatherArea = new WeatherArea();

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

