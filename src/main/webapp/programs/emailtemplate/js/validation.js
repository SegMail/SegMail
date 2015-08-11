$(document).ready(function () {
    add_new_template_form();
});

var add_new_template_form =
        $("#add_new_template_form").validate({
    rules: {
        subject: {
            required: true},
        body: {
            required: true,
            minlength: 10
        }

    },
    errorPlacement: function (error, element) {
        if (element.attr("name") == "gender" || element.attr("name") == "rules") {
            element.parents(".form-gr oup").append(error);
        } else
            error.insertAfter(element);
    }
});
    