/*global define */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global $:false, jQuery:false, _:false, gapi:false, dateFilter_start: true,
dateFilter_end: true, dateFilter_prevStart: true, dateFilter_prevEnd: true, moment: true
*/


function generateProductionCharts(cycleItems) {
    'use strict';
    var items = cycleItems.items,
        i,
        j,
        item,
        item2,
        chart;

    if (!items || items.length === 0) {
        console.log('No Data Returned');
    } else {

        var prodSeries = [];
        var sortPivot = []; // To sort the entries according to date. Passed into lodash sortBy function
        var cropName;
        var sum;
        var outputVolume = []; // Total volume of crops produced across all counties
        var outputCost = []; // Total cost of production across all counties
        var binaryCounter = []; // To sum the volume/cost of production produced by each county

        //Parse data from object
        for (i = 0; i < items.length; i++) {
            item = items[i];
            sortPivot[0] = item.startDate;
            cropName = item.cropName;
            prodSeries.push([parseInt(item.startDate, 10), item.totalSpent]);
        }

        // Initalize counter to zero in order to track which items have been processed already

        for (i = 0; i < items.length; i++) {
            binaryCounter[i] = 0;
        }
        // Get the volume of crops produced by each county
        for (i = 0; i < items.length; i++) {
            if (binaryCounter[i] === 0) {
                item = items[i];
                if (item.harvestType === "Kg") {
                    item.harvestAmt = item.harvestAmt * 2.20462;
                }
                sum = item.harvestAmt;
                for (j = i + 1; j < items.length - 1; j++) {
                    item2 = items[j];
                    if (binaryCounter[j] === 0) {
                        if (item.county === item2.county) {
                            sum += item2.harvestAmt;
                            binaryCounter[j] = 1;
                        }
                    }
                }
                outputVolume.push([item.county, sum]);
                sum = 0;
            }
        }


        for (i = 0; i < items.length; i++) {
            binaryCounter[i] = 0;
        }
        // Get the total costs of production for each county
        for (i = 0; i < items.length; i++) {
            if (binaryCounter[i] === 0) {
                item = items[i];
                sum = item.totalSpent;
                for (j = i + 1; j < items.length - 1; j++) {
                    item2 = items[j];
                    if (binaryCounter[j] === 0) {
                        if (item.county === item2.county) {
                            sum += item2.totalSpent;
                            binaryCounter[j] = 1;
                        }
                    }
                }
                outputCost.push([item.county, sum]);
                sum = 0;
            }
        }

        $(function () {
            $('#stockContainer').highcharts('StockChart', {
                loading: {
                    hideDuration: 1000,
                    showDuration: 1000
                },
                yAxis: {
                    min: 0,
                    tickInterval: 550
                },
                rangeSelector: {
                    selected: 1,
                    inputEnabled: $('#stockContainer').width() > 480
                },

                tooltip: {
                    pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>${point.y}</b><br/>',
                    valueDecimals: 2
                },
                colors: ['#4D90FE', '#1BC123'],
                title: {
                    text: 'Costs of Production'
                },

                chart: {},
                series: [{
                    data: []
                }]
            });

            var sortedData = _.sortBy(prodSeries, function (sortPivot) {
                return sortPivot[0];
            });
            chart = $('#stockContainer').highcharts('StockChart');
            chart.series[0].setData(sortedData);
            chart.series[0].name = cropName;
            //console.log(sortedData);
        });

        // Generate pie chart by Volume
        $(function () {
            $('#pieContainer').highcharts({
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: 1, //null,
                    plotShadow: false
                },
                title: {
                    text: 'Production Breakdown by Volume'
                },
                tooltip: {
                    pointFormat: '{series.name}: <b> {point.y} Lb</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            format: '<b>{point.name} </b>: {point.percentage:.1f} % '
                        }
                    }
                },
                series: [{
                    type: 'pie',
                    name: 'Production Output',
                    data: []
                }]
            });
            chart = $('#pieContainer').highcharts();
            chart.series[0].setData(outputVolume);
        });

        // Generate pie chart by Volume
        $(function () {
            $('#pieContainer2').highcharts({
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: 1, //null,
                    plotShadow: false
                },
                title: {
                    text: 'Production Breakdown by Total Spent'
                },
                tooltip: {
                    pointFormat: '{series.name}: <b> ${point.y}</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            format: '<b>{point.name} </b>: {point.percentage:.1f} % '
                        }
                    }
                },
                series: [{
                    type: 'pie',
                    name: 'Production Costs',
                    data: []
                }]
            });
            chart = $('#pieContainer2').highcharts();
            chart.series[0].setData(outputCost);
        });
    }
}


function generateHarvestCharts(cycleItems) {
    'use strict';
    var items = cycleItems.items,
        i,
        j,
        item,
        item2,
        chart;

    if (!items || items.length === 0) {
        console.log('No Data Returned');
    } else {

        // We are generate charts for both the selected year and the previous year, so we need variables for both 
        var harvestSeries_selectedYear = [],
            harvestSeries_previousYear = [],
            plantedSeries_previousYear = [],
            plantedSeries_selectedYear = [],
            sortPivot = [], // To sort the entries according to date. Passed into lodash sortBy function
            harvestVolume_selectedYear = [], // Total volume of crops produced across all counties
            harvestVolume_previousYear = [],
            plantedAcerage_previousYear = [], // Total cost of production across all counties
            plantedAcerage_selectedYear = [],
            cropName,
            sum = 0,
            sum2 = 0,
            temp,
            a,
            b,
            months_selectedYear = [],
            months_previousYear = [];

        //Parse data from object
        for (i = 0; i < items.length; i++) {
            item = items[i];
            sortPivot[0] = item.startDate;
            cropName = item.cropName;

            if (item.harvestType === "Kg") {
                item.harvestAmt *= 2.20462; // Convert to lbs
                item.harvestAmt = parseFloat(item.harvestAmt.toFixed(2)); // Round to 2 decimal places
            }

            if (item.landType === "Hectre") {
                item.landQty *= 2.47105; // Convert to acres
                item.landQty = parseFloat(item.landQty.toFixed(2));
            }

            if (item.startDate >= dateFilter_prevStart && item.startDate <= dateFilter_prevEnd) {
                harvestSeries_previousYear.push([parseInt(item.startDate, 10), item.harvestAmt]);
                plantedSeries_previousYear.push([parseInt(item.startDate, 10), item.landQty]);
            }
            if (item.startDate >= dateFilter_start && item.startDate <= dateFilter_end) {
                harvestSeries_selectedYear.push([parseInt(item.startDate, 10), item.harvestAmt]);
                plantedSeries_selectedYear.push([parseInt(item.startDate, 10), item.landQty]);
            }
        }
        //        console.log(harvestSeries_previousYear);
        //      console.log(harvestSeries_selectedYear);

        // Sort and group each result by month

        var sortedLandQty_previousYear = _.sortBy(plantedSeries_previousYear, function (sortPivot) {
            return sortPivot[0];
        });

        var sortedLandQty_selectedYear = _.sortBy(plantedSeries_selectedYear, function (sortPivot) {
            return sortPivot[0];
        });

        var sortedHarvestAmt_previousYear = _.sortBy(harvestSeries_previousYear, function (sortPivot) {
            return sortPivot[0];
        });

        var sortedHarvestAmt_selectedYear = _.sortBy(harvestSeries_selectedYear, function (sortPivot) {
            return sortPivot[0];
        });

        for (i = 0; i < items.length; i++) {
            item = items[i];
            if (item.startDate >= dateFilter_prevStart && item.startDate <= dateFilter_prevEnd) {
                temp = new Date(parseInt(item.startDate, 10));
                months_previousYear[i] = temp.getMonth();
            }

            if (item.startDate >= dateFilter_start && item.startDate <= dateFilter_end) {
                temp = new Date(parseInt(item.startDate, 10));
                months_selectedYear[i] = temp.getMonth();
            }
        }

        for (i = 0; i < 12; i++) {
            sum = 0;
            sum2 = 0;
            for (j = 0; j < sortedHarvestAmt_previousYear.length; j++) {
                a = new Date(sortedHarvestAmt_previousYear[j][0]);
                b = a.getMonth();
                if (b === i) {
                    sum += sortedHarvestAmt_previousYear[j][1];
                    sum2 += sortedLandQty_previousYear[j][1];
                }
            }
            harvestVolume_previousYear.push([sum]);
            plantedAcerage_previousYear.push([sum2]);
            sum = 0;
            sum2 = 0;
        }

        for (i = 0; i < 12; i++) {
            sum = 0;
            sum2 = 0;
            for (j = 0; j < sortedHarvestAmt_selectedYear.length; j++) {
                a = new Date(sortedHarvestAmt_selectedYear[j][0]);
                b = a.getMonth();
                if (b === i) {
                    sum += sortedHarvestAmt_selectedYear[j][1];
                    sum2 += sortedLandQty_selectedYear[j][1];
                }
            }
            harvestVolume_selectedYear.push([sum]);
            plantedAcerage_selectedYear.push([sum2]);
            sum = 0;
            sum2 = 0;
        }

        $(function () {
            $('#harvestContainer').highcharts({
                chart: {
                    zoomType: 'xy'
                },
                title: {
                    text: 'Aggregate Monthly Harvest Yield and Used Acreage'
                },
                xAxis: [{
                    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                        'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
                }],
                yAxis: [{ // Primary yAxis
                    labels: {
                        format: '{value} Lb'
                    },
                    min: 0,
                    title: {
                        text: 'Yield in Lb'
                    }
                }, { // Secondary yAxis
                    min: 0,
                    title: {
                        text: 'Acreage'
                    },
                    labels: {
                        format: '{value} acres'
                    },
                    opposite: true
                }],
                tooltip: {
                    shared: true
                },
                legend: {
                    layout: 'vertical',
                    align: 'left',
                    x: 120,
                    verticalAlign: 'top',
                    y: 100,
                    floating: true
                },
                series: [{
                    name: 'Acres',
                    type: 'column',
                    yAxis: 1,
                    data: [],
                    tooltip: {
                        valueSuffix: ' acres'
                    }

                }, {
                    name: 'Yield',
                    type: 'spline',
                    data: [],
                    tooltip: {
                        valueSuffix: ' KG'
                    }
                }]
            });
            // console.log(harvestVolume);
            //    console.log(plantedAcerage);
            chart = $('#harvestContainer').highcharts();
            chart.series[0].setData(plantedAcerage_selectedYear);
            chart.series[1].setData(harvestVolume_selectedYear);
        });

        $(function () {
            $('#lineContainer').highcharts({

                title: {
                    text: 'Volume comparison between ' + moment(dateFilter_prevEnd).year() + ' & ' + moment(dateFilter_end).year()
                },

                yAxis: {
                    min: 0
                },
                xAxis: {
                    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul',
                                     'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
                },

                tooltip: {
                    formatter: function () {
                        var tooltipOutput = '<b>' + this.x + '</b>';
                        var percentageChange;

                        $.each(this.points, function () {
                            tooltipOutput += '<br/>' + this.series.name + ': ' +
                                this.y + 'lbs';
                        });

                        if (this.points[0].y === 0 && this.points[1].y !== 0) {
                            percentageChange = this.points[1].y;
                            tooltipOutput += '<br/>' + 'Decrease of: ' + percentageChange + 'lbs' + '</b>';
                            return tooltipOutput;
                        } else if (this.points[0].y === 0 && this.points[1].y === 0) {
                            percentageChange = 0;
                        } else {
                            percentageChange = (((this.points[1].y - this.points[0].y) / this.points[0].y) * 100.00).toFixed(2);
                        }
                        if ((isNaN(percentageChange))) {
                            percentageChange = 0;
                        }
                        tooltipOutput += '<br/>' + 'Percentage Change: ' + percentageChange + '%' + '</b>';

                        return tooltipOutput;
                    },
                    shared: true
                },

                series: [{
                    name: moment(dateFilter_end).year(),
                    data: []
                }, {
                    name: moment(dateFilter_prevEnd).year(),
                    data: []
                }]
            });
            chart = $('#lineContainer').highcharts();
            chart.series[0].setData(harvestVolume_previousYear);
            chart.series[1].setData(harvestVolume_selectedYear);
        });

        $(function () {
            $('#lineContainer2').highcharts({

                title: {
                    text: 'Planted acreage comparison between ' + moment(dateFilter_prevEnd).year() + ' & ' + moment(dateFilter_end).year()
                },

                yAxis: {
                    min: 0
                },
                xAxis: {
                    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul',
                                     'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
                },

                tooltip: {
                    formatter: function () {
                        var tooltipOutput = '<b>' + this.x + '</b>';
                        var percentageChange;

                        $.each(this.points, function () {
                            tooltipOutput += '<br/>' + this.series.name + ': ' +
                                this.y + ' Acres';
                        });

                        if (this.points[0].y === 0 && this.points[1].y !== 0) {
                            percentageChange = this.points[1].y;
                            tooltipOutput += '<br/>' + 'Decrease of: ' + percentageChange + ' Acres' + '</b>';
                            return tooltipOutput;
                        } else if (this.points[0].y === 0 && this.points[1].y === 0) {
                            percentageChange = 0;
                        } else {
                            percentageChange = (((this.points[1].y - this.points[0].y) / this.points[0].y) * 100.00).toFixed(2);
                        }
                        if ((isNaN(percentageChange))) {
                            percentageChange = 0;
                        }
                        tooltipOutput += '<br/>' + 'Percentage Change: ' + percentageChange + '%' + '</b>';

                        return tooltipOutput;
                    },
                    shared: true
                },

                series: [{
                    name: moment(dateFilter_end).year(),
                    data: []
                }, {
                    name: moment(dateFilter_prevEnd).year(),
                    data: []
                }]
            });
            chart = $('#lineContainer2').highcharts();
            chart.series[0].setData(plantedAcerage_previousYear);
            chart.series[1].setData(plantedAcerage_selectedYear);
        });
    }
}