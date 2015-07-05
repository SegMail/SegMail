$(document).ready(function () {
    $("form.validate-form").validationEngine('attach',
            {promptPosition: "topRight",
                ajaxFormValidation: true,
                validationEventTrigger: "blur"});
});
