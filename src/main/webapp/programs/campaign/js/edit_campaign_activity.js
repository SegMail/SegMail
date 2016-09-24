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
                for(var key in mailmergeTagsSubscriber) {
                    if (mailmergeTagsSubscriber.hasOwnProperty(key)) {
                        allTagsAndLinks.push(key);
                    }
                }
                //mailmergeTagsSubscriber.forEach(function(item){
                //    allTagsAndLinks.push(item);
                //});
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
/**
 * No WS calls for this one, just JSF ajax.
 * We can't use JSF to submit changes because
 * @returns {undefined}
 */
var onSave = function (data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            reapply_textarea('editor');
            renderEverything();
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            refresh_summernote('textarea.editor');
            refresh_select2();
            modifyDomToGeneratePreview();
            noty({
                text : 'Email saved at ',
                layout : 'topCenter',
                type : 'success',
                timeout : true
            }).setTimeout(2000);
            break;
        case "error":
            noty({
                text : 'Error.',
                layout : 'topCenter',
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
        $('#processedContent').html($('.note-editable').html());//do this earlier

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

var addHashIdToLinks = function(timeout) {
    setTimeout(function(){
    //For each link, add a data-link hashed ID
        $('.note-editable').find('a').each(function(index){
            var obj = $(this);
            if(obj.attr('data-link'))
                return;
            var hashId = md5(obj+obj.attr('href')+obj.html()+index);
            obj.attr('data-link',hashId);
        })
    },timeout);
}

var renderEverything = function () {
    addHashIdToLinks(0);
    renderPreview(0);
    highlightAndCreateLinks(0);
    processMailmergeNoWS(0);
    //processMailmerge('#preview','#processedContent',mailmergeLinks,mailmergeTagsSubscriber, //Actually since we already know the entire list of mailmergeTags available, why not just load it in a xhtml page as a JSON object?
    //function(){//successCallback
    //    highlightAndCreateLinks(0);
    //},
    //function(){//errorCallback
    //    //$('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
    //});
};

var processMailmergeNoWS = function(timeout) {
    setTimeout(function(){
        for(var key in mailmergeTagsSubscriber) {
            if(mailmergeTagsSubscriber.hasOwnProperty(key)) {
                var subscVal = randomSubscriber[mailmergeTagsSubscriber[key]];
                var content = $('#preview').html();
                content = content.replace(key,subscVal);
                $('#preview').html(content);
            }
        }
    },timeout);
    
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

function setSendInBatch(id) {
    var value = document.getElementById(id).value;
    if (value <= 0)
        document.getElementById(id).value = null;
}
var highlightAndCreateLinksOld = function (timeout) {
    setTimeout(function () {
        //Clear the preview pane first
        $('#links').empty();
        //Clear the linksContainer too
        linksContainer.reset();
        var allLinks = $('.note-editable').find('a');
        if (allLinks.size() <= 0)
            return;

        var count = allLinks.size();
        var position = 0;
        allLinks.each(function (index) {
            //Create the badges for each link first, then call WS to get the redirectlink and fill them up in the preview pane!
            var obj = $(this);
            var linkText = obj.text();
            var offset = obj.offset().top - $('#preview').offset().top;
            var marginTop1 = offset;
            
            if(obj.attr('data-link'))
                return;

            if (index > 0) {
                var lastOffset = position;
                marginTop1 = marginTop1 - lastOffset;
            }
            var marginTop = Math.max(marginTop1, 0);
            if(marginTop > 0) //It is not "sticking" to the previous button
                marginTop -= 0.5*12; //12px is the pre-defined size of the div
            else
                marginTop -= 0.25*$('#links div').last().height();
            
            var link =
                    '<div style="margin-top: '+marginTop+'px">'
                    + "<span class='link-button'>"
                    + (index + 1) //key 1
                    + "</span> "
                    + linkText //key 2
                    + "</div>";
            $('#links').append(link);
            $('#links div').last().addClass('css-bounce');//.css('margin-top', marginTop);
            position = position + marginTop + $('#links div').last().height();
            
            callWSCreateUpdateLink(obj.attr('href'), obj.html(), index,
                    function (redirectLink) {
                        obj.attr('href', redirectLink);
                        //if (!--count)
                        //    copyPreviewContent();
                    },
                    function (code,text,result) {
                        noty({
                            text : result,
                            layout : 'topCenter',
                            type : 'danger'
                        });
                    }
            );
        });
    }, timeout);
}

var highlightAndCreateLinks = function (timeout) {
    setTimeout(function () {
        //Clear all links in the #links panel
        $('#links').empty();
        //Copy the contents of editor/note-editable to processedContent
        //Get all links in preview 
        var allLinks = $('#preview').find('a');
        var count = allLinks.size();
        var position = 0;
        var textareaHtml = $('<div>'+$('#processedContent').val()+'</div>');
        //Loop through each link 
        allLinks.each(function(index){
        //  1) render the link labels beside the preview panel
            var obj = $(this);
            var linkText = obj.text();
            var hashId = obj.attr('data-link');
            var offset = obj.offset().top - $('#preview').offset().top;
            var marginTop1 = offset;
            
            if (index > 0) {
                var lastOffset = position;
                marginTop1 = marginTop1 - lastOffset;
            }
            var marginTop = Math.max(marginTop1, 0);
            if(marginTop > 0) //It is not "sticking" to the previous button
                marginTop -= 0.5*12; //12px is the pre-defined size of the div
            else
                marginTop -= 0.25*$('#links div').last().height();
            var link =
                    '<div style="margin-top: '+marginTop+'px">'
                    + "<span class='link-button'>"
                    + (index + 1) //key 1
                    + "</span> "
                    + linkText //key 2
                    + "</div>";
            $('#links').append(link);
            $('#links div').last().addClass('css-bounce');//.css('margin-top', marginTop);
            position = position + marginTop + $('#links div').last().height();
        //  2) Call WS with params: linkTarget, linkText, index
            callWSCreateUpdateLink(
                    obj.attr('href')
                    ,obj.html()
                    ,index
                    ,function(redirectLink) {
                        //  3) In success callback (redirectLink):
                        //      a) replace the preview link with redirectLink
                        $('#preview a[data-link="'+hashId+'"]').attr('href',redirectLink);
                        //      b) replace the processedContent link with redirectLink
                        //$('#processedContent a[data-link="'+hashId+'"]').attr('href',redirectLink);//Doesn't work
                        //      small hack: http://stackoverflow.com/a/20430557/5765606
                        textareaHtml.find('a[data-link="'+hashId+'"]').attr('href',redirectLink);
                        if(!--count)
                            $('#processedContent').val(textareaHtml.html());
                        console.log($('#processedContent').val());
                    },function() {
                        
                    })
        
        })
    }, timeout);
}

var linksContainer = function () {
    var linksArray = [];

    return {
        addLink: function (redirectLink) {
            linksArray.push(redirectLink);
        },
        contains: function (link) {
            for (var i = 0; i < linksArray.length; i++) {
                if (linksArray[i] === link)
                    return true;
            }
            return false;
        },
        submit: function () {
            $('#pseudo-links').val(JSON.stringify(linksArray));
        },
        reset: function () {
            linksArray = [];
        }
    }
}();

var callWSCreateUpdateLink = function (linkTarget, linkText, index, successCallback, errorCallback) {
    callWS(web_service_endpoint, 'createOrUpdateLink', 
        'http://webservice.campaign.program.segmail/',
        {
            linkTarget: linkTarget,
            linkText: linkText,
            index: index,
            //originalHTML : originalHTML
        }, successCallback, errorCallback);
};

$(document).ready(function () {
    
    toggleMenu();
    refresh_summernote('textarea.editor');
    adjustPreviewPanelHeight();
    modifyDomToGeneratePreview();
});