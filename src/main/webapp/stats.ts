/// <reference path='services.ts'/>
/// <reference path='onestation.ts'/>

function statsinit() {
  init();

  fetchStatisticsData(showStats, reportConnectionProblem);

}

function reportConnectionProblem(): void {
  // ignore connection problem
}

function showStats(response) {
  let container:HTMLElement = document.getElementById("stats_container");

  for (let i = 0; i < response.stats.length; i++) {
    let stats = response.stats[i];
    let columnName = translate(stats.range);
    if (columnName !== undefined) {
      let line:HTMLDivElement = document.createElement("div");
      line.className = "statistics_row";

      line.appendChild(create(columnName, "column_caption"));
      line.appendChild(create(getOptionalNumber(stats.rain, "l"), "statistics_value_narrow"));
      line.appendChild(create(getOptionalNumber(stats.minTemperature, "°C"), "statistics_value"));
      line.appendChild(create(getOptionalNumber(stats.maxTemperature, "°C"), "statistics_value"));
      if (stats.kwh !== undefined) {
        line.appendChild(create(getOptionalNumber(stats.kwh, "kWh"), "statistics_value2"));
        showKwhCaption();
      }
      container.appendChild(line);
    }
  }
}

function create(value, style:string):HTMLElement {
  let element:HTMLDivElement = document.createElement("div");
  element.className = style;
  element.innerHTML = value;
  return element;
}

function showKwhCaption() {
  let kwhCaption:HTMLElement = document.getElementById("caption_kwh");
  kwhCaption.innerHTML="Ertrag";
}

function translate(range:string):string {
  let translatedRange;
  if (range === 'today') {
    translatedRange = 'heute';
  } else if (range === 'yesterday') {
    translatedRange = 'gestern';
  } else if (range === 'last7days') {
    translatedRange = '7 Tage';
  } else if (range === 'last30days') {
    translatedRange = '30 Tage';
  }
  return translatedRange;
}
