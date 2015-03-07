//Object to encapsulate ws request information and calls the service
var SOAPRequestAdaptor = (function(){
    var url;
    var method;
    var appendMethodToURL;
    var SOAPAction;
    var soap12;
    var async;
    var data;
    var wss;
    var HTTPHeaders;
    var envAttributes;
    var namespaceQualifier;
    var namespaceURL;
    var noPrefix;
    var elementName;
    var enableLogging;
    var context;
    
    
}());

function callSOAP(points) {
    return $.soap({
        url: "#{ProgramChartJS.WEB_SERVICE_ENDPOINT}",
        method: "#{ProgramChartJS.WEB_SERVICE_METHOD}",
        appendMethodToURL: 0,
        //SOAPAction: 'outstandingLoanOverTime',
        //soap12: $('#soap12').prop('checked'),
        //async: $('#async').prop('checked'),
        data: {
            int: $('#intRateInput').val(),
            n: $('#numYearsInput').val(),
            p: $('#principalInput').val()
        },
        //wss: wss,
        //HTTPHeaders: {
            // Authentication: 'Basic ' + btoa('test:test')
        //},
        envAttributes: {
            //'xmlns:test': 'http://www.test.org/'
            'xmlns:SOAP-ENV': 'http://schemas.xmlsoap.org/soap/envelope/'
        },
        //SOAPHeader: '<test>"SOAPHeader"</test>',
        namespaceQualifier: '#{ProgramChartJS.WEB_SERVICE_NAMESPACE_QUALIFIER}',
        namespaceURL: 'http://chartjs.program.seca2/',
        noPrefix: 0, //You definitely need a namespace prepended to the operationName
        //elementName: $('#elementName').val(),
        //enableLogging: $('#enableLogging').prop('checked'),
        //context: $('#feedback'),

        //My variables
        HTTPHeaders : {

        },

        beforeSend: function (SOAPEnvelope) {
            var xmlout = dom2html($.parseXML(SOAPEnvelope.toString()).firstChild);
            $('#soapRequest').text(xmlout);
        },
        success: function (SOAPResponse) {

            //	console.log(this)

            $('#soapcall').html('Response: Success!');
            //$('#soapcall').text(dom2html(SOAPResponse.toXML().firstChild));
            var result = SOAPResponse.content;
            //$('#soapResult').append("<p>"+SOAPResponse.toString()+"</p>");
            //$('#soapResult').text(dom2html(SOAPResponse.content));
            var xmlResults = SOAPResponse.toJSON();
            var test = JSON.stringify(xmlResults);
            //amount = $(xmlResults).find("return");
            points.data = xmlResults["#document"]["S:Envelope"]["S:Body"]["#{ProgramChartJS.WEB_SERVICE_NAMESPACE_QUALIFIER}:#{ProgramChartJS.WEB_SERVICE_METHOD}Response"];  

        },
        error: function (SOAPResponse) {

            console.log(this)

            $('#soapcall').text('Response: Error!').append(SOAPResponse.toString());
            //$('#soapcall')
        },
        statusCode: {
            404: function () {
                console.log('404')
            },
            200: function () {
                console.log('200')
            }
        }
    });
}