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
    if ($(document).has('#listIntro').length) {
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
    initRedirect();
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

var FormListEmail;

var initRedirect = function() {
    FormListEmail = $('#FormListEmail').validate({
        rules: {
            'FormListEmail:confirmRedirect' : {
                url : true
            },
            'FormListEmail:welcomeRedirect' : {
                url : true
            },
            'FormListEmail:unsubscribeRedirect' : {
                url : true
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
        mojarra.ab(this, //this here should be the original checkbox, not the icheck checkbox
            event,'valueChange','@form','@form FormEditListHeader',{'onevent':updateDatasource,'delay':'0'}); 
    })
}

var initToggleInfo = function() {
    $('#hide_all_info').click(function(){
        toggleDatasourceInfo();
    });
    toggleDatasourceInfo();
}

var toggleDatasourceInfo = function() {
    var block = $('#hide_all_info');

    if(block.hasClass("showing")) {
        block.find("h2.h2-hide").hide();
        block.find("h2.h2-show").show();
        block.removeClass("showing").addClass("hiding");
        $('.expandable').slideToggle();
    } else {
        block.find("h2.h2-hide").show();
        block.find("h2.h2-show").hide();
        block.removeClass("hiding").addClass("showing");
        $('.expandable').slideToggle();
    }
};

var showNoty = function() {
    var messages = $('#FormListDatasource').find('input[data-message="message"]');
    messages.each(function(){
        noty({
            text : $(this).val(),
            layout : 'topCenter',
            type : function(ordinal){
                switch(ordinal){
                    case '0' : return 'info';
                    case '1' : return 'warning';
                    case '2' : return 'error';
                    case '3' : return 'success';
                    default: return 'alert';
                }
            }($(this).attr('data-severity')),
            timeout: function(ordinal) {
                switch(ordinal){
                    case '0' : return true;
                    case '1' : return false;
                    case '2' : return false;
                    case '3' : return 3000;
                    default: return true;
                }
            }($(this).attr('data-severity'))
        });
    })
}

function updateDatasource(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            //block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            initICheckMand();
            showNoty(block);
            updateSegmailIp();
            break;
    }
};

var updateSegmailIp = function() {
    $.getJSON('https://jsonip.com/?callback=?', function(response){
        $('.ip_address').html(response.ip);
    });
}

var addSchemeToUrl = function() {
    $('input.url').each(function(){
        var address = $(this).val();
        if(address && !address.startsWith('http')) {
            $(this).val('http://'+address);
        }
    });
}

var passTabBackToJSF = function() {
    $('#list_editing_block .nav-tabs li a').click(function(){
        var id = $(this).data('id');
        $('#activeTab').val(id);
    })
}

$(document).ready(function(){
    initICheckMand();
    initToggleInfo();
    updateSegmailIp();
    passTabBackToJSF();
});

$(window).load(function(){
    
})