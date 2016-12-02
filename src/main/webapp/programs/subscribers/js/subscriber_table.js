$(document).ready(function () {
    //Datatables
    //.$('.sortable').dataTable();
    //initSubscriberTable();
});

var initSubscriberTable = function () {
    //if (!$.fn.dataTable.isDataTable('.sortable')) {
        $('.sortable').dataTable({
            'serverSide' : true,
            //'destroy': false,
            'filter': true,
            'paging': true,
            'searching': false,
            'info': false
        });
    //}
};

var toggleCheckbox = function() {
    var checkboxes = $('input.checkbox');
    var trigCheckbox = $('input.trigger-check');
    checkboxes.prop('checked',trigCheckbox.prop('checked'));
};

var refresh_select2 = function() {
    var select = $('body').find("select.select2");
    select.select2("destroy").select2();
};

var refresh_blocks = function() {
    block_refresh($('#FormSubscriptionLists').parents('.block'));
    block_refresh($('#FormSubscriberTable').parents('.block'));
}

function refresh(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            refresh_blocks();
            break;

        case "complete": // This is called right after ajax response is received.

            break;

        case "success": // This is called when ajax response is successfully processed.
            refresh_blocks();
            refresh_select2();
            break;
    }
};