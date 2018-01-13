$(document).ready(function () {
    //Datatables
    //.$('.sortable').dataTable();
    //initSubscriberTable();
    updateAddSubsriberButtons();
    initExpandTab();
    toggleMenu();
    initCheckbox();
});

var initSubscriberTable = function () {
    $('.sortable').dataTable({
        'serverSide' : true,
        //'destroy': false,
        'filter': true,
        'paging': true,
        'searching': false,
        'info': false
    });
};

var toggleCheckbox = function(checkbox) {
    var checkboxes = $('input.checkbox');
    var checked = $(checkbox).prop('checked');
    if(checked) {
        checkboxes.prop('checked',true);
    } else {
        checkboxes.prop('checked',false);
    }
    checkboxes.each(function(n,item){
        switchCheckbox($(item));
    });
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

var toggleMenu = function () {
    if ($(document).has('#no_subscribers').length) {
        page_navigation();
    }
}

var initCheckbox = function() {
    $('#checkedIds').val('');
    $('input.checkbox').change(function(){
        switchCheckbox($(this));
    })
}

var switchCheckbox = function(checkbox) {
    var id = $(checkbox).data('id');
    var checked = $(checkbox).prop('checked');
    var checkedIdString = $('#checkedIds').val();
    var checkedIds = (checkedIdString.length > 0) ? checkedIdString.split(',') : [];
    var index = checkedIds.indexOf(id + "");
    if(index >= 0 && !checked) {
        checkedIds.splice(index,1);
    } else if (index < 0 && checked) {
        checkedIds.push(id);
    }
    $('#checkedIds').val(checkedIds.join());
}