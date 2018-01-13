
var toggleMenu = function () {
    if ($(document).has('#FormSubscriberView').length) {
        page_navigation();
    }
};

var updateFieldValue = function(id,key,value,successCB,errorCB) {
    $.ajax({
        type: 'POST',
        url: CONTEXT_PATH+'/rest/subscriber/field/update/' + id + '/' + key + '?value=' + value,
        success: function (data) {
            //console.log(data);
            if(data.result === 'success') {
                $.noty.closeAll(); 
                noty({
                    text: 'Field updated.',
                    layout : 'topCenter',
                    type : 'success',
                    timeout : 3000
                });
                if(successCB)
                    successCB(data);
            } else {
                noty({
                    text: 'Error: ' + data.result,
                    layout : 'topCenter',
                    type : 'error',
                    timeout : false
                });
                if(errorCB)
                    errorCB(data);
            }
        },
        error: function(error) {
            console.log(error);
            if(error.status === 401) {
                noty({
                    text: 'Your session has timed out, please refresh this page',
                    layout : 'topCenter',
                    type : 'warning',
                    timeout : false
                });
            } else {
                noty({
                    text: error,
                    layout : 'topCenter',
                    type : 'error',
                    timeout : false
                });
            }
        }
    })
}

var initFieldActions = function() {
    // init Save buttons
    $('button.save-field').each(function(i){
        var button = $(this);
        var id = button.data('id');
        var key = button.data('key');
        button.click(function(){
            var buttonCalled = $(this);
            var value = $('input[data-id="'+id+'"][data-key="'+key+'"]').val(); //Call this in click() so that it will capture the latest value, not the initial value
            updateFieldValue(id,key,value,function(result){
                $('span.field-value-display[data-id="'+id+'"][data-key="'+key+'"]').html(result.value);
                list_item_controls($(buttonCalled).parents(".list-item"),1);
                if(result.date_changed) {
                    $('span.field-value-date[data-id="'+id+'"][data-key="'+key+'"]').html(result.date_changed);
                }
            });
        })
    })
    
    // init Delete buttons
    $('a.delete-field').each(function(){
        var button = $(this);
        var id = button.data('id');
        var key = button.data('key');
        button.click(function(){
            $('#fieldOwnerId').val(id);
            $('#fieldKey').val(key);
        })
    })
}

var initSubscriptionActions = function() {
    $('a.resend-email').each(function(){
        var button = $(this);
        var source = button.data('source-id');
        var target = button.data('target-id');
        var numResends = button.data('sent');
        button.click(function(){
            $('#resend-sub-source').val(source);
            $('#resend-sub-target').val(target);
            $('#resend-message').html(
                'You have already sent out ' + numResends + ' email' 
                + (numResends > 1 ? 's' : '') + '.'
            );
        })
    });
    
    $('a.remove-sub').each(function(){
        var button = $(this);
        var unsubkey = button.data('unsubkey');
        var listname = button.data('list-name');
        button.click(function(){
            $('#remove-unsubkey').val(unsubkey);
            $('#remove-list-name').html(listname);
        });
    });
    
    $('a.restore-sub').each(function(){
        var button = $(this);
        var unsubkey = button.data('unsubkey');
        var listname = button.data('list-name');
        button.click(function(){
            $('#restore-unsubkey').val(unsubkey);
            $('#restore-list-name').html(listname);
        });
    });
    
};

function refresh(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents('.modal-content');
    
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

var refresh_summernote = function (selector) {
    if($(selector).length <= 0)
        return;
    $(selector).summernote({
        height: 300,
        toolbar: [
             ["font", ["bold", "italic", "underline","style"]],
             ["test", ["picture", "link"]],
             ["para", ["ol", "ul", "paragraph"]],
             ["misc", ["help", "MailMerge"]]
        ],
        MailMerge: {
            tags: function() {
                var allTagsAndLinks = [];
                for(var key in mailmergeTagsSubscriber) {
                    if (mailmergeTagsSubscriber.hasOwnProperty(key)) {
                        allTagsAndLinks.push(mailmergeTagsSubscriber[key]);
                    }
                }
                return allTagsAndLinks;
            }()
        }
    });
    /*observeDOM(document.getElementsByClassName('note-editable')[0], function () {
        onEditorChange();
    });
    */
}

var initSendSingleEmail = function(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $('#FormSendSingleEmail .modal-dialog');
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            refresh_summernote('textarea.editor');
            break;
    }
}

$(document).ready(function(){
    toggleMenu();
    initFieldActions();
    initSubscriptionActions();
    //refresh_summernote('textarea.editor')
})