var SUMMERNOTE_HEIGHT = 290;
var PREVIEW_HEIGHT = 400;

/**
 * To submit a JSF partial request using pure JS, you need to:
 * 1) Attach a f:ajax in the form that you want to submit in the xhtml page.
 * 2) In this JS function, pass in the source, ajax event and list of params. Refer to
 * https://docs.oracle.com/cd/E17802_01/j2ee/javaee/javaserverfaces/2.0/docs/js-api/symbols/jsf.ajax.html#.request
 * for detailed explanation of each param.
 * 
 * @param {type} activityId
 * @param {type} event
 * @returns {undefined}
 */

function load_activity(activityId, event) {
    jsf.ajax.request(
            $('#FormCampaignActivities'),
            event,
            {
            })
}

function refresh_summernote() {
    $('textarea.editor').summernote({
        height: SUMMERNOTE_HEIGHT,
        
        toolbar: [
            ['style',['style']],
            ['font', ['bold', 'italic', 'underline']],
            //['font', ['fontname']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link', 'picture']],
            ['view',['codeview']]
        ]
    });
    // Observe a specific DOM element:
    observeDOM(document.getElementsByClassName('note-editable')[0], function () {
        console.log('dom changed');
        preview();
        highlightAndCreateLinks();
    });
}

function refresh_select2() {
    //$('select.form-select').select2();
    var select = $('body').find("select.select2");
    select.select2("destroy").select2();
}

function preview() {
    $('#content').next().find('.note-editable').each(function () {

        var height = $('#editor-form').height();
        $('#preview').html($(this).html());
        var maxWidth = largestWidth('#preview');
        var scaleX = Math.min($('#preview').width() / maxWidth, 1);
        var scaleY = Math.min(PREVIEW_HEIGHT / $('#preview').height(), 1);
        $('#preview').css({
            transform: 'scale(' + scaleX + ',' + scaleY + ')',
            'transform-origin': '0 0 0'
        });
        //Transform the container as well, or the whole modal will remain long
        $('#preview-form').height(scaleY * $('#preview').height());
    });
}

function highlightAndCreateLinks() {

    var index = 1;
    var prevPos = 0;
    var positions = [];
    //Clear the preview pane first
    $('#links').empty();
    $('#preview').find('a').each(function () {
        //console.log($(this).attr('href'));
        var offset = $(this).offset().top - $('#preview').offset().top;
        //console.log($(this).attr('href')+'(offset:'+offset+')');

        positions.push(offset);

        var marginTop = Math.max(offset - prevPos - $('#links div').last().height(), 0);
        //Add new element to links pane
        $('#links').append(
                "<div style='margin-top: "
                + marginTop
                + "px;' "
                + "class='css-bounce' "
                + ">"
                + "<span class='badge badge-primary'>"
                + (index++)
                + "</span> "
                + $(this).text()
                + "</div>"
                );
        //factor in the height for offset
        var prevHeight = $('#links div').last().height();
        prevPos = offset;// - 

    });
}

/**
 * This algorithm must produce an evenly distributed list of items based on their 
 * position offset in posArray.
 * 
 * @param {type} posArray
 * @returns {undefined}
 */
function rearrangeDivs(posArray) {
    for (var i = 0; i < posArray.length; i++) {

    }
}

function largestWidth(selector) {
    var maxWidth = 0;
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

/**
 * http://stackoverflow.com/a/14570614/5765606
 * Better listener
 * 
 */
var observeDOM = (function () {
    var MutationObserver = window.MutationObserver || window.WebKitMutationObserver,
            eventListenerSupported = window.addEventListener;

    return function (obj, callback) {
        if (MutationObserver) {
            // define a new observer
            var obs = new MutationObserver(function (mutations, observer) {
                if (mutations[0].addedNodes.length || mutations[0].removedNodes.length)
                    callback();
            });
            // have the observer observe foo for changes in children
            obs.observe(obj, {childList: true, subtree: true});
        }
        else if (eventListenerSupported) {
            obj.addEventListener('DOMNodeInserted', callback, false);
            obj.addEventListener('DOMNodeRemoved', callback, false);
            //obj.addEventListener('DOMNodeInsertedIntoDocument', callback, false);
        }
    }
})();


function saveAndContinue(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");
    

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
             //IMPORTANT! This will copy the contents of Summernote editor into our original textarea.
            preview();
            highlightAndCreateLinks();
            break;

        case "complete": // This is called right after ajax response is received.
            //ajaxloader.style.display = 'none';
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            refresh_summernote();
            refresh_select2();
            setSendInBatch('sendInBatch');
            preview();
            highlightAndCreateLinks();
            break;
    }
};

function load_activity(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");
    

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            //ajaxloader.style.display = 'none';
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            refresh_summernote();
            refresh_select2();
            setSendInBatch('sendInBatch');
            preview();
            highlightAndCreateLinks();
            break;
    }
};

function create_new_activity(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".refresh");
    //var ajaxloader = document.getElementById("ajaxloader");
    

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            break;
    }
};

function saveBasicSettings(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");
    

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            
            break;
    }
};

function saveAssignLists(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");
    

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            refresh_select2();
            break;
    }
};

function setSendInBatch(id) {
    var value = document.getElementById(id).value;
    if(value <= 0 )
        document.getElementById(id).value = null;
}


