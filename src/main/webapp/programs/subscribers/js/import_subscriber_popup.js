var DELIMITER = ',';

var ERROR_COUNT_LIMIT = 10;

function bindFileInput() {
    $('#uploader').on('change.bs.fileinput', function (event) {

        //event.stopPropagation();
        //https://github.com/anpur/client-line-navigator
        var files = document.getElementById('fileUploaded').files;
        if (!files)
            return;
        var navigator = new FileNavigator(files[0]);
        navigator.readLines(0, 1, function lineReadHandler(err, partIndex, lines, eof) {
            if (err) 
                GenericErrorController.setErrors(err, 'danger');
            //Get the first line
            var firstLine = lines[0];
            if (!firstLine) {
                GenericErrorController.setErrors("Empty file.", "danger");
                return;
            }
            var headers = firstLine.split(DELIMITER);
            $('#field_selector select').empty().append($('<option>', {
                value: '',
                text: '--No field selected--'
            }));
            $.each(headers, function (i, item) {
                $('#field_selector select').append($('<option>', {
                    value: i,
                    text: item
                })).select2();
            });

            $('#field_selector').show();
            $('#importButton').show();
            $('#progress-bar-container').show();
            $('#soap-errors').show();
            $('#upload-status').show();
        });
    });
}

var validateFields = function() {
    var stop = false;
    var elems = $('#field_selector select');
    $('#field_selector select').each(function(i){
        var required = $(this).data('required');
        if($(this).data('required')) {
            $(this).siblings('label.control-label').remove();
            if(!$(this).val()) {
                stop = true;
                $(this).parent('.form-group')
                        .append('<label class="control-label" style="color:#d9534f">'
                                +'Please select a column for this field.</label>');
            } else {
                
            }
            
        }
    });
    
    return stop;
}

function startFileUpload() {
    
    if(validateFields())
        return;

    //Get the file object and create a FileNavigator wrapper
    var files = document.getElementById('fileUploaded').files;
    if (files === null || files.length <= 0) { 
        GenericErrorController.setErrors("No file selected.", "danger");
        return
    }
    PanelUpdater.reset();
    checkedErrorsCollector.reset();
    $('#soap-errors').empty();
    $('#importButton').hide();
    $('#cancelButton').show();
    //Ping the server and check if the upload was stopped halfway previously
    //by checking the file hash
    var md5Hash = '';
    checkFileHash(files[0]);
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


var checkFileHash = function (file) {
    block_refresh($('#importButton').parents(".modal-dialog"));
    $('#importButton').prop("disable", true);

    if (!file) {
        GenericErrorController.setErrors("No file selected. Please select file.", "danger");
        block_refresh($('#importButton').parents(".modal-dialog"));
        return;
    }
    var navigator = new FileNavigator(file);
    var startIndex = 0;
    var countLines = 0;

    navigator.readSomeLines(
            startIndex,
            function linesReadHandler(err, index, lines, eof, progress) {
                if (err) {
                    GenericErrorController.setErrors('FileNavigator error: ' + err, 'danger');
                    return;
                }
                countLines += lines.length;

                if (eof) {
                    PanelUpdater.setTotalLines(countLines - 1); //minus first line which is the header
                    var contents = '';
                    for (var i = 0; i < lines.length; i++) {
                        contents += lines[i];
                    }

                    checkFileStatus(file, file.name, md5(contents), successAndStart);
                    return;
                }
                navigator.readSomeLines(index + lines.length, linesReadHandler);
            }
    );

};

var checkFileStatus = function (file, name, hash, success, error) {
    $.soap({
        url: web_service_endpoint,
        method: 'checkFileStatus',
        appendMethodToURL: 0,
        data: {
            fileHash: hash,
            filename: name
        },
        namespaceQualifier: 'ns',
        namespaceURL: 'http://subscribers.program.segmail/',
        noPrefix: 0,
        HTTPHeaders: {
        },
        success: function (SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:checkFileStatusResponse']["return"];
            success(file, result);
            block_refresh($('#importButton').parents(".modal-dialog"));
        },
        error: function (SOAPResponse) {
            logSOAPErrors(SOAPResponse);
            block_refresh($('#importButton').parents(".modal-dialog"));
        }
    })
};

var successAndStart = function (file, startIndex) {

    //set buffer variables
    var readIndex = (startIndex >= 0) ? startIndex : 1;
    var navigator = new FileNavigator(file, '', {
        chunkSize: Math.pow(2, 10) * 2 //Assuming ASCII encoding 2 bytes per character
    });
    navigator.readSomeLines(
            readIndex,
            function linesReadHandler(err, index, lines, eof, progress) {
                if (err) {
                    GenericErrorController.setErrors('File read error: '+err+' at line '+index,'danger');
                    return;
                }
                //Call SOAP
                //Construct the JSON objects for each subscriber
                //Construct the container JSON object to collect all subscriber objects
                //Call Webservice
                var batch = constructSubscribers(lines, index);
                sendBatchToWS(batch, function (totalProcessed, success, errors) {
                    //PanelUpdater.updateLines(lines.length);
                    PanelUpdater.updateTotalProcessed(totalProcessed);
                    PanelUpdater.updateSuccess(success);
                    PanelUpdater.updateError(errors);
                    PanelUpdater.updateStatus();
                    checkedErrorsCollector.updateErrorList();
                    checkedErrorsCollector.generateErrorFile(file);
                });

                if (eof) {
                    return;
                }

                navigator.readSomeLines(index + lines.length, linesReadHandler);
            });
};

var constructSubscribers = function (array, startIndex) {
    //get mapping first
    var mapping = {};
    $('select.select2').each(function (i, item) {
        var id = item.id;
        var val = item.value;
        if (val) {
            var idStore = mapping[val];
            if (!idStore) {
                idStore = [];
            }
            idStore[idStore.length] = id; //id = the key of the field
            mapping[val] = idStore; //val = position in file
        }
    });

    //Initialize the container object
    var container = {};

    //For each item in array, pass in a function to construct and add in the JSON object for subscriber
    $.each(array, function (i, row) {
        var newObj = {
            sno: i + startIndex
        };
        var delimitedRow = row.split(DELIMITER);
        $.each(mapping, function (j, node) {
            if (!node)
                return;

            $.each(node, function (k, field) {
                var str = delimitedRow[j];
                if (str && str.charAt(0) === '"')
                    str = str.substring(1, str.length); //Remove opening "
                if (str && str.charAt(str.length - 1) === '"')
                    str = str.substring(0, str.length - 1); //Remove closing "

                //str.replace(/(\r\n|\n|\r)/gm,""); //Remove line breaks
                newObj[field] = str;
            });
        });
        container[i] = newObj;

        //update progress

    });
    return container;
};

var sendBatchToWS = function (batch, successCallback, errorCallback) {
    $.soap({
        url: web_service_endpoint,
        method: 'addSubscribers',
        appendMethodToURL: 0,
        data: {
            subscribers: JSON.stringify(batch)
        },
        namespaceQualifier: 'ns',
        namespaceURL: 'http://subscribers.program.segmail/',
        noPrefix: 0,
        HTTPHeaders: {
        },
        success: function (SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();

            //Retrieve these numbers from xmlResults
            //successCallback(totalCreated,existing,fresh);
            logCheckedErrors(SOAPResponse, successCallback);

        },
        error: function (SOAPResponse) {
            logSOAPErrors(SOAPResponse);
            if (errorCallback)
                errorCallback();

        }
    })
};

var logSOAPErrors = function (SOAPResponse) {
    var jsonresult = SOAPResponse.toJSON();

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
    //if($('#soap-errors').find('.alert').length <= 0)
    //    $('#soap-errors').append('<div class="alert alert-'+severity+'"><strong>'+errorMessage+'</strong></div>');
    GenericErrorController.setErrors(errorMessage, severity);
};

var logCheckedErrors = function (SOAPResponse, callback) {
    var jsonresult = SOAPResponse.toJSON();

    var responseObject = JSON.parse(jsonresult['#document']['S:Envelope']['S:Body']["ns2:addSubscribersResponse"]["return"]);
    var totalCount = 0;
    var errors = 0;

    var keys = Object.keys(responseObject);
    for (var i = 0; i < keys.length; i++) {
        if (keys[i] === 'total') {
            totalCount = responseObject[keys[i]];
            continue;
        }
        if (keys[i] === 'errors') {
            errors = responseObject[keys[i]];
            continue;
        }

        var values = responseObject[keys[i]];
        values.sort(function (a, b) {
            return a['sno'] - b['sno'];
        });
        for (var j = 0; j < values.length; j++) {
            checkedErrorsCollector.addLine(keys[i], values[j]['sno']);
        }

    }
    if (callback)
        callback(totalCount, totalCount - errors, errors);
}

var PanelUpdater = (function () {
    var totalLines = 0;
    var totalProcessed = 0;
    var totalSuccess = 0;
    var totalError = 0;

    return {
        setTotalLines: function (lines) {
            totalLines = lines;
        },
        getTotalLines: function () {
            return totalLines;
        },
        updateTotalProcessed: function (processed) {
            totalProcessed += processed;
        },
        updateSuccess: function (success) {
            totalSuccess += success;
        },
        updateError: function (error) {
            totalError += error;
        },
        getTotalProcessed: function () {
            return totalProcessed;
        },
        getSuccess: function () {
            return totalSuccess;
        },
        getError: function () {
            return totalError;
        },
        getProgress: function () {
            return (totalLines > 0) ? 100 * totalProcessed / totalLines : 0;
        },
        updateStatus: function () {
            $('#progress-bar').css('width', this.getProgress() + '%');
            $('#progress-bar-level').html(Math.round(this.getProgress()));
            $('#totalProcessed').html(this.getTotalProcessed());
            $('#totalSuccess').html(this.getSuccess());
            $('#totalError').html(this.getError());
        },
        reset: function () {
            totalLines = 0;
            totalProcessed = 0;
            totalSuccess = 0;
            totalError = 0;
            this.updateStatus();
        }
    };
})();

var processError500 = function (JsonResult) {
    var faultstring = JsonResult['#document']['S:Envelope']['S:Body']['S:Fault']["faultstring"];
    if (faultstring.indexOf('UserLoginException') > -1) //if starts with java.lang.RuntimeException: eds.component.user.UserLoginException: Please enter username.
        return "Please log in again.";
    switch (faultstring) {
        default    :
            return 'Error occurred at server side: ' + faultstring;
    }
};

var checkedErrorsCollector = (function () {
    var container = 'error-messages';
    var fileDownloadContainerId = 'error-file-download';
    var errorItemList = {};
    var outputErrorFileContent = '';

    return {
        addError: function (errorMessage) {
            var id = this.getId(errorMessage);
            if ($('#' + container).find('#' + id).length <= 0)
                $('#' + container).append(
                        '<div id=' + id + '>'
                        + '<p>\"<strong>' + errorMessage + '</strong>\" at:</p>'
                        + '</div>'
                        );
        },
        addLine: function (errorMessage, lineNum) {
            this.addError(errorMessage);
            if (!errorItemList[errorMessage])
                errorItemList[errorMessage] = [];
            errorItemList[errorMessage].push(lineNum);
        },
        getId: function (errorMessage) {
            var words = errorMessage.split(/[\s,.]+/);
            var id = '';
            for (var i = 0; i < words.length && i < 3; i++) {
                if (i > 0)
                    id += '-';
                id += words[i];
            }
            return id;
        },
        updateErrorList: function () {
            var keys = Object.keys(errorItemList);
            $.each(keys, function (i, key) {
                errorItemList[key].sort(function (a, b) {
                    return a - b;
                });
                var id = checkedErrorsCollector.getId(key);
                $('#' + id).empty();
                $('#' + id).append('<p><strong>' + key + '</strong></p>');
                $('#' + id).append('<ul></ul>');
                for (var i = 0; i < errorItemList[key].length; i++) {
                    if (i >= ERROR_COUNT_LIMIT) {
                        $('#' + id + ' ul').append('<li><em>...and ' + (errorItemList[key].length - i) + ' more</em></li>');
                        return;
                    }
                    var lineNum = errorItemList[key][i];
                    $('#' + id + ' ul').append('<li>Line number ' + lineNum + '</li>');
                }
            });
        },
        generateErrorFile: function (file) {
            $('#' + fileDownloadContainerId).empty();
            if (PanelUpdater.getProgress() < 100) {
                return;
            }

            $('#doneButton').show();

            //Re-arrange the lines with errors into a sorted array
            var sortedArray = [];
            var keys = Object.keys(errorItemList);
            for (var i = 0; i < keys.length; i++) {
                var key = keys[i];
                for (var j = 0; j < errorItemList[key].length; j++) {
                    var line = errorItemList[key][j];
                    sortedArray[sortedArray.length] = {
                        sno: line,
                        error: key
                    };
                }
            }
            sortedArray.sort(function sortFn(a, b) {
                return a['sno'] - b['sno'];
            });

            if (sortedArray.length <= 0)
                return;

            //Start our awesome algorithm
            //Forget awesome, think simple
            var bufferStart = 0;
            var bufferEnd = 0;
            var navigator = new FileNavigator(file);

            navigator.readSomeLines(0, function callback(err, index, lines, eof, progress) {
                if (err) {
                    GenericErrorController.setErrors(err, 'danger');
                    return;
                }

                //Write the first line
                if (index === 0) {
                    outputErrorFileContent += lines[0] + '\r\n';
                }

                //Assume that sortedArray is always smaller than file
                for (var i = 0; i < sortedArray.length; i++) {
                    var errorLine = sortedArray[i]['sno'] - index;//index is the offest
                    if (errorLine < 0 || errorLine >= lines.length)
                        continue;
                    var actualLine = lines[errorLine];
                    var errorMessage = sortedArray[i]['error'];
                    outputErrorFileContent += actualLine + ',' + errorMessage + '\r\n';
                }

                if (eof) {
                    $('#' + fileDownloadContainerId).append('<a id=\"' + fileDownloadContainerId + '-link\">Click here to download error file.</a>');
                    $('#' + fileDownloadContainerId + '-link').attr('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(outputErrorFileContent));
                    $('#' + fileDownloadContainerId + '-link').attr('download', file.name);
                    return;
                }

                navigator.readSomeLines(index + lines.length, callback);
            });

        },
        reset: function () {
            errorItemList = {};
            outputErrorFileContent = '';
            $('#' + container).empty();
            $('#' + fileDownloadContainerId).empty();
        }
    };

})();

var WSController = (function () {
    var MAX_NUM_CALLS = 10;
    var count = 0;

    return {
        incrementCallCounts: function () {
            count++;
        },
        decrementCallCounts: function () {
            count--;
        },
        checkCall: function (callback) {
            while (count >= MAX_NUM_CALLS) {

            }
            callback();
        },
        reset: function () {
            count = 0;
        }
    }
})();

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

function refreshImport(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $('#FormImportSubscribers .modal-dialog');
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.

            break;

        case "success": // This is called when ajax response is successfully processed.
            //block_refresh(block);
            bindFileInput();
            initUIElem();
            refresh_select2();
            break;
    }
};

var initUIElem = function() {
    $('#fileUploaded').val('');//To clear the previous uploaded file
    $('#field_selector').hide();
    $('#importButton').hide();
    $('#progress-bar-container').hide();
    $('#soap-errors').hide();
    $('#upload-status').hide();
    $('#doneButton').hide();
    $('#cancelButton').hide();
}

$(document).ready(function () {
    
});