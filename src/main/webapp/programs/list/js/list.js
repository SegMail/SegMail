var web_service_endpoint = 'WSImportSubscriber';

var DELIMITER = ',';

var ERROR_COUNT_LIMIT = 10;

function saveSettings(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");


    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            //data.source.onBegin();
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            //data.source.onComplete();
            break;

        case "success": // This is called when ajax response is successfully processed.
            //$(data.source).trigger('onSuccess');
            bindFileInput();
            //data.source.onSuccess();
            break;
    }
}
;

function bindFileInput() {
    $('#uploader').on('change.bs.fileinput', function (event) {
        
        //event.stopPropagation();
        //https://github.com/anpur/client-line-navigator
        var files = document.getElementById('fileUploaded').files;
        if(!files) return;
        var navigator = new FileNavigator(files[0]);
        navigator.readLines(0,1,function lineReadHandler(err, partIndex, lines, eof){
            if (err) console.log(err);
            
            //Get the first line
            var firstLine = lines[0];
            //document.getElementById('columns').value = firstLine;
            //console.log(firstLine);
            if(!firstLine){
                console.log('Empty file.');
                return;
            }
            var headers = firstLine.split(DELIMITER);
            $('#field_selector select').empty().append($('<option>', { 
                value: '',
                text : '--No field selected--' 
            }));
            $.each(headers, function (i, item) {
                $('#field_selector select').append($('<option>', { 
                    value: i,
                    text : item 
                })).select2(); 
            });
            
            $('#field_selector').show();
            $('#importButton').show();
            $('#progress-bar-container').show();
            $('#soap-errors').show();
            $('#upload-status').show();
            //Send JSF ajax to populate the headers as options instead
            //refreshFileColumns(event);
        }); 
    });
}

function startFileUpload() {
    
    //Get the file object and create a FileNavigator wrapper
    var files = document.getElementById('fileUploaded').files;
    if(files === null || files.length <= 0) {
        //console.log('Error: no file selected')   
        return
    }
    //var navigator = new FileNavigator(files[0]);
    PanelUpdater.reset();
    checkedErrorsCollector.reset();
    $('#soap-errors').empty();
    WSController.reset();
    $('#importButton').hide();
    //Ping the server and check if the upload was stopped halfway previously
    //by checking the file hash
    var md5Hash = '';
    checkFileHash(files[0]);
    //console.log(storeFileHash.getHash());
    //checkFileStatus(files[0].name, storeFileHash.getHash());
    
    
    //Show a different panel which will tell the user to wait and do not close the window
    //if the file upload was interrupted, ask them to upload the same file again
    //Show the progress bar and hide the file selector
    
    
    //Continue with the last file line
    //For each file line, break up into an array and extract only the selected columns
    //which were chosen in $('#field_selector')
    //Collect lines into one JSON object
    
    //When max buffer size has reached, call another function that will call the webservice
    //passing in the JSON object
    
    return false;
}


var checkFileHash = function(file) {
    block_refresh($('#importButton').parents(".block"));
    $('#importButton').prop("disable",true);
    
    if(!file) {
        console.log('File not defined!');
        $('#error-messages').text('Please select a file.');
        block_refresh($('#importButton').parents(".block"));
        return;
    }
    var navigator = new FileNavigator(file);
    var startIndex = 0;
    var countLines = 0;
    
    navigator.readSomeLines(
            startIndex,
            function linesReadHandler(err,index,lines,eof,progress) {
                if(err){
                    console.log('FileNavigator error: '+err);
                    return;
                }
                countLines += lines.length;
                //console.log(lines.length);
                
                if(eof) {
                    PanelUpdater.setTotalLines(countLines-1); //minus first line which is the header
                    //console.log(PanelUpdater.getTotalLines());
                    var contents = '';
                    for(var i=0; i<lines.length; i++){
                        contents += lines[i];
                    }
                    
                    checkFileStatus(file,file.name,md5(contents),successAndStart);
                    return;
                }
                navigator.readSomeLines(index + lines.length, linesReadHandler);
            }
    );
        
};

var checkFileStatus = function(file,name,hash,success,error) {
    $.soap({
        url :   web_service_endpoint,
        method: 'checkFileStatus',
        appendMethodToURL: 0,
        data : {
            fileHash : hash,
            filename : name
        },
        namespaceQualifier: 'ns',
        namespaceURL: 'http://webservice.list.program.segmail/',
        noPrefix: 0,
        HTTPHeaders : {
            
        },
        success: function (SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:checkFileStatusResponse']["return"];
            //console.log(result);
            success(file,result);
            block_refresh($('#importButton').parents(".block"));
        },
        error: function (SOAPResponse) {
            logSOAPErrors(SOAPResponse);
            block_refresh($('#importButton').parents(".block"));
        }
    })
};

var successAndStart = function(file,startIndex){
    console.log('Started with index '+startIndex);
    
    //set buffer variables
    var readIndex = (startIndex >= 0) ? startIndex : 1;
    var navigator = new FileNavigator(file,'', {
        chunkSize : Math.pow(2,10) * 2 //Assuming ASCII encoding 2 bytes per character
    });
    navigator.readSomeLines(
        readIndex,
        function linesReadHandler(err,index,lines,eof,progress) {
            if(err){
                console.log('Error at line '+index,err);
                return;
            }
            //Call SOAP
            console.log('Now at line '+index);
            console.log('This batch is '+lines.length+' lines long.');
            console.log('progress = '+progress);
            //Construct the JSON objects for each subscriber
            //Construct the container JSON object to collect all subscriber objects
            //Call Webservice
            var batch = constructSubscribers(lines,index);
            console.log(batch);
            sendBatchToWS(batch, function(totalProcessed,success,errors){
                //PanelUpdater.updateLines(lines.length);
                PanelUpdater.updateTotalProcessed(totalProcessed);
                PanelUpdater.updateSuccess(success);
                PanelUpdater.updateError(errors);
                //console.log(PanelUpdater.getProgress());
                PanelUpdater.updateStatus();
                checkedErrorsCollector.updateErrorList();
                checkedErrorsCollector.generateErrorFile(file);
            });
            
            if(eof){
                return;
            }
            
            navigator.readSomeLines(index + lines.length, linesReadHandler);
    });
};

var constructSubscribers = function(array,startIndex){
    //get mapping first
    var mapping = {};
    $('select.select2').each(function(i,item){
        var id = item.id;
        var val = item.value;
        if(val) {
            //console.log(id,val);
            var idStore = mapping[val];
            if(!idStore){
                idStore = [];
            } 
            idStore[idStore.length] = id; //id = the key of the field
            mapping[val] = idStore; //val = position in file
        }
    });
    console.log(mapping);
    
    //Initialize the container object
    var container = {};
    
    //For each item in array, pass in a function to construct and add in the JSON object for subscriber
    $.each(array,function(i,row){
        var newObj = {
            sno : i+startIndex
        };
        var delimitedRow = row.split(DELIMITER);
        $.each(mapping,function(j,node){
            if(!node)
                return;
            
            $.each(node,function(k,field){
                var str = delimitedRow[j];
                if(str && str.charAt(0) === '"')
                    str = str.substring(1,str.length); //Remove opening "
                if(str && str.charAt(str.length-1) === '"')
                    str = str.substring(0,str.length-1); //Remove closing "

                //str.replace(/(\r\n|\n|\r)/gm,""); //Remove line breaks
                newObj[field] = str;
            });
        });
        container[i] = newObj;
        
        //update progress
        
    });
    //console.log(container);
    return container;
};

var sendBatchToWS = function(batch,successCallback,errorCallback){
    WSController.incrementCallCounts();
    $.soap({
        url :   web_service_endpoint,
        method: 'addSubscribers',
        appendMethodToURL: 0,
        data : {
            subscribers : JSON.stringify(batch)
        },
        namespaceQualifier: 'ns',
        namespaceURL: 'http://webservice.list.program.segmail/',
        noPrefix: 0,
        HTTPHeaders : {
            
        },
        success: function (SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            //console.log(xmlResults);
            
            //Retrieve these numbers from xmlResults
            //successCallback(totalCreated,existing,fresh);
            logCheckedErrors(SOAPResponse,successCallback);
            WSController.decrementCallCounts();
        },
        error: function (SOAPResponse) {
            //console.log(this);
            //$('#soapcall').text('Response: Error!').append(SOAPResponse.toString());
            logSOAPErrors(SOAPResponse);
            if(errorCallback) errorCallback();
            WSController.decrementCallCounts();
        }
    })
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

var logCheckedErrors = function(SOAPResponse,callback) {
    var jsonresult = SOAPResponse.toJSON();
    
    var responseObject = JSON.parse(jsonresult['#document']['S:Envelope']['S:Body']["ns2:addSubscribersResponse"]["return"]);
    //Parse into JSON object first
    console.log(responseObject);
    var totalCount = 0;
    var errors = 0;
    
    var keys = Object.keys(responseObject);
    for(var i = 0; i < keys.length; i++) {
        if(keys[i] === 'total') {
            totalCount = responseObject[keys[i]];
            continue;
        }
        if(keys[i] === 'errors') {
            errors = responseObject[keys[i]];
            continue;
        }
       
        var values = responseObject[keys[i]];
        values.sort(function(a,b){
            return a['sno'] - b['sno'];
        });
        for(var j = 0; j < values.length; j++) {
            checkedErrorsCollector.addLine(keys[i],values[j]['sno']);
        }
        
    }
    if(callback) callback(totalCount,totalCount-errors,errors);
}

var PanelUpdater = (function(){
    var totalLines = 0;
    var totalProcessed = 0;
    var totalSuccess = 0;
    var totalError = 0;
    
    return {
        setTotalLines : function(lines) {
            totalLines = lines;
        },
        
        getTotalLines : function() {
            return totalLines;
        },
        
        updateTotalProcessed : function(processed) {
            totalProcessed += processed;
        },
        
        updateSuccess : function(success) {
            totalSuccess += success;
        },
        
        updateError : function(error) {
            totalError += error;
        },
        
        getTotalProcessed : function() {
            return totalProcessed;
        },
        
        getSuccess : function() {
            return totalSuccess;
        },
        
        getError : function() {
            return totalError;
        },
        
        getProgress : function() {
            return (totalLines > 0) ? 100 * totalProcessed / totalLines : 0;
        },
        
        updateStatus : function () {
            $('#progress-bar').css('width',this.getProgress()+'%');
            $('#progress-bar-level').html(Math.round(this.getProgress()));
            $('#totalProcessed').html(this.getTotalProcessed());
            $('#totalSuccess').html(this.getSuccess());
            $('#totalError').html(this.getError());
        },
        
        reset : function() {
            totalLines = 0;
            totalProcessed = 0;
            totalSuccess = 0;
            totalError = 0;
            this.updateStatus();
        }
    };
})();

var processError500 = function(JsonResult) {
    var faultstring = JsonResult['#document']['S:Envelope']['S:Body']['S:Fault']["faultstring"];
    console.log(faultstring);
    if(faultstring.indexOf('UserLoginException') > -1) //if starts with java.lang.RuntimeException: eds.component.user.UserLoginException: Please enter username.
        return "Please log in again.";
    switch(faultstring) {
        default    :   return 'Error occurred at server side: '+faultstring;
    }
};

var checkedErrorsCollector = (function(){
    var container = 'error-messages';
    var fileDownloadContainerId = 'error-file-download';
    var errorItemList = {};
    var outputErrorFileContent = '';
    
    return {
        addError    :   function(errorMessage) {
            var id = this.getId(errorMessage);
            if($('#'+container).find('#'+id).length <= 0)
                $('#'+container).append(
                    '<div id='+id+'>'
                        +'<p>\"<strong>'+errorMessage+'</strong>\" at:</p>'
                    +'</div>'
                    );
        },
        
        addLine     :   function(errorMessage,lineNum) {
            this.addError(errorMessage);
            if(!errorItemList[errorMessage])
                errorItemList[errorMessage] = [];
            errorItemList[errorMessage].push(lineNum);
        },
        
        getId       :   function(errorMessage) {
            var words = errorMessage.split(/[\s,.]+/);
            var id = '';
            for(var i = 0; i < words.length && i < 3; i++){
                if(i > 0)
                    id += '-';
                id += words[i];
            }
            return id;
        },
        
        updateErrorList :   function() {
            var keys = Object.keys(errorItemList);
            $.each(keys,function(i,key){
                errorItemList[key].sort(function(a,b){
                    return a - b;
                });
                var id = checkedErrorsCollector.getId(key);
                $('#'+id).empty();
                $('#'+id).append('<p><strong>'+key+'</strong></p>');
                $('#'+id).append('<ul></ul>');
                for(var i = 0; i < errorItemList[key].length; i++){
                    if(i >= ERROR_COUNT_LIMIT) {
                        $('#'+id+' ul').append('<li><em>...and '+ (errorItemList[key].length-i)+' more</em></li>');
                        return;
                    }
                    var lineNum = errorItemList[key][i];
                    $('#'+id+' ul').append('<li>Line number '+lineNum+'</li>');
                }
            });
        },
        
        generateErrorFile   :   function(file) {
            $('#'+fileDownloadContainerId).empty();
            if(PanelUpdater.getProgress() < 100) {
                 return;
            }
            
            $('#doneButton').show();
            console.log('Done');
            
            //Re-arrange the lines with errors into a sorted array
            var sortedArray = [];
            var keys = Object.keys(errorItemList);
            for(var i = 0; i < keys.length; i++){
                var key = keys[i];
                for(var j = 0; j < errorItemList[key].length; j++){
                    var line = errorItemList[key][j];
                    sortedArray[sortedArray.length] = {
                        sno : line,
                        error : key
                    };
                }
            }
            sortedArray.sort(function sortFn(a,b){
                return a['sno'] - b['sno'];
            });
            console.log(sortedArray);
            
            if(sortedArray.length <= 0)
                return;
            
            //Start our awesome algorithm
            //Forget awesome, think simple
            var bufferStart = 0; 
            var bufferEnd = 0;
            var navigator = new FileNavigator(file);
            
            navigator.readSomeLines(0,function callback(err,index,lines,eof,progress){
                if(err) {
                    console.log(err);
                    return;
                }
                    
                //Write the first line
                if(index === 0) {
                    outputErrorFileContent += lines[0] + '\r\n';
                }
                
                //Assume that sortedArray is always smaller than file
                for(var i = 0; i < sortedArray.length; i++) {
                    var errorLine = sortedArray[i]['sno'] - index;//index is the offest
                    if(errorLine < 0 || errorLine >= lines.length)
                        continue;
                    var actualLine = lines[errorLine];
                    var errorMessage = sortedArray[i]['error'];
                    outputErrorFileContent += actualLine + ',' + errorMessage + '\r\n';
                }
                
                if(eof) {
                    //console.log(outputErrorFileContent);
                    $('#'+fileDownloadContainerId).append('<a id=\"'+fileDownloadContainerId+'-link\">Click here to download error file.</a>');
                    $('#'+fileDownloadContainerId+'-link').attr('href','data:text/plain;charset=utf-8,'+encodeURIComponent(outputErrorFileContent));
                    $('#'+fileDownloadContainerId+'-link').attr('download',file.name);
                    return;
                }
                
                navigator.readSomeLines(index + lines.length, callback);
            });
            
        },
        
        reset : function() {
            errorItemList = {};
            outputErrorFileContent = '';
            $('#'+container).empty();
            $('#'+fileDownloadContainerId).empty();
        }
    };
    
})();

var WSController = (function(){
    var MAX_NUM_CALLS = 10;
    var count = 0;
    
    return {
        incrementCallCounts : function(){
            count++;
        },
        decrementCallCounts : function(){
            count--;
        },
        checkCall : function(callback){
            while(count >= MAX_NUM_CALLS){
                
            }
            callback();
        },
        reset : function(){
            count = 0;
        }
    }
})();

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

$(document).ready(function () {
    //$('#importButton').trigger('onSuccess');
    bindFileInput();
    $('#fileUploaded').val('');//To clear the previous uploaded file
    $('#field_selector').hide();
    $('#importButton').hide();
    $('#progress-bar-container').hide();
    $('#soap-errors').hide();
    $('#upload-status').hide();
    $('#doneButton').hide();
    
    //Datatables
    $('.sortable').dataTable();
    $('.sortable').on('page.dt',function(){
        console.log('Page event triggered');
        $('.sortable').row.add({
            "EMAIL" : "test"
        }).draw();
    });
});