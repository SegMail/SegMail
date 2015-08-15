$(document).ready(function () {
    add_new_template_form.form();
    edit_template_form.form();
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

    }
});

var edit_template_form =
        $("#edit_template_form").validate({
    rules: {
        subject: {
            required: true},
        body: {
            required: true,
            minlength: 10
        }
    }
});