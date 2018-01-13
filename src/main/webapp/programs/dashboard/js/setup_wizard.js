var initSetupWizard = function () {
    
    if($("#form-wizard2").length > 0){
        $("#form-wizard2").bootstrapWizard({"tabClass": "form-wizard-levels"});
    }
    
    if ($("#form-wizard").length > 0) {

        var $validator = $("#FormSetupWizard").validate({
            rules: {
                login: {
                    required: true,
                    minlength: 2,
                    maxlength: 8
                },
                password: {
                    required: true,
                    minlength: 5,
                    maxlength: 10
                },
                repassword: {
                    required: true,
                    minlength: 5,
                    maxlength: 10,
                    equalTo: "#password"
                },
                email: {
                    required: true,
                    email: true
                },
                name: {
                    required: true,
                    maxlength: 10
                },
                about: {
                    required: true
                }
            }
        });

        $('#form-wizard').bootstrapWizard({
            'tabClass': 'form-wizard-levels',
            'onNext': function (tab, navigation, index) {
                var $valid = $("#FormSetupWizard").valid();
                if (!$valid) {
                    $validator.focusInvalid();
                    return false;
                }
                // Call webservice to execute update
                var rsName = $(tab).find('a').data('rsname');
                if(rsName) {
                    block_refresh(navigation);
                    $.ajax({
                        type: 'POST',
                        url: CONTEXT_PATH + '/rest/wizard/' + rsName,
                        data: $('#FormSetupWizard').serialize(),
                        
                        success: function(data) {
                            block_refresh(navigation);
                            return callbackSuccess[rsName](data);
                        },
                        error: function(error) {
                            block_refresh(navigation);
                            return callbackError[rsName](error);
                        }
                     });
                }
                
                // move these to callbacks
                var $total = navigation.find('li').length;
                var $current = index + 1;
                if ($current >= $total) {
                    $('#wizard-validate').find('.pager .next').hide();
                    $('#wizard-validate').find('.pager .finish').show();
                } else {
                    $('#wizard-validate').find('.pager .next').show();
                    $('#wizard-validate').find('.pager .finish').hide();
                }
            },
            onTabClick: function (tab, navigation, index) {
                return false;
            }
        });

    }
}

var substringMatcher = function(strs) {
  return function findMatches(q, cb) {
    var matches, substringRegex;

    // an array that will be populated with substring matches
    matches = [];

    // regex used to determine if a string contains the substring `q`
    substrRegex = new RegExp(q, 'i');

    // iterate through the pool of strings and for any string that
    // contains the substring `q`, add it to the `matches` array
    $.each(strs, function(i, str) {
      if (substrRegex.test(str)) {
        matches.push(str);
      }
    });

    cb(matches);
  };
};

var initTypeaheadAddress = function() {
    $('#email').typeahead({
        source : substringMatcher(existing_addresses)
    });
}

var callbackSuccess = {
    address : function(data) {
        messageNoty(data.message,data.result);
        
        if(data.result === 'success') return true;
        if(data.result === 'warning') return true;
        if(data.result === 'info') return true;
        
        return false;
    },
    list : function(data) {
        
    },
    auto : function(data) {
        
    },
    collect : function(data) {
        
    },
    send : function(data) {
        
    }
}

var callbackWarning = {
    address : function(data) {
        
    },
    list : function(data) {
        
    },
    auto : function(data) {
        
    },
    collect : function(data) {
        
    },
    send : function(data) {
        
    }
}

var callbackError = {
    address : function(data) {
        messageNoty(data.message,data.result);
        return false;
    },
    list : function(data) {
        
    },
    auto : function(data) {
        
    },
    collect : function(data) {
        
    },
    send : function(data) {
        
    }
}

var messageNoty = function(message,msgType) {
    noty({
        text: message,
        layout : 'topCenter',
        type : msgType,
        timeout : function() {
            switch(msgType){
                case 'success' : 3000;
                case 'warning' : 3000;
                case 'error'   : 5000;
            }
        }
    });
}

$(document).ready(function(){
    initSetupWizard();
    initTypeaheadAddress();
})