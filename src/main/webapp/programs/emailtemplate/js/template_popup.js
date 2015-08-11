var popupType = (function() {

    var title_id;
    var title;
    var type_id;
    var type;

    return {
        
        init : function(title_id, title, type_id, type) {
            
            this.title_id = title_id;
            this.title = title;
            this.type_id = type_id;
            this.type = type;
        },
        
        popup : function() {
            
            document.getElementById(this.title_id).innerHTML = this.title;
            //document.getElementById(title_id).value = title;

            var TYPE = this.type.toUpperCase();
            document.getElementById(this.type_id).value = TYPE;
        }

    };
});

//Global representation
var popup = new popupType();