var saveAutoemail = function() {
    //Call webservice
    $.soap({
        url : web_service_endpoint,
        method : 'saveAutoemail',
        appendMethodToURL: 0,
        namespaceQualifier: 'ns',
        namespaceURL: 'http://webservice.autoresponder.program.segmail/',
        noPrefix: 0,
        HTTPHeaders: {
        },
    /*$.ajax({
        type : "POST",
        contentType : "application/json",
        url: "/autoresponder/save",*/
        data : {
            'body' : document.getElementById('editor').innerHTML,//$('#editor').html().text(),
            'bodyProcessed' : $('#processedContent').val()
        },
        success : function(SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:saveAutoemailResponse']["return"];
            $('#saveResults').html(result); //Don't know how it will look like yet
            $('#saveButton').prop('disabled',false);
        },
        error : function(SOAPResponse) {
            var response = SOAPResponse.toJSON();
            $('#saveResults').html(response); //Don't know how it will look like yet
            $('#saveButton').prop('disabled',false);
        }
    });
};

var createMailmergeTestLink = function(label,htmlLink) {
    //Call webservice
    $.soap({
        url : web_service_endpoint,
        method : 'createMailmergeTestLink',
        appendMethodToURL: 0,
        namespaceQualifier: 'ns',
        namespaceURL: 'http://webservice.autoresponder.program.segmail/',
        noPrefix: 0,
        HTTPHeaders: {
        },
    /*$.ajax({
        type : "POST",
        contentType : "application/json",
        url: "/autoresponder/save",*/
        data : {
            label : label
        },
        success : function(SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:saveAutoemailResponse']["return"];
            var name = result['name'];
            var url = result['url'];
            $(htmlLink).attr('href',url);
            $(htmlLink).html(name);
        },
        error : function(SOAPResponse) {
            var response = SOAPResponse.toJSON();
            $('#saveResults').html(response); //Don't know how it will look like yet
            $('#saveButton').prop('disabled',false);
        }
    });
};

var callWS = function(url,method,namespace,data,successCallback,errorCallback,timeout) {
    setTimeout(function(){
    $.soap({
        url : url,
        method : method,
        appendMethodToURL: 0,
        namespaceQualifier: 'ns',
        namespaceURL: namespace,//'http://webservice.autoresponder.program.segmail/',
        noPrefix: 0,
        HTTPHeaders: {
        },
        data : data,
        success : function(SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:'+method+'Response']["return"];
            if (successCallback)
                successCallback(result);
        },
        error : function(SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['S:Fault']["faultstring"];
            if (errorCallback)
                errorCallback(SOAPResponse.httpCode,SOAPResponse.httpText,result);
        }
    });
    },timeout);
}