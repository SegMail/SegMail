var SUMMERNOTE_HEIGHT = 290;
var PREVIEW_HEIGHT = 400;

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

var preview = function() {
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

    var index = 0;
    var prevPos = 0;
    var positions = [];
    //Clear the preview pane first
    $('#links').empty();
    //Clear the linksContainer too
    linksContainer.reset();
    var allLinks = $('#preview').find('a');
    var count = allLinks.size();
    allLinks.each(function () {
        var obj = $(this);
        var offset = obj.offset().top - $('#preview').offset().top;
        positions.push(offset);

        var marginTop = Math.max(offset - prevPos - $('#links div').last().height(), 0);
        //Add new element to links pane
        var link = "<div style='margin-top: "
                + marginTop
                + "px;' "
                + "class='css-bounce' "
                + ">"
                + "<span class='badge badge-primary'>"
                + (++index) //key 1
                + "</span> "
                + obj.text() //key 2
                + "</div>";
        $('#links').append(link);
        //factor in the height for offset
        var prevHeight = $('#links div').last().height();
        prevPos = offset;// - 
        //Call WS to create or update links
        //Update the generated parsed link in the text so that we don't have to 
        //parse it anymore during sending!
        callWSCreateUpdateLink(obj.attr('href'),obj.html(),index,
            function(redirectLink){
                //Replace the html target by this 
                obj.attr('href',redirectLink);
                linksContainer.addLink(redirectLink);
                if(!--count) copyPreviewContent();
            },
            function(){
                console.log('Error has occurred');
            }
        )
        
        //execute callback
        
    });
}

var callWSCreateUpdateLink = function(linkTarget,linkText,index,successCallback,errorCallback){
    $.soap({
        url :   web_service_endpoint,
        method: 'createOrUpdateLink',
        appendMethodToURL: 0,
        data : {
            linkTarget : linkTarget,
            linkText : linkText,
            index : index
        },
        namespaceQualifier: 'ns',
        namespaceURL: 'http://webservice.campaign.program.segmail/',
        noPrefix: 0,
        HTTPHeaders : {
            
        },
        success: function (SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:createOrUpdateLinkResponse']["return"];
            if(successCallback) successCallback(result);
        },
        error: function (SOAPResponse) {
            logSOAPErrors(SOAPResponse);
            if(errorCallback) errorCallback();
        }
    });
    //successCallback('dfrwogfwlfa,cr');
 };
 
var logSOAPErrors = function(SOAPResponse){
    var jsonresult = SOAPResponse.toJSON();
    //console.log(SOAPResponse.content); 
    
    var errorMessage = '';
    var severity = '';
    switch(SOAPResponse.httpCode){
        case 404    :   errorMessage = SOAPResponse.httpText;
                        severity = 'danger';
                        break;
        case 500    :   errorMessage = processError500(jsonresult);
                        severity = 'danger';
                        break;
        default     :   errorMessage = SOAPResponse.httpText;
                        severity = 'danger';
                        break;
    }
    //if($('#soap-errors').find('.alert').length <= 0)
    //    $('#soap-errors').append('<div class="alert alert-'+severity+'"><strong>'+errorMessage+'</strong></div>');
    GenericErrorController.setErrors(errorMessage,severity);
};

var processError500 = function(JsonResult) {
    var faultstring = JsonResult['#document']['S:Envelope']['S:Body']['S:Fault']["faultstring"];
    //console.log(faultstring);
    if(faultstring.indexOf('UserLoginException') > -1) //if starts with java.lang.RuntimeException: eds.component.user.UserLoginException: Please enter username.
        return "Please log in again.";
    switch(faultstring) {
        default    :   return 'Error occurred at server side: '+faultstring;
    }
};

var copyPreviewContent = function() {
    var content = $('#preview').html();
    $('#pseudo-preview').val(content);
}

/**
 * To enhance performanace by caching the links that were already generated.
 * 
 * @type Function|campaign_L176.campaignAnonym$17
 */
var linksContainer = function(){
    var linksArray = [];
    
    return {
        addLink : function(redirectLink) {
            /*var linkObj = {
                'target' : target,
                'text' : text,
                'index' : index
            };
            linksArray.push(linkObj);
            this.submit();*/
            linksArray.push(redirectLink);
        },
        
        contains : function(link) {
            for(var i = 0; i < linksArray.length; i++) {
                if(linksArray[i] === link)
                    return true;
            }
            return false;
        },
        
        submit : function() {
            $('#pseudo-links').val(JSON.stringify(linksArray));
        },
        
        reset : function() {
            linksArray = [];
        }
    }
}();

var GenericErrorController = function(){
    var id = 'soap-errors';
    
    return {
        
        setErrors : function(error,severity) {
            $('#'+id)
            if($('#'+id).find('.alert').length > 0) {
                $('#'+id).empty();
            }
            $($('#'+id)).append('<div class="alert alert-'+severity+'"><strong>'+error+'</strong></div>');
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
            preview();
            highlightAndCreateLinks();
            //copyPreviewContent();
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
};

function setSendInBatch(id) {
    var value = document.getElementById(id).value;
    if(value <= 0 )
        document.getElementById(id).value = null;
}

/**
 * A simple hack to force-generate a preview and links by adding and subtracting
 * a small unoticable DOM object.
 * 
 * @returns {undefined}
 */
var modifyDomToGeneratePreview = function() {
    var randomNum = Math.round(Math.random()*100000);
    $('.note-editable').append('<div id=modifyDomToGeneratePreview'+randomNum+'></div>');
    $('#modifyDomToGeneratePreview'+randomNum).remove();
}

var doEverything = function() {
    preview();
    highlightAndCreateLinks();
}