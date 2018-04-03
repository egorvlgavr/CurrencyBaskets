// Chart.js scripts
// -- Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#292b2c';
// -- Currency Pie Chart
function drawPieChart() {

    // TODO remove id
    var jsonData = $.ajax({
        url: '/aggregated/amount?userId=1',
        dataType: 'json'
    }).done(function (results) {

        var labels = [], data=[];
        results.forEach(function(packet) {
          labels.push(packet.currency);
          data.push(packet.amount);
        });

        // Get the context of the canvas element we want to select
        var ctx = document.getElementById("currencyPieChart");

        var myPieChart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: ['#85bb65', '#1761DC', '#FF5C00'],
                }]
            }
        });
    });
}

drawPieChart();
