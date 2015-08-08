$(document).ready(function(){
    $('#edit-button').click(function(){
        $('#talent .editable').editable('toggleDisabled');
        //Change the button to a save button

    });

    $('#talentname').editable({
            type: 'text',
            pk: 1,
            name: 'username',
            title: 'Enter username',
            disabled: true
     });
});