/*global define */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global $:false, jQuery:false, _:false, gapi:false*/

function showSuccess() {
    'use strict';
    $("#alertArea").hide();
    $("#successArea").show();
    $("#successArea").fadeOut(1000);
}

function showError(errorHtml) {
    'use strict';
    $("#alertArea").removeClass('alert-error alert-info alert-success').addClass('alert-error');
    $("#alertContentArea").html(errorHtml);
    $("#alertArea").show();
}

function showInfo(infoHtml) {
    'use strict';
    $("#alertArea").removeClass('alert-error alert-info alert-success').addClass('alert-info');
    $("#alertContentArea").html(infoHtml);
    $("#alertArea").show();
}

// This method loads the Endpoint libraries
function loadGapi() {
    'use strict';
    gapi.client.load('cycleendpoint', 'v1', function () {
        console.log("Cycle API Loaded");
    });
    gapi.client.load('rpurchaseendpoint', 'v1', function () {
        console.log("RPurchase API Loaded");
    });
}

// Function for checking error responses; it correctly sanitizes error messages
// so that they are safe to display in the UI
function checkErrorResponse(result) {
    'use strict';
    if (result && result.error) {
        var safeErrorHtml = $('<div/>').text(result.error.message).html();
        return {
            isError: true,
            errorMessage: safeErrorHtml
        };
    }

    return {
        isError: false
    };
}