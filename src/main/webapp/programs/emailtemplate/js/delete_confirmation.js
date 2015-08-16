var DeleteConfirm = (function () {

    var container;
    var mainContainer;
    var deleteContainer;

    return {
        init: function (id) {
            this.container = $('#' + id + '.delete-confirm-container');
            this.mainContainer = $('#' + id + ' div[data-role="main"]');
            this.deleteContainer = $('#' + id + ' div[data-role="delete"]');
            /*this.mainContainer.css("display", "block");
            this.deleteContainer.css("display", "none");*/
        },
        show: function () {
            this.mainContainer.show();
            this.deleteContainer.hide();
        },
        toggle: function () {
            this.mainContainer.toggle();
            this.deleteContainer.toggle();
        }
    };
});


$(document).ready(function () {

    var deleteTemplate = new DeleteConfirm();
    deleteTemplate.init('delete-template-1');
});