//Functions and objects
/*$.extend($.summernote.plugins, {
    MailMerge: function (context) {
        var ui = $.summernote.ui;
        var tags = context.options.MailMerge.tags;

        context.memo('button.MailMerge', function () {
            // create button
            var button = ui.buttonGroup([
                ui.button({
                    className: 'dropdown-toggle',
                    contents: ' Mailmerge tags <span class="caret"></span>',
                    //tooltip: 'Click here to select the mail merge tag to insert',
                    data: {
                        toggle: 'dropdown'
                    }
                }),
                ui.dropdown({
                    className: 'dropdown-template',
                    items: tags,
                    click: function (event) {
                        var $button = $(event.target);
                        var value = $button.data('value');
                        //var path = context.options.mailmerge.path + '/' + value + '.html';
                        var node = document.createElement('span');
                        node.innerHTML = value;
                        context.invoke('editor.insertNode', node);
                    }
                })
            ]);

            return button.render();   // return button as jquery object 
        });
    }
});
*/

var refresh_summernote = function (selector) {
    $(selector).summernote({
        height: 260, //try the CSS flex approach next time
        toolbar: [
             ["font", ["bold", "italic", "underline","style"]],
             ["test", ["picture", "link"]],
             ["para", ["ol", "ul", "paragraph", "height"]],
             ["misc", ["codeview", "help", "MailMerge"]]
        ],
        MailMerge: {
            tags: function() {
                var allTagsAndLinks = [];
                for(var key in mailmergeTagsSubscriber) {
                    if (mailmergeTagsSubscriber.hasOwnProperty(key)) {
                        allTagsAndLinks.push(key);
                    }
                }
                for(var key in mailmergeLinks) {
                    if (mailmergeLinks.hasOwnProperty(key)) {
                        allTagsAndLinks.push(key);
                    }
                }
                return allTagsAndLinks;
            }()
        }
    });
    observeDOM(document.getElementsByClassName('note-editable')[0], function () {
        onEditorChange();
    });
}

// Events
var onEditorChange = function () {
    renderEverything();
}

var onSave = function () {
    //Block button
    //$('#saveButton').prop('disabled', true);
    block_refresh($('#editor-panel'));
    renderEverything(); //This will get called later than the below code 
    setTimeout(function(){
        var subject = $('#subject').val();
        var body = $('#editor').text();
        var bodyProcessed = $('#processedContent').val();
        callWS(
                WSAutoresponderEndpoint,
                'saveAutoemail', 
                'http://webservice.autoresponder.program.segmail/',
                {
                    'subject' : subject,
                    'body': body,
                    //'bodyProcessed': bodyProcessed
                }, function (result) {
            //$('#saveResults').html('Saved at ' + result); //Don't know how it will look like yet
            //$('#saveButton').prop('disabled', false);
            block_refresh($('#editor-panel'));
            noty({
                text : 'Email saved at '+result,
                layout : 'topCenter',
                type : 'success',
                timeout : true
            }).setTimeout(2000);
        }, function (error) {
            //$('#saveResults').html('Error: ' + error); //Don't know how it will look like yet
            //$('#saveButton').prop('disabled', false);
            block_refresh($('#editor-panel'));
            noty({
                text : error,
                layout : 'topCenter',
                type : 'danger'
            });
        });
    },100);
    
};

// Helper functions
var toggleMenu = function () {
    if ($(document).has('#FormEditExistingTemplate').length) {
        page_navigation();
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
    processMailmergeNoWS(0);
    /*processMailmerge('#preview','#processedContent',mailmergeLinks,mailmergeTagsSubscriber,
    function(){//successCallback
        
    },
    function(){//errorCallback
        $('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
    });*/
}

var processMailmergeNoWS = function(timeout) {
    setTimeout(function(){
        var content = $('#preview').html();
        for(var key in mailmergeTagsSubscriber) {
            if(mailmergeTagsSubscriber.hasOwnProperty(key)) {
                var subscVal = randomSubscriber[mailmergeTagsSubscriber[key]];
                if(!subscVal)
                    continue;
                content = content.replace(RegExp(key,'g'),subscVal);
            }
        }
        for(var key in mailmergeLinks) {
            if(mailmergeLinks.hasOwnProperty(key)) {
                if(!mailmergeLinks[key][1] || !mailmergeLinks[key][0])
                    continue;
                var url = '<a target="_blank" href="'+mailmergeLinks[key][1]+'">'+mailmergeLinks[key][0]+'</a>';
                content = content.replace(RegExp(key,'g'),url);
            }
        }
        $('#preview').html(content);
    },timeout);
    
}


var modifyDomToGeneratePreview = function () {
    var randomNum = Math.round(Math.random() * 100000);
    $('.note-editable').append('<div id=modifyDomToGeneratePreview' + randomNum + '></div>');
    $('#modifyDomToGeneratePreview' + randomNum).remove();
};

//Loader
$(document).ready(function () {
    if ($('#editor-panel').size() <= 0)
        return;

    toggleMenu();
    refresh_summernote('textarea.editor');
    adjustPreviewPanelHeight();
    modifyDomToGeneratePreview();
});