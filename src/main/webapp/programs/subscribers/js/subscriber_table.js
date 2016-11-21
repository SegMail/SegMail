$(document).ready(function () {
    //Datatables
    //.$('.sortable').dataTable();
    initSubscriberTable();
});

var initSubscriberTable = function () {
    if (!$.fn.dataTable.isDataTable('.sortable')) {
        $('.sortable').dataTable({
            //'destroy': false,
            //'filter': false,
            //'paging': true,
            'searching': false,
            'info': false
        });
    }
}