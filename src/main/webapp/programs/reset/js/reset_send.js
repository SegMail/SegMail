var FormResetSend;

var initResetSend = function() {
    FormReset = $('#FormResetSend').validate({
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

function reset(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            blockRefresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            blockRefresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            break;
    }
};

var blockRefresh = function(block) {
           
    if(!block.hasClass("block-refreshing")){
        block.append('<div class="spinner-overlay" ></div><p class="spinner" ></p>');
        
        block.find(".spinner-overlay")
                .width(block.innerWidth())
                .height(block.innerHeight())
        block.addClass("block-refreshing");
    }else{
        block.find(".spinner-overlay").remove();
        block.find(".spinner").remove();
        block.removeClass("block-refreshing");
    }    
}

$(document).ready(function(){
    initResetSend();
});