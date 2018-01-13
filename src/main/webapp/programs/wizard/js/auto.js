var refresh_summernote = function (selector) {
    $(selector).summernote({
        height: 260, //try the CSS flex approach next time
        toolbar: [
             ["font", ["bold", "italic", "underline","style"]],
             ["test", ["picture", "link"]],
             ["para", ["ol", "ul", "paragraph", "height"]],
             ["misc", ["codeview", "help", "MailMerge"]]
        ],
        MailMerge: {
            tags: function() {
                var allTagsAndLinks = [];
                for(var key in mailmergeTagsSubscriber) {
                    if (mailmergeTagsSubscriber.hasOwnProperty(key)) {
                        allTagsAndLinks.push(key);
                    }
                }
                for(var key in listTags) {
                    if (listTags.hasOwnProperty(key)) {
                        allTagsAndLinks.push(listTags[key]);
                    }
                }
                for(var key in mailmergeLinks) {
                    if (mailmergeLinks.hasOwnProperty(key)) {
                        allTagsAndLinks.push(key);
                    }
                }
                return allTagsAndLinks;
            }()
        }
    });
}

var autoForm = function() {
    return $("#ProgramSetupWizard").validate({
        rules: {
            'ProgramSetupWizard:confirm_subject': {
                required: true
            },
            'ProgramSetupWizard:welcome_subject': {
                required: true
            }
        }
    });
};

$(document).ready(function () {
    refresh_summernote('textarea.editor');
})