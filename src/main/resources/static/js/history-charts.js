
function drawAmountHistoryLineChart(fromParam) {
    $.ajax({
        url: '/history/amount',
        data: {from: fromParam},
        dataType: 'json'
    }).done(function (results){
        var labels = [], data=[];
        results.forEach(function(packet) {
            labels.push(packet.label);
            data.push(packet.amount);
        });

        $("canvas#historyAmount").remove();
        $("#historyAmountHolder").append('<canvas id="historyAmount" width="100%" height="30"></canvas>');
        var ctx = document.getElementById("historyAmount");
        var historyAmountLineChart = new Chart(ctx, {
          type: 'line',
          data: {
            labels: labels,
            datasets: [{
              label: "Total amount",
              lineTension: 0.3,
              backgroundColor: "rgba(2,117,216,0.2)",
              borderColor: "rgba(2,117,216,1)",
              pointRadius: 5,
              pointBackgroundColor: "rgba(2,117,216,1)",
              pointBorderColor: "rgba(255,255,255,0.8)",
              pointHoverRadius: 5,
              pointHoverBackgroundColor: "rgba(2,117,216,1)",
              pointHitRadius: 20,
              pointBorderWidth: 2,
              data: data,
            }],
          },
          options: {
            scales: {
              xAxes: [{
                time: {
                  unit: 'date'
                },
                ticks: {
                  maxTicksLimit: 7
                }
              }],
              yAxes: [{
                ticks: {
                  min: 0,
                  maxTicksLimit: 5
                },
                gridLines: {
                  color: "rgba(0, 0, 0, .125)",
                }
              }],
            },
            legend: {
              display: false
            }
          }
        });
        if (labels.length > 0) {
            var latestLabel = labels[labels.length - 1];
            $('#amountHistoryFooter').text('Updated ' + latestLabel);
        }
    });
}

function drawRatesHistoryLineChart(fromParam) {
    $.ajax({
        url: '/history/rates',
        data: {from: fromParam},
        dataType: 'json'
    }).done(function (results){
        var chartData = [];
        var labelesSet = new Set();
        results.forEach(function(packet) {
            chartData.push({
                data: packet.history.map(function (elem) {
                    return {x: elem.label,
                        y: elem.amount
                    }
                }),
                label: packet.currency,
                borderColor: packet.color,
                fill: false
            });
            packet.history.forEach(function (hist) {
                labelesSet.add(hist.label);
            });
        });
        var labels = processLabels(labelesSet);


        $("canvas#historyRates").remove();
        $("#historyRateHolder").append('<canvas id="historyRates" width="100%" height="30"></canvas>');
        var ctx = document.getElementById("historyRates");
        var historyRatesLineChart =new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: chartData
            },
            options: {
                scales: {
                    yAxes: [{ticks: {min: 0}}]
                }
            }
        });
        if (labels.length > 0) {
            var latestLabel = labels[labels.length - 1];
            $('#ratesHistoryFooter').text('Updated ' + latestLabel);
        }
    });
}

function processLabels(labelesSet) {
    var labels = [];
    labelesSet.forEach(function (elem) {
        labels.push(moment(elem, "DD-MM-YYYY"));
    });
    labels.sort(function (a, b) {
        return a - b;
    });
    return labels.map(function (elem) {
        return moment(elem).format("DD-MM-YYYY")
    })
}


/**
 *  ===== Amount history ====
 */
function drawAmountHistoryLineChartMonth() {
    drawAmountHistoryLineChart('month');
}

function drawAmountHistoryLineChartYear() {
    drawAmountHistoryLineChart('year');
}

function drawAmountHistoryLineChartWeek() {
    drawAmountHistoryLineChart('week');
}

function drawAmountHistoryLineChartAll() {
    drawAmountHistoryLineChart('all');
}


/**
 *  ===== Rates history ====
 */
function drawRatesHistoryLineChartMonth() {
    drawRatesHistoryLineChart('month');
}

function drawRatesHistoryLineChartYear() {
    drawRatesHistoryLineChart('year');
}

function drawRatesHistoryLineChartWeek() {
    drawRatesHistoryLineChart('week');
}

function drawRatesHistoryLineChartAll() {
    drawRatesHistoryLineChart('all');
}


/**
 *  ===== INIT ====
 */
// this is called because year charts is active by default
$(function() {
    drawAmountHistoryLineChartYear();
    drawRatesHistoryLineChartYear();
});