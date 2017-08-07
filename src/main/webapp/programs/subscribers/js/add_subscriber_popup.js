
var validateListFields = function() {
    var submitForm = true;
    var count = $('#FormAddSubscriber input').size();
    $('#FormAddSubscriber input').each(function(i,o){
        var hasError = false;
        if($(this).prop('required') === true && !$(this).val()) {
            hasError = true;
        }
        if($(this).prop('type') === 'email') {
            //How to validate email?
        }
        
        if(hasError) {
            $(this).parents('.form-group').addClass('has-error');
            $(this).parents('.form-group').removeClass('has-success');
            submitForm = false;
        } else {
            $(this).parents('.form-group').addClass('has-success');
            $(this).parents('.form-group').removeClass('has-error');
        }
        
        if(!--count) {
            return submitForm;
        }
    })
}

var initValidationEngine = function() {
    $("#FormAddSubscriber").validationEngine('attach',{promptPosition : "topRight"});
}

function refreshAddSubsc(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $("#FormAddSubscriber .modal-dialog");
    
    //if(!validateListFields())
    //    return;
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            initAddSubsc();
            refresh_select2();
            break;
    }
};

var FormAddSubscriber;

var initAddSubsc = function() {
    FormAddSubscriber = $('#FormAddSubscriber').validate({
        
        errorPlacement: function (error, element) {
            element.parents('.form-group').addClass('has-error');
            error.insertAfter(element);

        },
        highlight: function (element, errorClass) {
            
        },
        success : function (label, element) {
            $(element).parents('.form-group').removeClass('has-error');
            $(element).siblings('label').remove();
        }
    });
    $('#FormAddSubscriber .required').each(function(){
        $(this).rules('add', {
            required : true
        })
    });
    $('#FormAddSubscriber .email').each(function(){
        $(this).rules('add', {
            email : true
        })
    });
};

$(document).ready(function(){
    initAddSubsc();
})