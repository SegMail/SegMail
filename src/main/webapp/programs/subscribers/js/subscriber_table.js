$(document).ready(function () {
    //Datatables
    //.$('.sortable').dataTable();
    //initSubscriberTable();
    updateAddSubsriberButtons();
    initExpandTab();
    initFieldForms();
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
    //var trigCheckbox = $('input.trigger-check');
    //Default action is to check all
    //If at least 1 box is unchecked, check all
    //Else, uncheck all (if all boxes are checked)
    var checkAll = false;
    checkboxes.each(function(i){
        if(!this.checked)
            checkAll = true;
    });
    if(checkAll) {
        checkboxes.prop('checked',true);
    } else {
        checkboxes.prop('checked',false);
    }
};

var refresh_select2 = function() {
    var select = $('body').find("select.select2");
    select.select2("destroy").select2();
};

var refresh_blocks = function() {
    block_refresh($('#FormSubscriptionLists').parents('.block'));
    block_refresh($('#FormSubscriberStatus').parents('.block'));
    $('#FormSubscriberTable').find('.block').each(function(){
        block_refresh($(this));
    })
}

var updateAddSubsriberButtons = function() {
    var select = $('#FormSubscriptionLists select.select2');
    if($('#FormSubscriptionLists select.select2').val()) {
        $('#addSubscButtons').show();
    } else {
        $('#addSubscButtons').hide();
    }
}

function refresh(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            refresh_blocks();
            break;

        case "complete": // This is called right after ajax response is received.
            refresh_blocks();
            break;

        case "success": // This is called when ajax response is successfully processed.
            refresh_select2();
            updateAddSubsriberButtons();
            initExpandTab();
            initFieldForms();
            break;
    }
};



var initExpandTab = function(){

    $(".faq .item .title").click(function(){
        var text = $(this).parent('.item').find('.text');
        
        if(text.is(':visible')) {
            text.slideUp(200,function(){
                $(".page-content").mCustomScrollbar("update");
            });
            $(this).removeClass('expanded');
        } else {
            text.slideDown(200,function(){
                $(".page-content").mCustomScrollbar("update");
            });
            $(this).addClass('expanded');
        }
    });

    $("#faqSearch").click(function(){
        var keyword = $(".faqSearchKeyword").val();
        
        if(keyword.length >= 3){
            $(".faq").find('.text').slideUp(200,function(){
                $(".page-content").mCustomScrollbar("update");
            });
            $("#faqSearchResult").html("");
            $(".faq").removeHighlight();
            
            var items = $(".faq .text:containsi('"+keyword+"')");
            items.highlight(keyword);
            items.slideDown(200,function(){
                $(".page-content").mCustomScrollbar("update");
            });
            $("#faqSearchResult").html("<span class='text-success'>Found in "+items.length+" answers</span>");            
            
        }else
            $("#faqSearchResult").html("<span class='text-error'>Minimum 3 chars</span>");
                 
    });
    
    $("#faqListController a").click(function(){
        var open = $(this).attr('href');
        $(open).find('.text').slideDown(200,function(){
            $(".page-content").mCustomScrollbar("update");
        });
        
    });
    
    $("#faqOpenAll").click(function(){
        $(".faq").find('.text').slideDown(200,function(){
            $(".page-content").mCustomScrollbar("update");
        });        
    });
    
    $("#faqCloseAll").click(function(){
        $(".faq").find('.text').slideUp(function(){
            $(".page-content").mCustomScrollbar("update");
        });        
    });
    
    $("#faqRemoveHighlights").click(function(){
        $(".faq").removeHighlight();
    });
    
}

var updateFieldValue = function(id,key,value) {
    $.ajax({
        type: 'POST',
        url: CONTEXT_PATH+'/rest/subscriber/field/update/' + id + '/' + key + '/' + value,
        //dataType: 'json',
        /*data: {
            'id':id,
            'key':key,
            'value':value
        }*/
        success: function (data) {
            //console.log(data);
            if(data.result === 'success') {
                noty({
                    text: 'Field updated.',
                    layout : 'topCenter',
                    type : 'success',
                    timeout : 3000
                });
                return;
            }
            noty({
                text: 'Error: ' + data.error,
                layout : 'topCenter',
                type : 'error',
                timeout : false
            });
        },
        error: function(error) {
            console.log(error);
        }
    })
}

var initFieldForms = function() {
    // init Save buttons
    $('button.save').each(function(i){
        var button = $(this);
        var id = button.data('id');
        var key = button.data('key');
        button.click(function(){
            var value = $('input[data-id="'+id+'"][data-key="'+key+'"]').val(); //Call this in click() so that it will capture the latest value, not the initial value
            updateFieldValue(id,key,value);
        })
    })
    
    // init Reset buttons
    $('button.reset').each(function(){
        var button = $(this);
        var id = button.data('id');
        var key = button.data('key');
        button.click(function(){
            var defValue = $('input[data-id="'+id+'"][data-key="'+key+'"]').prop("defaultValue");
            $('input[data-id="'+id+'"][data-key="'+key+'"]').val(defValue);
        })
    })
}