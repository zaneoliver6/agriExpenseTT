function showSuccess() {
  //  'use strict';
    $("#alertArea").hide();
    $("#successArea").show();
    $("#successArea").fadeOut(1000);
}

function showError(errorHtml) {
   // 'use strict';
    $("#alertArea").removeClass('alert-error alert-info alert-success').addClass('alert-error');
    $("#alertContentArea").html(errorHtml);
    $("#alertArea").show();
}

function showInfo(infoHtml) {
   // 'use strict';
    $("#alertArea").removeClass('alert-error alert-info alert-success').addClass('alert-info');
    $("#alertContentArea").html(infoHtml);
    $("#alertArea").show();
}

// This method loads the Endpoint libraries
function loadGapi() {
    //'use strict';
    gapi.client.load('cycleendpoint', 'v1', function () {
        console.log("API Loaded");
       // fetchData();
    });
}

// Function for checking error responses; it correctly sanitizes error messages
// so that they are safe to display in the UI
function checkErrorResponse(result) {
    //'use strict';
    if (result && result.error) {
        var safeErrorHtml = $('<div/>').text(result.error.message).html();
        return {isError: true, errorMessage: safeErrorHtml};
    }
  
    return {isError: false};
}

function fetchData() {
    //'use strict';
	var option1 = document.getElementById("crop");
    var a = option1.options[option1.selectedIndex].value;
    var option2 = document.getElementById("landQty");
    var b = parseFloat(option2.options[option2.selectedIndex].value);
    var x = gapi.client.cycleendpoint.getMatchingCycles({"cropName": a,"landQty": b});
    x.execute(function(resp) { generateChart(resp); });
}

function generateChart(cycleItems) {
    //'use strict';
    var items = cycleItems.items, i, item, chart;
  
    if (!items || items.length === 0) {
        console.log('No Data Returned');
    } else {
        $(function () {
            $('#container').highcharts({
                title: {
                    text: 'Cost of Cycle Items',
                    x: -20 // Center
                },
                chart: {},
                plotOptions: {
                    series: {
                        allowPointSelect: true
                    }
                },
                series: [{
                    data: []
                }]
            });
            var mySeries = [];
            for (i = 0; i < items.length; i++) {
                item = items[i];
                if (item.costPer) {
                    console.log(item.landQty);
                }
                mySeries.push([item.landQty]);
               // i++;
            }
            chart = $('#container').highcharts();
            chart.series[0].setData(mySeries);
        });
    }
}