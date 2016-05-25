/// <reference path='services.ts'/>

class Column {
  caption:string;
  style:string;
  visible:boolean;
  value:any [];

}

class Columns {
  timestamp: Column;
  location: Column;
  temperature: Column;
  humidity: Column;
  rain: Column;
  rain_today: Column;
}

class WorkingArea {
  area:HTMLElement = document.getElementById("working_area");
  columns:Columns;

  weatherData:WeatherData[];

  constructor() {
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
    }
  }

  update(newData:WeatherData[]):void {
    this.weatherData = newData;
    this.updateColumns();
    this.area.appendChild(this.createCaption());
    for (let row = 0; row < this.weatherData.length; row++) {
      this.area.appendChild(this.createRow(row));
    }
  }

  clear():void {
    while (this.area.hasChildNodes()) {
      this.area.removeChild(this.area.lastChild);
    }
  }

  reportProblem(problemDescription:string):void {
    let errorLine:HTMLElement = document.getElementById("errorLine");
    if (errorLine != undefined) {
      errorLine.innerHTML = problemDescription;
    } else {
      errorLine = WorkingArea.createErrorLine(problemDescription);
      errorLine.id = "errorLine";
      WorkingArea.prependChild(this.area, errorLine);
    }
  }

  updateColumns():void {
    for (let i in this.columns) {
      if (this.columns.hasOwnProperty(i)) {
        this.columns[i].visible = false;
        this.columns[i].value = [];
      }
    }

    for (let i in this.weatherData) {
      if (this.weatherData.hasOwnProperty(i)) {
        this.columns.timestamp.value[i] = this.weatherData[i].localtime;
        this.columns.location.value[i] = new WeatherLocation(this.weatherData[i]);
        this.columns.temperature.value[i] = getOptionalNumber(this.weatherData[i].temperature, "°C");
        this.columns.humidity.value[i] = this.weatherData[i].humidity + " %";
        this.columns.rain.value[i] = getOptionalNumber(this.weatherData[i].rain, "l");
        this.columns.rain_today.value[i] = getOptionalNumber(this.weatherData[i].rain_today, "l");
      }
    }

    for (let i in this.columns) {
      if (this.columns.hasOwnProperty(i)) {
        this.columns[i].visible = WorkingArea.detectFilledColumn(this.columns[i].value);
      }
    }
  }

  private static detectFilledColumn(value):boolean {
    let filled = false;
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
  }

  private static createErrorLine(errorMessage:string):HTMLElement {
    let errorLine:HTMLDivElement = document.createElement("div");
    errorLine.className = "error";
    errorLine.id = "errorLine";
    errorLine.innerHTML = errorMessage;
    return errorLine;
  }

  private createCaption():HTMLElement {
    let caption:HTMLDivElement = document.createElement("div");
    caption.className = "caption";

    for (let i in this.columns) {
      if (this.columns.hasOwnProperty(i)) {
        let element = WorkingArea.createWeatherElement(this.columns[i].caption, this.columns[i].style);
        WorkingArea.appendChild(caption, this.columns[i].visible, element);
      }
    }

    return caption;
  }

  private static createWeatherElement(text:string, style:string):HTMLElement {
    let element:HTMLDivElement = document.createElement("div");
    element.className = "column " + style;
    element.innerHTML = text;
    return element;
  }

  private createRow(rowNumber):HTMLElement {
    let row:HTMLDivElement = document.createElement("div");
    row.className = "row";

    for (var i in this.columns) {
      if (this.columns.hasOwnProperty(i)) {
        var element = this.createWeatherSpan(this.columns[i].value[rowNumber], this.columns[i].style);
        WorkingArea.appendChild(row, this.columns[i].visible, element);
      }
    }
    return row;
  }

  private static appendChild(parent:HTMLElement, visible:boolean, child:HTMLElement):void {
    if (visible) {
      parent.appendChild(child);
    }
  }

  private static prependChild(parent:HTMLElement, child:HTMLElement):void {
    parent.insertBefore(child, parent.firstChild);
  }


  private createWeatherSpan(value:any, style:string):HTMLElement {
    let element:HTMLDivElement = document.createElement("div");
    element.className = "column " + style;

    let span:HTMLSpanElement = document.createElement("span");
    span.className = "value";
    if (value instanceof WeatherLocation) {
      let link:HTMLAnchorElement = document.createElement("a");
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
  }
}


class WeatherLocation {
  description:string;
  forecast:string;

  constructor(weatherDataset:any) {
    this.description = weatherDataset.location;
    this.forecast = weatherDataset.forecast;
  }
}

function init() {
  let workingArea = new WorkingArea();
  function updateWeatherView(weatherData) {
    workingArea.clear();
    workingArea.update(weatherData);
  }

  function reportConnectionProblem() {
    workingArea.reportProblem("Verbindung gestört");
  }

  let urlParam = window.location.search.substring(1);

  fetchAllWeatherData(updateWeatherView, reportConnectionProblem, urlParam);
  window.setInterval(fetchAllWeatherData, 3 * 60 * 1000, updateWeatherView, reportConnectionProblem, urlParam);
}

