function login(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");


    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            blockRefresh(block);
            //$('.btn-block').prop('disabled',true);
            
            break;

        case "complete": // This is called right after ajax response is received.
            blockRefresh(block);
            //$('.btn-block');
            break;

        case "success": // This is called when ajax response is successfully processed.
            break;
    }
}

var blockRefresh = function(block) {
           
    if(!block.hasClass("block-refreshing")){
        //block.append('<div class="block-refresh-layer"><i class="fa fa-spinner fa-spin"></i></div>');
        block.append('<div class="spinner-overlay" ></div><p class="spinner" ></p>');
        
        block.find(".spinner-overlay")
                .width(block.innerWidth())
                .height(block.innerHeight())
                //.css('top',block.pos)
                //.css('left',block.offset().left);
        block.addClass("block-refreshing");
    }else{
        block.find(".spinner-overlay").remove();
        block.find(".spinner").remove();
        block.removeClass("block-refreshing");
    }    
}