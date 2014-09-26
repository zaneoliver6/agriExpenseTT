/*global define */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global $:false, jQuery:false, _:false, moment:false, gapi:false, dateFilter_start: true,
dateFilter_end: true, dateFilter_prevStart: true, dateFilter_prevEnd: true, generatePurchaseCharts:false, generateProductionCharts:false, generateHarvestCharts:false */

function fetchProductionData() {
    'use strict';
    var cropValue = $("#cropSelection").val(),
        selectedArea = $("#areaSelection").val(),
        queryData = gapi.client.cycleendpoint.getMatchingCycles({
            "cropName": cropValue,
            "selectedArea": selectedArea,
            "start_date": dateFilter_start.toString(),
            "end_date": dateFilter_end.toString()
        });
    queryData.execute(function (resp) {
        generateProductionCharts(resp);
    });
}

function fetchHarvestData() {
    'use strict';
    // Get selected year 
    var selectedYear = $('#yearpicker').val();

    // Format year by appending date and month using the moment plugin
    dateFilter_start = moment([selectedYear, 0, 1, 0, 0, 0, 0]);
    dateFilter_end = moment([selectedYear, 11, 31, 23, 59, 59, 999]);

    // Get the previous year for comparison
    dateFilter_prevStart = dateFilter_start.clone().subtract(1, 'year');
    dateFilter_prevEnd = dateFilter_end.clone().subtract(1, 'year');

    // Convert to unix timestamp
    dateFilter_start = dateFilter_start.valueOf();
    dateFilter_end = dateFilter_end.valueOf();
    dateFilter_prevStart = dateFilter_prevStart.valueOf();
    dateFilter_prevEnd = dateFilter_prevEnd.valueOf();

    var cropValue = $("#cropSelection").val(),
        selectedArea = $("#areaSelection").val(),
        queryData = gapi.client.cycleendpoint.getMatchingCycles({
            "cropName": cropValue,
            "selectedArea": selectedArea,
            "start_date": dateFilter_prevStart.toString(),
            "end_date": dateFilter_end.toString()
        });
    queryData.execute(function (resp) {
        generateHarvestCharts(resp);
    });
}

function fetchAllCycles() {
    'use strict';
    var queryData_Cycle = gapi.client.cycleendpoint.getAllCycles({});
    var queryData_RPurchase = gapi.client.rpurchaseendpoint.getAllPurchases({});
    queryData_Cycle.execute(function (resp) {
        generatePurchaseCharts(resp);
    });
}