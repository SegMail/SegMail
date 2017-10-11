var blockRefresh = function(block) {
           
    if(!block.hasClass("block-refreshing")){
        block.append('<div class="spinner-overlay" ></div><p class="spinner" ></p>');
        
        block.find(".spinner-overlay")
                .width(block.innerWidth())
                .height(block.innerHeight())
        block.addClass("block-refreshing");
    }else{
        block.find(".spinner-overlay").remove();
        block.find(".spinner").remove();
        block.removeClass("block-refreshing");
    }    
}