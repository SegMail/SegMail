function verify(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".modal-content");

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            
            break;
    }
};

var FormVerifyNewAddress;

var initSignup = function() {
    FormVerifyNewAddress = $('#FormVerifyNewAddress').validate({
        rules: {
            email: {
                required: true,
                email: true
            }
        },
        errorPlacement: function (error, element) {
            element.parents('.form-group').addClass('has-error');
            error.insertBefore(element);

        },
        highlight: function (element, errorClass) {

        },
        success : function (label, element) {
            $(element).parents('.form-group').removeClass('has-error');
            $(element).siblings('label').remove();
        }
    });
};

$(document).ready(function(){
    initSignup();
});