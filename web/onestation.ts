/// <reference path='services.ts'/>

class WeatherArea {
  labelLocation:HTMLElement = document.getElementById("location");
  labelTime:HTMLElement = document.getElementById("time");
  labelProblem:HTMLElement = document.getElementById("problem");
  labelTemperature:HTMLElement = document.getElementById("temperature");
  labelHumidity:HTMLElement = document.getElementById("humidity");
  labelWindText:HTMLElement = document.getElementById("windText");
  labelWind:HTMLElement = document.getElementById("wind");
  labelRain:HTMLElement = document.getElementById("rain");
  labelRainText:HTMLElement = document.getElementById("rainText");
  labelWattText:HTMLElement = document.getElementById("wattText");
  labelWatt = document.getElementById("watt");

  rainInfoButton = this.createMoreInfoButton();

  update(weatherData:WeatherData):void {
    this.updateLabel(this.labelTime, weatherData.localtime);
    this.updateLabel(this.labelTemperature, getOptionalNumber(weatherData.temperature, "°C"));
    this.updateLabel(this.labelHumidity, weatherData.humidity + " %");
    if (this.labelWind !== undefined) {
      if (weatherData.wind != null) {
        this.updateLabel(this.labelWindText, "Wind:");
        this.updateLabel(this.labelWind, getOptionalNumber(weatherData.wind, "km/h"));
      }
    }
    if (weatherData.rain_today != null) {
      this.updateLabel(this.labelRainText, "Regen:");
      this.updateLabel(this.labelRain, this.combineRainValues(weatherData.rain, weatherData.rain_today));
      this.labelRain.appendChild(this.rainInfoButton);
    }

    if (this.labelWatt !== undefined) {
      this.updateLabel(this.labelWattText, "");
      this.updateLabel(this.labelWatt, "");
      if (weatherData.hasOwnProperty("watt") && weatherData.watt > 0) {
        this.updateLabel(this.labelWattText, "Leistung:");
        this.updateLabel(this.labelWatt, getOptionalNumber(weatherData.watt, "W"));
      }
    }
  }

  setup(weatherData:WeatherData):void {
    this.updateLabel(this.labelLocation, weatherData.location);
    this.update(weatherData);
  }

  reportProblem(problem:string):void {
    this.updateLabel(this.labelProblem, problem);
  }

  clear():void {
    for (let label in this) {
      if (this.hasOwnProperty(label)) {
        let myType = typeof this[label];
        if (myType != 'function' && label != 'labelLocation' && label != 'rainInfoButton') {
          this.updateLabel(this[label], "");
        }
      }
    }
  }

  updateLabel(label:HTMLElement, text:string) {
    if (label != null) {
      label.innerHTML = text;
    }
  }

  combineRainValues(rain, rainToday):string {
    let result:string = "";

    if (rainToday != "") {
      result = getOptionalNumber(rainToday, " l");
      if (rain != "") {
        result += " / " + getOptionalNumber(rain, " l");
      }
    }
    return result;
  }

  createMoreInfoButton():HTMLElement {
    let button:HTMLButtonElement = document.createElement("button");

    button.innerHTML = "?";
    button.className = "rain_button";
    button.id = "rain_button";
    button.onclick = function () {
      this.disabled = true;
      showMoreRain();
    };

    return button;
  }

}


class RainArea {
  div:HTMLDivElement;

  constructor() {
    this.div = document.createElement("div");
    this.div.className = "rain_div hidden";
    this.div.id = "rain_div";
    this.div.onclick = function () {
      let root:HTMLElement = document.getElementById("content");
      root.removeChild(this);
      let rainInfoButton:HTMLButtonElement = <HTMLButtonElement>document.getElementById("rain_button");
      rainInfoButton.disabled = false;
    };

    let caption:HTMLDivElement = document.createElement("div");
    caption.className = "rain_caption";
    caption.innerHTML = "Regen";
    this.div.appendChild(caption);

    let root:HTMLElement = document.getElementById("content");
    root.appendChild(this.div);

    window.setTimeout(makeVisible(this.div), 20, this.div);
  }

  setup(rainData):void {
    this.addRow("letzte Stunde:", rainData.lastHour);
    this.addRow("heute:", rainData.today);
    this.addRow("gestern:", rainData.yesterday);
    this.addRow("letzte Woche:", rainData.lastWeek);
    this.addRow("letzte 30 Tage:", rainData.last30days);
  }

  addRow(labelText:string, value) {
    let labelDiv:HTMLDivElement = document.createElement("div");
    labelDiv.className = "rain_label";
    labelDiv.innerHTML = labelText;
    this.div.appendChild(labelDiv);


    let valueDiv:HTMLDivElement = document.createElement("div");
    valueDiv.className = "rain_value";
    valueDiv.innerHTML = getOptionalNumber(value, " l");
    this.div.appendChild(valueDiv);
  }

  makeVisible():void {
    this.div.className = "rain_div transition";
  }

}


function showMoreRain() {
  let rainArea = new RainArea();

  function showRain(rainData) {
    rainArea.setup(rainData);
  }

  fetchWeatherData(showRain, null, "rain");

}

function makeVisible (div) {
  div.className = "rain_div transition";
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

  fetchWeatherData(initializeWeatherArea, reportConnectionProblem, "current");
  window.setInterval(fetchWeatherData, 3 * 60 * 1000, updateWeatherArea, reportConnectionProblem, "current");
}

