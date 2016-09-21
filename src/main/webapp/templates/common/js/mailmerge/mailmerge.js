var mailmergeWSCache = {};

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

var renderMailmergeLinkHelper = function(sourceSel,targetSel,token,result) {
    var jsonObj = JSON.parse(result);
    //var count = $(sourceSel+' a.' + token).size();
    $(sourceSel+' a.' + token).each(function(){
        var link = $(this);
        link.attr('href', jsonObj['url']);
        link.html(jsonObj["name"]);
        //if (!--count)
        //    $(targetSel).val($(sourceSel).html()); //No need for this, since the preview content is just for viewing
    });
}
var renderMailmergeLink = function(sourceSel,targetSel,label,errorCallback,timeout) {
    var token = md5(label);
    
    setTimeout(function () {
        if(mailmergeWSCache[token]){
            renderMailmergeLinkHelper(sourceSel,targetSel,token,mailmergeWSCache[token]);
            return;
        }
        callWS(WSAutoresponderEndpoint,
            'createSystemMailmergeTestLink',
            'http://webservice.autoresponder.program.segmail/',
            {label: label},
            function (result) {
                renderMailmergeLinkHelper(sourceSel,targetSel,token,result);
                //Cache the results
                mailmergeWSCache[token] = result;
            },
            function (code, error, message) {
                //$('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
                errorCallback(code, error, message);
            }
        );
    }, timeout);
};

var renderMailmergeTagHelper = function(sourceSel,targetSel,token,result) {
    //var count = $(sourceSel+' span.' + token).size();
    $(sourceSel+' span.' + token).each(function(){
        $(this).html(result);
        //if (!--count)
        //    $(targetSel).val($(sourceSel).html()); //No need for this, since the preview content is just for viewing
    });
}

var renderMailmergeTag = function(sourceSel,targetSel,label,errorCallback,timeout) {
    var token = md5(label);
    
    setTimeout(function () {
        if(mailmergeWSCache[token]){
            renderMailmergeTagHelper(sourceSel,targetSel,token,mailmergeWSCache[token]);
            return;
        }
        callWS(WSAutoresponderEndpoint,
            'createSubscriberMailmergeTestValue',
            'http://webservice.autoresponder.program.segmail/',
            {label: label},
            function (result) {
                renderMailmergeTagHelper(sourceSel,targetSel,token,result);
                //Cache the results
                mailmergeWSCache[token] = result;
            },
            function (code, error, message) {
                //$('#saveResults').html('<span style="color: red">Error: ' + message + '</span>');
                errorCallback(code, error, message);
            }
        );
    }, timeout);
};

var replaceMailmergeTags = function(sourceSel,tags,timeout) {
    setTimeout(function() {
        var content = $(sourceSel).html();
        for(var i=0; i<tags.length; i++) {
            var label = tags[i];
            var token = md5(label);
            var mmTag = '<span class="' + token + '"></span>';
            content = content.replace(RegExp(label,'g'), mmTag);
        }
        $(sourceSel).html(content);
    },timeout);
}

var replaceMailmergeLinks = function(sourceSel,links,timeout) {
    setTimeout(function() {
        var content = $(sourceSel).html();
        for(var i=0; i<links.length; i++) {
            var label = links[i];
            var token = md5(label);
            var mmLink = '<a target="_blank" class="' + token + '"></a>';
            content = content.replace(RegExp(label,'g'), mmLink);
        }
        $(sourceSel).html(content);
    },timeout);
}

var renderMailmergeTags = function(sourceSel,targetSel,tags,errorCallback,timeout) {
    //Call WS and render the actual values
    for(var i=0; i<tags.length; i++) {
        renderMailmergeTag(sourceSel,targetSel,tags[i],errorCallback,timeout);
    }
}

var renderMailmergeLinks = function(sourceSel,targetSel,tags,errorCallback,timeout) {
    //Call WS and render the actual values
    for(var i=0; i<tags.length; i++) {
        renderMailmergeLink(sourceSel,targetSel,tags[i],errorCallback,timeout);
    }
}

/**
 * 
 * @param {type} sourceSel source selector
 * @param {type} updateSel the element to update after all links or tags are rendered
 * @param {type} links array of mailmerge links tag
 * @param {type} tags array of mailmerge attribute tags
 * @param {type} successCallback optional callback method
 * @param {type} errorCallback optional callback method
 * @returns {undefined}
 */
var processMailmerge = function(sourceSel,updateSel,links,tags,successCallback,errorCallback) {
    replaceMailmergeTags(sourceSel,tags,50);
    replaceMailmergeLinks(sourceSel,links,50);
    renderMailmergeTags(sourceSel,updateSel,tags,
        errorCallback,50);
    renderMailmergeLinks(sourceSel,updateSel,links,
        errorCallback,50);
}

$(document).ready(function(){
    if(!WSAutoresponderEndpoint)
        console.log('WSAutoresponderEndpoint is missing.');
    
    if(!mailmergeTagsSubscriber)
        console.log('mailmergeTagsSubscriber is missing.');
    
    if(!mailmergeLinks)
        console.log('mailmergeLinks is missing.');
})