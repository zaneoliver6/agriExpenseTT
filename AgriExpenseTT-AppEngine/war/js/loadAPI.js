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
    var cropSelection = document.getElementById("cropSelection");
    var cropValue = cropSelection.options[cropSelection.selectedIndex].value;
    var inputRange = $(rangeSel).data('slider').getValue();
    var start = parseFloat(inputRange[0]);
    var end = parseFloat(inputRange[1]);
    var queryData = gapi.client.cycleendpoint.getMatchingCycles({"cropName": cropValue,"landQty": landValue,"start": start,"end": end});
    queryData.execute(function(resp) { generateChart(resp); });*/
}

function generateChart(cycleItems) {
    //'use strict';
    var items = cycleItems.items, i, item, chart;
  
    if (!items || items.length === 0) {
        console.log('No Data Returned');
    } else {
        $(function () {
            $('#interactive').highcharts({
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
            chart = $('#interactive').highcharts();
            chart.series[0].setData(mySeries);
        });
    }
}