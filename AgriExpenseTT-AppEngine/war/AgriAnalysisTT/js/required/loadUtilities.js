/*global define */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global $:false, jQuery:false, _:false, gapi:false, moment:false*/

/* BOOTSTRAP SLIDER */
$(function () {
    'use strict';
    $('.slider').slider();
});

/*Global variables, used to send date ranges for app engine query*/
var dateFilter_start, dateFilter_end;

// Filters to get the previous year where required
var dateFilter_prevStart, dateFilter_prevEnd;

$(function () {
    'use strict';
    $('#reportrange').daterangepicker({
        minDate: '2010-01-01',
        ranges: {
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
            'Last 7 Days': [moment().subtract('days', 6), moment()],
            'Last 30 Days': [moment().subtract('days', 29), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')],
            'Last Year': [moment().subtract('month', 12).startOf('month'), moment()],
            'Last Two Years': [moment().subtract('month', 24).startOf('month'), moment()],
            'All Recorded Years': ['2010-01-01', moment()]
        },
        startDate: moment().subtract('days', 29),
        endDate: moment()
    }, function (start, end) {
        $('#reportrange').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
        var storeStartDate = start.format('DD,MM,YYYY'),
            storeEndDate = end.format('DD,MM,YYYY');
        // http://stackoverflow.com/questions/9873197/convert-date-to-timestamp-in-javascript
        storeStartDate = storeStartDate.split(",");
        storeEndDate = storeEndDate.split(",");
        dateFilter_start = storeStartDate[1] + "/" + storeStartDate[0] + "/" + storeStartDate[2];
        dateFilter_end = storeEndDate[1] + "/" + storeEndDate[0] + "/" + storeEndDate[2];
        dateFilter_start = new Date(dateFilter_start).getTime();
        dateFilter_end = new Date(dateFilter_end).getTime();
    });
});

$(function () {
    'use strict';
    var i;
    for (i = new Date().getFullYear(); i > 2010; i--) {
        $('#yearpicker').append($('<option />').val(i).html(i));
    }
});