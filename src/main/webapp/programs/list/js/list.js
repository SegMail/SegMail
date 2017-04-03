//var web_service_endpoint = 'WSImportSubscriber';



function saveSettings(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            bindFileInput();
            $('body select.select2').select2();
            initICheckMand();
            break;
    }
}
;

function deleteList(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".modal-dialog");
    //var ajaxloader = document.getElementById("ajaxloader");


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
}

var toggleMenu = function () {
    if ($(document).has('#list_editing_block').length) {
        page_navigation();
    }
};

$(document).ready(function () {
    //Datatables
    $('.sortable').dataTable();
    /*$('.sortable').on('page.dt', function () {
        $('.sortable').row.add({
            "EMAIL": "test"
        }).draw();
    });*/
    toggleMenu();
    
    initSettingsValidation();
});

var FormListSettings;

var initSettingsValidation = function() {
    FormListSettings = $('#FormListSettings').validate({
        rules: {
            'FormListSettings:listname' : {
                required: true
            },
            'FormListSettings:selectVerifiedEmail' : {
                required: true,
                email: true
            },
            'FormListSettings:sendasname' : {
                required: true,
                minlength: 6
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

var initICheckMand = function() {
    gICheck.init('body');
    $('input.mandatory').on('ifChanged',function(event){
        //doing this here because iCheck cannot auto-trigger our input.checkbox's onchange method
        mojarra.ab(this,event,'valueChange','@form','@form FormEditListHeader',{'onevent':saveSettings,'delay':'500'}); 
    })
}

$(document).ready(function(){
    initICheckMand();
})