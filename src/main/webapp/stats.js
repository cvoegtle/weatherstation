/// <reference path='services.ts'/>
/// <reference path='onestation.ts'/>
function statsinit() {
    init();
    fetchWeatherData(showStats, reportConnectionProblem, "stats");
}
function reportConnectionProblem() {
    // ignore connection problem
}
function showStats(response) {
    var container = document.getElementById("stats_container");
    for (var i = 0; i < response.stats.length; i++) {
        var stats = response.stats[i];
        var columnName = translate(stats.range);
        if (columnName !== undefined) {
            var line = document.createElement("div");
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
function create(value, style) {
    var element = document.createElement("div");
    element.className = style;
    element.innerHTML = value;
    return element;
}
function showKwhCaption() {
    var kwhCaption = document.getElementById("caption_kwh");
    kwhCaption.innerHTML = "Ertrag";
}
function translate(range) {
    var translatedRange;
    if (range === 'today') {
        translatedRange = 'heute';
    }
    else if (range === 'yesterday') {
        translatedRange = 'gestern';
    }
    else if (range === 'last7days') {
        translatedRange = '7 Tage';
    }
    else if (range === 'last30days') {
        translatedRange = '30 Tage';
    }
    return translatedRange;
}
