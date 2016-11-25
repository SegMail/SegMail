$(document).ready(function () {
    //Datatables
    //.$('.sortable').dataTable();
    //initSubscriberTable();
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
};

var toggleCheckbox = function() {
    var checkboxes = $('input.checkbox');
    var trigCheckbox = $('input.trigger-check');
    checkboxes.prop('checked',trigCheckbox.prop('checked'));
}