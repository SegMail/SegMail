
var addNew = function(data){
    var ajaxStatus = data.status; 
    var block = $(data.source).parents(".modal-dialog");
    
    switch (ajaxStatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block); 
            
            break;
    }
};