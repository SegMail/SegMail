/*$(document).ready(function () {
    $("#add_new_auto_email_form").reset();
    $("#edit_auto_email_form").reset();
    add_new_auto_email_form.form();
    edit_auto_email_form.form();
});*/

var add_new_auto_email_form =
        $("#add_new_auto_email_form").validate({
    rules: {
        subject: {
            required: true},
        body: {
            required: true,
            minlength: 10
        }

    }
});

var edit_auto_email_form =
        $("#edit_auto_email_form").validate({
    rules: {
        subject: {
            required: true},
        body: {
            required: true,
            minlength: 10
        }
    }
});
