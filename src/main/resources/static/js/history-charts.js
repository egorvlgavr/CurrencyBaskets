
function drawAmountHistoryLineChart(fromParam) {
    var jsonData = $.ajax({
        url: '/history/amount',
        data: {from: fromParam},
        dataType: 'json'
    }).done(function (results){
        var labels = [], data=[];
        results.forEach(function(packet) {
            labels.push(packet.label);
            data.push(packet.amount);
        });
        var maxYAxis = Math.max.apply(Math,data) * 1.2;

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
                gridLines: {
                  display: false
                },
                ticks: {
                  maxTicksLimit: 7
                }
              }],
              yAxes: [{
                ticks: {
                  min: 0,
                  max: maxYAxis,
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

function drawAmountHistoryLineChartMonth() {
    drawAmountHistoryLineChart('month')
}

function drawAmountHistoryLineChartYear() {
    drawAmountHistoryLineChart('year')
}

function drawAmountHistoryLineChartWeek() {
    drawAmountHistoryLineChart('week')
}

// this is called because year chart is active by default
$(function() {
    drawAmountHistoryLineChartYear()
});