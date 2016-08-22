/*var resend = function() {
    $.ajax({
        type : 'POST',
        url : window.location.pathname+'/resend',
        dataType : "json",
        beforeSend : function(data) {
            
            $('#resend-panel')
                    .empty()
                    //.css('text-align','center')
                    .append('<div class="uil-ring-css" style="transform:scale(0.4);"><div></div></div>');
        },
        complete : function(data) {
            $('#resend-panel').empty();
        },
        success : function(data) {
            $('#resend-panel').append(
                    '<p>The confirmation email has been resent to your email. Please check your inbox in a while.</p>');
        },
        error : function(data) {
            $('#resend-panel').empty();
            $('#resend-panel').append(
                    '<p>Oops...an error has occured. Please go back and retry or contact us at <u>support [at] segmail.io</u>');
        }
    })
};*/

var resend = function(data) {
    var ajaxstatus = data.status;
    
    switch (ajaxstatus) {
        case "before" : 
            break;
        case "begin" : 
            $('#resend-panel')
                    .empty()
                    //.css('text-align','center')
                    .append('<div class="uil-ring-css" style="transform:scale(0.4);"><div></div></div>');
            break;
        case "success" :
            $('#resend-panel')
                    .empty()
                    .append('<p>The confirmation email has been resent to your email. Please check your inbox in a while.</p>');
            break;
        case "error" :
            $('#resend-panel').empty();
            $('#resend-panel').append(
                    '<p>Oops...an error has occured. Please go back and retry or contact us at <u>support [at] segmail.io</u>');
            break;
    }
}