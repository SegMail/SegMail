//Functions and objects
$.extend($.summernote.plugins, {
    MailMerge: function (context) {
        var ui = $.summernote.ui;
        var paramSubcriber = context.options.MailMerge.subscriber;

        context.memo('button.MailMerge', function () {
            // create button
            var button = ui.buttonGroup([
                ui.button({
                    className: 'dropdown-toggle',
                    contents: ' Mailmerge tags <span class="caret"></span>',
                    tooltip: 'Click here to select the mail merge tag to insert',
                    data: {
                        toggle: 'dropdown'
                    }
                }),
                ui.dropdown({
                    className: 'dropdown-template',
                    items: paramSubcriber,
                    click: function (event) {
                        var $button = $(event.target);
                        var value = $button.data('value');
                        //var path = context.options.mailmerge.path + '/' + value + '.html';
                        var node = document.createElement('span');
                        node.innerHTML = value;
                        context.invoke('editor.insertNode', node);
                    }
                })
            ])

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
            subscriber: mailmergeTagsSubscriber
        }

        /*popover: {//wtf does this do? f knows
         link: [
         ['link', ['linkDialogShow', 'unlink']]
         ]
         }*/
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
    //});

};

var renderMailmergeTag = function (label, timeout) {
    var token = md5(label);
    setTimeout(function () {
        var content = $('#preview').html();
        var mmLink = '<a target="_blank" class="' + token + '"></a>';
        $('#preview').html(content.replace(label, mmLink));
        var count = $('#preview a.' + token).size();
        $('#preview a.' + token).each(function () {
            var link = $(this);
            link.attr('data-link', token)
            callWS('createMailmergeTestLink',
                    {label: label},
            function (result) {
                var jsonObj = JSON.parse(result);
                link.attr('href', jsonObj['url']);
                link.html(jsonObj["name"]);

                link.removeClass(token);
                if (!--count)
                    $('#processedContent').val($('#preview').html());
            },
                    function (code, error, message) {
                        $('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
                    });
        })

    }, timeout);
}

var renderEverything = function () {
    renderPreview(0);
    renderMailmergeTag('!confirm', 50);
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