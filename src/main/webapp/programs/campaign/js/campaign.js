var SUMMERNOTE_HEIGHT = 290;
var PREVIEW_HEIGHT = 450;
var PREVIEW_WIDTH = 420;

//var web_service_endpoint = 'WSCampaignActivityLink';

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

/*function load_activity(activityId, event) {
 jsf.ajax.request(
 $('#FormCampaignActivities'),
 event,
 {
 })
 }*/

function refresh_summernote() {
    $('textarea.editor').summernote({
        height: SUMMERNOTE_HEIGHT,
        toolbar: [
            ['style', ['style']],
            ['font', ['bold', 'italic', 'underline']],
            //['font', ['fontname']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link', 'picture']],
            ['view', ['codeview']]
        ]
    });
    // Observe a specific DOM element:
    observeDOM(document.getElementsByClassName('note-editable')[0], function () {
        //console.log('dom changed');
        //preview();
        //highlightAndCreateLinks();
        doEverything();

    });
}

function refresh_select2() {
    //$('select.form-select').select2();
    var select = $('body').find("select.select2");
    select.select2("destroy").select2();
}

var preview = function (sourceSel, sourceContSel, targetSel, targetContSel, timeout) {
    setTimeout(function () {
        var height = $(sourceContSel).height();
        $(targetSel).html($(sourceSel).html());
        var maxWidth = largestWidth(targetSel);
        var scaleX = Math.min($(targetSel).width() / maxWidth, 1);
        var scaleY = Math.min(PREVIEW_HEIGHT / $(targetSel).height(), 1);
        $(targetSel).css({
            transform: 'scale(' + scaleX + ',' + scaleY + ')',
            'transform-origin': '0 0 0'
        });
        //Transform the container as well, or the whole modal will remain long
        $(targetContSel).height(scaleY * $(targetSel).load().height());
        //highlightAndCreateLinks();
    }, timeout);
};

var registerDelayedChange = function () {
    $('.note-editable').delayedChange().on('delayedchange', function () {
        doEverything();
    });
};


var highlightAndCreateLinks = function (timeout) {
    setTimeout(function () {
        //var index = 0;
        var prevPos = 0;
        var positions = [];
        //Clear the preview pane first
        $('#links').empty();
        //Clear the linksContainer too
        linksContainer.reset();
        var allLinks = $('#preview').find('a');
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
                        if (!--count)
                            copyPreviewContent();
                    },
                    function () {
                        console.log('Error has occurred');
                    }
            );
        });
    }, timeout);
}

var callWSCreateUpdateLink = function (linkTarget, linkText, index, successCallback, errorCallback) {
    callWS(web_service_endpoint, 'createOrUpdateLink', {
        linkTarget: linkTarget,
        linkText: linkText,
        index: index
    }, successCallback, errorCallback);
};

var callWS = function (url, method, data, successCallback, errorCallback) {
    $.soap({
        url: url,
        method: method,
        appendMethodToURL: 0,
        data: data,
        namespaceQualifier: 'ns',
        namespaceURL: 'http://webservice.campaign.program.segmail/',
        noPrefix: 0,
        HTTPHeaders: {
        },
        success: function (SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:' + method + 'Response']["return"];
            if (successCallback)
                successCallback(result);
        },
        error: function (SOAPResponse) {
            logSOAPErrors(SOAPResponse);
            if (errorCallback)
                errorCallback();
        }
    });
};

var logSOAPErrors = function (SOAPResponse) {
    var jsonresult = SOAPResponse.toJSON();
    //console.log(SOAPResponse.content); 

    var errorMessage = '';
    var severity = '';
    switch (SOAPResponse.httpCode) {
        case 404    :
            errorMessage = SOAPResponse.httpText;
            severity = 'danger';
            break;
        case 500    :
            errorMessage = processError500(jsonresult);
            severity = 'danger';
            break;
        default     :
            errorMessage = SOAPResponse.httpText;
            severity = 'danger';
            break;
    }
    GenericErrorController.setErrors(errorMessage, severity);
};

var processError500 = function (JsonResult) {
    var faultstring = JsonResult['#document']['S:Envelope']['S:Body']['S:Fault']["faultstring"];
    //console.log(faultstring);
    if (faultstring.indexOf('UserLoginException') > -1) //if starts with java.lang.RuntimeException: eds.component.user.UserLoginException: Please enter username.
        return "Please log in again.";
    switch (faultstring) {
        default    :
            return 'Error occurred at server side: ' + faultstring;
    }
};

var copyPreviewContent = function () {
    var content = $('#preview').html();
    $('#pseudo-preview').val(content);
}

/**
 * To enhance performanace by caching the links that were already generated.
 * 
 * @type Function|campaign_L176.campaignAnonym$17
 */
var linksContainer = function () {
    var linksArray = [];

    return {
        addLink: function (redirectLink) {
            /*var linkObj = {
             'target' : target,
             'text' : text,
             'index' : index
             };
             linksArray.push(linkObj);
             this.submit();*/
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

var GenericErrorController = function () {
    var id = 'soap-errors';

    return {
        setErrors: function (error, severity) {
            $('#' + id)
            if ($('#' + id).find('.alert').length > 0) {
                $('#' + id).empty();
            }
            $($('#' + id)).append('<div class="alert alert-' + severity + '"><strong>' + error + '</strong></div>');
        }
    };
}();


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
            //preview();
            //highlightAndCreateLinks();
            break;

        case "complete": // This is called right after ajax response is received.
            //ajaxloader.style.display = 'none';

            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            refresh_summernote();
            refresh_select2();
            setSendInBatch('sendInBatch');
            //preview('.note-editable', '#editor-form', '#preview', '#preview-form',);
            //highlightAndCreateLinks();
            //copyPreviewContent();
            doEverything();
            break;
    }
}
;

function load_activity(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");


    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            //preview();
            //highlightAndCreateLinks();

            break;

        case "complete": // This is called right after ajax response is received.
            //ajaxloader.style.display = 'none';

            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            refresh_summernote();
            refresh_select2();
            setSendInBatch('sendInBatch');
            modifyDomToGeneratePreview();
            //preview();
            //highlightAndCreateLinks();
            //copyPreviewContent();
            break;
    }
}
;

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
}
;

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
}
;

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
}
;

function executeAndClose(data) {
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
}
;

function track_activity(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");


    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.

            break;

        case "complete": // This is called right after ajax response is received.
            break;

        case "success": // This is called when ajax response is successfully processed.
            getTopNumbers(0);
            resizeContent(100);
            renderLinksAndRefreshStats(100);
            break;
    }
}
;

function setSendInBatch(id) {
    var value = document.getElementById(id).value;
    if (value <= 0)
        document.getElementById(id).value = null;
}

/**
 * A simple hack to force-generate a preview and links by adding and subtracting
 * a small unoticable DOM object.
 * 
 * @returns {undefined}
 */
var modifyDomToGeneratePreview = function () {
    var randomNum = Math.round(Math.random() * 100000);
    $('.note-editable').append('<div id=modifyDomToGeneratePreview' + randomNum + '></div>');
    $('#modifyDomToGeneratePreview' + randomNum).remove();
};

var doEverything = function () {
    preview('.note-editable', '#editor-form', '#preview', '#preview-form', 0);
    highlightAndCreateLinks(50);
};

var resizeContent = function (timeout) {
    setTimeout(function () {
        var maxWidth = largestWidth('#html-content');
        var scaleY = Math.min(PREVIEW_HEIGHT / $('#html-content').height(), 1);
        var scaleX = Math.min(PREVIEW_WIDTH / maxWidth, 1);
        $('#html-content').css({
            transform: 'scale(' + scaleX + ',' + scaleY + ')',
            'transform-origin': '0 0 0'
        });
        //Transform the container as well, or the whole modal will remain long
        $('#html-content-form').height(PREVIEW_HEIGHT);

    }, timeout);
};

var renderLinksAndRefreshStats = function (timeout) {
    setTimeout(function () {
        var position = 0;
        var wsCounts = $('#html-content').find('a').size();
        var totalClicks = 0;
        $('#links-start').empty();
        $('#stats-table').find('tbody').empty();
        $('#html-content').find('a').each(function (index) {
            //console.log(index);
            var redirectLink = $(this).attr('href');
            var linkText = $(this).text();
            var offset = $(this).offset().top - $('#html-content').offset().top;
            var marginTop1 = offset;

            if (index > 0) {
                var lastOffset = position;
                marginTop1 = marginTop1 - lastOffset;
            }
            //minus another half a line cause you want the link-button to centralize
            var marginTop = Math.max(marginTop1, 0);
            if(marginTop > 0) //It is not "sticking" to the previous button
                marginTop -= 0.5*12; //12px is the pre-defined size of the div
            else
                marginTop -= 0.25*$('#links-start div').last().height();
            var link = 
                    '<div style="margin-top: '+marginTop+'px">'
                    + "<span class='link-button'>"
                    + (index + 1) //key 1
                    + "</span> "
                    + linkText //key 2
                    + "</div>";
            
            $('#links-start').append(link);
            $('#links-start div').addClass('css-bounce');
            position = position + marginTop + $('#links-start div').last().height();
            //console.log(index+' done');

            //Call webservice to get counts and update 
            callWS(web_service_endpoint, 'getClickCountForActivity', {
                'redirectLink': redirectLink
            }, function (result) {
                //Add result to table
                var totalSent = Number($('#sent').html());
                var clickthrough = (totalSent === 0) ? 0 : (Number(result) / totalSent) * 100.0;
                var row = '<tr>' +
                        '<td>' + (index + 1) + '</td>' +
                        '<td>' + linkText + '</td>' +
                        '<td>' + result + '</td>' +
                        '<td>' + clickthrough + '%' + '</td>' +
                        '</tr>';
                $('#stats-table').find('tbody').append(row);
                totalClicks += Number(result);
                //Once all calls are done
                if (!--wsCounts) {
                    
                    var totalClickthrough = (totalSent === 0) ? 0 : (Number(totalClicks) / totalSent) * 100.0;
                    var summaryRow = '<tr>' +
                                     '<td colspan="2" style="text-align: right;"><strong>Total</strong></td>' +
                                     '<td>' + totalClicks + '</td>' +
                                     '<td>' + totalClickthrough + '%</td>' +
                                     '</tr>';
                    $('#stats-table').find('tfoot').append(summaryRow);
                                     
                    $('#stats-table').dataTable({
                        'destroy': true,
                        'filter': false,
                        'paging': false,
                        'searching': false,
                        'info': false
                    });
                    $('#clicked').html(totalClicks);
                }
            }, function () {
                
            });
        });
    }, timeout);
};

var getSentEmails = function() {
    callWS(web_service_endpoint, 'getExecutedForCampaignActivity', {
        campaignActivityId : $('#activity-id').val()
    }, function(result){
        $('#sent').html(result);
    }, function(){
        $('#sent').html(0);
    });
};

var getTotalTargeted = function() {
    callWS(web_service_endpoint, 'getTotalTargetedForCampaignActivity', {
        campaignActivityId : $('#activity-id').val()
    }, function(result){
        $('#targeted').html(result);
    }, function(){
        $('#targeted').html(0);
    });
}

var getTopNumbers = function(timeout) {
    setTimeout(function(){
        getTotalTargeted();
        getSentEmails();
    },timeout)
}