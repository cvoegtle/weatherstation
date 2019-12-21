class WeatherData {
  timestamp:string;
  localtime:string;
  wind:number;
  humidity:number;
  raining:boolean;
  location:string;
  rain_today:number;
  rain:any;
  temperature:number;
  watt:any;
}


let testdaten:WeatherData[] = [

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

let singleTestdaten = {
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

function fetchAllWeatherData(processWeatherData:Function, reportConnectionProblem:any, urlParam:string) {
  var ajaxRequest = new XMLHttpRequest();
  ajaxRequest.onload = function () {
    let weatherData:WeatherData[] = JSON.parse(ajaxRequest.responseText);
    processWeatherData(weatherData);


  };
  ajaxRequest.onerror = reportConnectionProblem;


  ajaxRequest.open("get", "/weatherstation/read?" + urlParam + "&build=web&type=current", true);
  ajaxRequest.send();
}

function fetchWeatherData(processWeatherData:Function, reportConnectionProblem:any, type:string):void {
  let ajaxRequest = new XMLHttpRequest();
  ajaxRequest.onload = function () {
    let weatherData = JSON.parse(ajaxRequest.responseText);
    processWeatherData(weatherData);


  };
  ajaxRequest.onerror = reportConnectionProblem;

  ajaxRequest.open("get", "/weatherstation/query?type=" + type + "&new&build=web", true);
  ajaxRequest.send();
}

function getOptionalNumber(value:any, unit:string):string {
  if (value != null && value != "") {
    let result = value.toFixed(1);
    return result.replace(".", ",") + " " + unit;
  } else {
    return "";
  }
}

function getTimeFractionAsString(dateAsString:string):string {
  let date = new Date(dateAsString);
  let hours = date.getHours();
  let minutes = date.getMinutes();

  return hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
}

