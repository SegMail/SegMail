var SUMMERNOTE_HEIGHT = 280;
var PREVIEW_HEIGHT = 450;
var PREVIEW_WIDTH = 420;

var refresh_summernote = function (selector) {
    if($(selector).length <= 0)
        return;
    
    $(selector).summernote({
        height: SUMMERNOTE_HEIGHT, //try the CSS flex approach next time
        toolbar: [
             ["font", ["bold", "italic", "underline","style"]],
             ["test", ["picture", "link"]],
             ["para", ["ol", "ul", "paragraph", "height"]],
             ["misc", ["codeview", "help", "MailMerge"]]
        ],
        MailMerge: {
            tags: function() {
                var allTagsAndLinks = [];
                mailmergeTagsSubscriber.forEach(function(item){
                    allTagsAndLinks.push(item);
                });
                mailmergeLinks.forEach(function(item){
                    allTagsAndLinks.push(item);
                });
                
                return allTagsAndLinks;
            }()
        }
    });
    observeDOM(document.getElementsByClassName('note-editable')[0], function () {
        onEditorChange();
    });
}

var onEditorChange = function () {
    renderEverything();
}

var renderEverything = function () {
    renderPreview(0);
    processMailmerge('#preview','#processedContent',mailmergeLinks,mailmergeTagsSubscriber,
    function(){//successCallback
        
    },
    function(){//errorCallback
        $('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
    });
}

/**
 * No WS calls for this one, just JSF ajax.
 * 
 * @returns {undefined}
 */
var onSave = function (data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            reapply_textarea('editor');
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            refresh_summernote('textarea.editor');
            modifyDomToGeneratePreview();
            noty({
                text : 'Email saved.',
                type : 'success'
            });
            break;
        case "error":
            noty({
                text : 'Error.',
                type : 'danger'
            });
    }
};

var renderPreview = function (timeout) {
    //Copy the html over from 
    setTimeout(function () {
        //Copy summernote content back to textarea
        reapply_textarea('editor');
        $('#preview').html($('.note-editable').html());

        //Get ratios
        var scaleY = $('#preview-panel').height() / $('#preview').height();
        var scaleX = $('#preview-panel').width() / largestWidth('#preview');//$('#preview').width();
        //Transform
        var scaleYTransform = Math.min(1, scaleY);
        var scaleXTransform = Math.min(1, scaleX);
        $('#preview').css({
            transform: 'scale(' + scaleXTransform + ',' + scaleYTransform + ')',
            'transform-origin': '0 0 0'
        });

    }, timeout);
};

function largestWidth(selector) {
    var maxWidth = 1;
    var widestSpan = null;
    var $element;
    $(selector).find('*').each(function () {
        $element = $(this);
        if ($element.width() > maxWidth) {
            maxWidth = $element.width();
            widestSpan = $element;
        }
    });
    return maxWidth;
}

var adjustPreviewPanelHeight = function () {
    
    if($('#editor-panel').length <= 0)
        return;
    //Listen for resize on the #editor-panel
    //$('.note-editable').resize(function(){ //seems like only for window
    //Adjust heights
    //Get actual heights and widths
    var editorBottom = $('#editor-panel').offset().top
            + $('#editor-panel').height()
            + parseInt($('#editor-panel').css('margin-bottom').replace("px", ""))
            + parseInt($('#editor-panel').css('border-bottom-width').replace("px", ""));
    var previewHeight = editorBottom - $('#preview-panel').offset().top;
    $('#preview-panel').height(previewHeight);
};

var renderEverything = function () {
    renderPreview(0);
    processMailmerge('#preview','#processedContent',mailmergeLinks,mailmergeTagsSubscriber,
    function(){//successCallback
        
    },
    function(){//errorCallback
        $('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
    });
}

var modifyDomToGeneratePreview = function () {
    var randomNum = Math.round(Math.random() * 100000);
    $('.note-editable').append('<div id=modifyDomToGeneratePreview' + randomNum + '></div>');
    $('#modifyDomToGeneratePreview' + randomNum).remove();
};

// Helper functions
var toggleMenu = function () {
    if ($(document).has('#FormEditEmailActivity').length) {
        page_navigation();
    }
};

$(document).ready(function () {
    
    toggleMenu();
    refresh_summernote('textarea.editor');
    adjustPreviewPanelHeight();
    modifyDomToGeneratePreview();
});