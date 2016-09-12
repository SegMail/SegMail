//Functions and objects
$.extend($.summernote.plugins, {
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


var refresh_summernote = function () {
    $('textarea.editor').summernote({
        height: 260, //try the CSS flex approach next time
        toolbar: [
            ["style", ["style"]],
             ["font", ["bold", "italic", "underline"]],
             ["test", ["picture", "link"]],
             ["para", ["ol", "ul", "paragraph", "height"]],
             ["misc", ["codeview", "help"]],
            ["mybutton", ["MailMerge"]]
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

// Events
var onEditorChange = function () {
    renderEverything();
}

var onSave = function () {
    //Block button
    $('#saveButton').prop('disabled', true);
    renderEverything();
    //saveAutoemail();
    callWS(
            'saveAutoemail', {
                'body': $('#editor').text(),
                'bodyProcessed': $('#processedContent').val()
            }, function (result) {
        $('#saveResults').html('Saved at ' + result); //Don't know how it will look like yet
        $('#saveButton').prop('disabled', false);
    }, function (error) {
        $('#saveResults').html('Error: ' + error); //Don't know how it will look like yet
        $('#saveButton').prop('disabled', false);
    });
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
            + $('#editor-panel').height();
    +parseInt($('#editor-panel').css('margin-bottom').replace("px", ""))
            + parseInt($('#editor-panel').css('border-bottom-width').replace("px", ""));
    var previewHeight = editorBottom - $('#preview-panel').offset().top;
    $('#preview-panel').height(previewHeight);
};

var mailmergeWSCache = {};

var renderMailmergeLinkHelper = function(token,result) {
    var jsonObj = JSON.parse(result);
    var count = $('#preview a.' + token).size();
    $('#preview a.' + token).each(function(){
        var link = $(this);
        link.attr('href', jsonObj['url']);
        link.html(jsonObj["name"]);
        if (!--count)
            $('#processedContent').val($('#preview').html());
    });
}
var renderMailmergeLink = function(label,timeout) {
    var token = md5(label);
    
    setTimeout(function () {
        if(mailmergeWSCache[token]){
            renderMailmergeLinkHelper(token,mailmergeWSCache[token]);
            return;
        }
        callWS('createSystemMailmergeTestLink',
            {label: label},
            function (result) {
                renderMailmergeLinkHelper(token,result);
                //Cache the results
                mailmergeWSCache[token] = result;
            },
            function (code, error, message) {
                $('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
            }
        );
    }, timeout);
};

var renderMailmergeTagHelper = function(token,result) {
    var count = $('#preview span.' + token).size();
    $('#preview span.' + token).each(function(){
        $(this).html(result);
        if (!--count)
            $('#processedContent').val($('#preview').html());
    });
}

var renderMailmergeTag = function(label,timeout) {
    var token = md5(label);
    
    setTimeout(function () {
        if(mailmergeWSCache[token]){
            renderMailmergeTagHelper(token,mailmergeWSCache[token]);
            return;
        }
        callWS('createSubscriberMailmergeTestValue',
            {label: label},
            function (result) {
                renderMailmergeTagHelper(token,result);
                //Cache the results
                mailmergeWSCache[token] = result;
            },
            function (code, error, message) {
                $('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
            }
        );
    }, timeout);
};

var replaceMailmergeTags = function(tags,timeout) {
    setTimeout(function() {
        var content = $('#preview').html();
        for(var i=0; i<tags.length; i++) {
            var label = tags[i];
            var token = md5(label);
            var mmTag = '<span class="' + token + '"></span>';
            content = content.replace(RegExp(label,'g'), mmTag);
        }
        $('#preview').html(content);
    },timeout);
}

var replaceMailmergeLinks = function(links,timeout) {
    setTimeout(function() {
        var content = $('#preview').html();
        for(var i=0; i<links.length; i++) {
            var label = links[i];
            var token = md5(label);
            var mmLink = '<a target="_blank" class="' + token + '"></a>';
            content = content.replace(RegExp(label,'g'), mmLink);
        }
        $('#preview').html(content);
    },timeout);
}

var renderMailmergeTags = function(tags,timeout) {
    //Call WS and render the actual values
    for(var i=0; i<tags.length; i++) {
        renderMailmergeTag(tags[i],timeout);
    }
}

var renderMailmergeLinks = function(tags,timeout) {
    //Call WS and render the actual values
    for(var i=0; i<tags.length; i++) {
        renderMailmergeLink(tags[i],timeout);
    }
}

var renderEverything = function () {
    renderPreview(0);
    replaceMailmergeTags(mailmergeTagsSubscriber,50);
    replaceMailmergeLinks(mailmergeLinks,50);
    renderMailmergeTags(mailmergeTagsSubscriber,50);
    renderMailmergeLinks(mailmergeLinks,50);
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
    refresh_summernote();
    adjustPreviewPanelHeight();
    modifyDomToGeneratePreview();
});