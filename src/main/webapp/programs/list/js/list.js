var web_service_endpoint = 'WSImportSubscriber';

var DELIMITER = ',';

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
            //Send JSF ajax to populate the headers as options instead
            //refreshFileColumns(event);
        }); 
    });
}

function startFileUpload() {
    
    //Get the file object and create a FileNavigator wrapper
    var files = document.getElementById('fileUploaded').files;
    if(files === null || files.length <= 0) {
        console.log('Error: no file selected')   
    }
    var navigator = new FileNavigator(files[0]);
    PanelUpdater();
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
    
    if(!file) {
        console.log('File not defined!');
        return;
    }
    var navigator = new FileNavigator(file);
    var startIndex = 0;
    var countLines = 0;
    
    //navigator.getSize(PanelUpdater.start);
    
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
                    PanelUpdater.setTotalLines(countLines);
                    //console.log(PanelUpdater.getTotalLines());
                    var contents = '';
                    for(var i=0; i<lines.length; i++){
                        contents += lines[i];
                        //console.log(lines[i]);
                        //console.log(contents);
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
            //var test = JSON.stringify(xmlResults);
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:checkFileStatusResponse']["return"];
            //console.log(result);
            success(file,result);
            block_refresh($('#importButton').parents(".block"));
        },
        error: function (SOAPResponse) {
            console.log(this);
            //$('#soapcall').text('Response: Error!').append(SOAPResponse.toString());
            logErrors(SOAPResponse);
        }
    })
};

var successAndStart = function(file,startIndex){
    console.log('Started with index '+startIndex);
    
    //set buffer variables
    var readIndex = (startIndex >= 0) ? startIndex : 0;
    var navigator = new FileNavigator(file,'', {
        chunkSize : Math.pow(2,18) * 2 //Assuming ASCII encoding 2 bytes per character
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
            var batch = constructSubscribers(lines);
            console.log(batch);
            sendBatchToWS(batch, function(totalCreated,existing,fresh){
                PanelUpdater.updateLines(lines.length);
                PanelUpdater.updateTotalCreated(totalCreated);
                PanelUpdater.updateSubWithOtherLists(existing);
                PanelUpdater.updateFreshSubscribers(fresh);
                //console.log(PanelUpdater.getProgress());
                PanelUpdater.updateStatus();
            });
            
            if(eof){
                return;
            }
            
            navigator.readSomeLines(index + lines.length, linesReadHandler);
    });
};

var constructSubscribers = function(array){
    //get mapping first
    var mapping = {};
    $('select.select2').each(function(i,item){
        var id = item.id;
        var val = item.value;
        if(val) {
            console.log(id,val);
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
            sno : i
        };
        var delimitedRow = row.split(DELIMITER);
        $.each(mapping,function(j,node){
            if(!node)
                return;
            
            $.each(node,function(k,field){
                newObj[field] = delimitedRow[j];
            });
        });
        container[i] = newObj;
        
        //update progress
        
    });
    //console.log(container);
    return container;
}

var sendBatchToWS = function(batch,successCallback,errorCallback){
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
            console.log(xmlResults);
            
            //Retrieve these numbers from xmlResults
            //successCallback(totalCreated,existing,fresh);
            successCallback(1,1,1);
            
        },
        error: function (SOAPResponse) {
            console.log(this);
            //$('#soapcall').text('Response: Error!').append(SOAPResponse.toString());
            logErrors(SOAPResponse);
            errorCallback();
        }
    })
}

$(document).ready(function () {
    //$('#importButton').trigger('onSuccess');
    bindFileInput();
});

var logErrors = function(SOAPResponse){
    var errorMessage = '';
    switch(SOAPResponse.httpCode){
        case 500    :   errorMessage = 'Your session has expired. Please refresh the page and try again.';
                        break;
        default     :   errorMessage = SOAPResponse.httpText;
                        break;
    }
    $('#error-message').text('Error: ').append(errorMessage);
    block_refresh($('#importButton').parents(".block"));
};

var PanelUpdater = (function(){
    var totalLines = 0;
    var linesUpdated = 0;
    var totalCreated = 0;
    var subWithOtherLists = 0;
    var freshSubscribers = 0;
    
    return {
        setTotalLines : function(lines) {
            totalLines = lines;
        },
        
        getTotalLines : function() {
            return totalLines;
        },
        
        updateLines : function(lines) {
            linesUpdated += lines;
        },
        
        updateTotalCreated : function(created) {
            totalCreated += created;
        },
        
        updateSubWithOtherLists : function(otherList) {
            subWithOtherLists += otherList;
        },
        
        updateFreshSubscribers : function(fresh) {
            freshSubscribers += fresh;
        },
        
        getUpdatedlines : function() {
            return linesUpdated;
        },
        
        getTotalCreated : function() {
            return totalCreated;
        },
        
        getSubWithOtherLists : function() {
            return subWithOtherLists;
        },
        
        getFreshSubscribers : function() {
            return freshSubscribers;
        },
        
        getProgress : function() {
            return 100 * linesUpdated / totalLines ;
        },
        
        updateStatus : function () {
            $('#progress-bar').css('width',this.getProgress()+'%');
            $('#progress-bar-level').html(Math.round(this.getProgress()));
            $('#totalCreated').html(this.getTotalCreated());
            $('#subWithOtherLists').html(this.getSubWithOtherLists());
            $('#freshSubscribers').html(this.getFreshSubscribers());
        }
    };
});