/**
 * This is a very important piece of code that must be applied in all pages with
 * summernote editors. It copies the content of the editor back into the original
 * textarea so that our JSF renderer can apply the contents back into the backing
 * beans.
 * 
 * @param {type} id
 * @returns {undefined}
 */
function reapply_textarea(id){
    var realTextboxParent = document.getElementById(id).nextSibling;
    /*var realContent;
    for (var i=0; i<realTextboxParent.childNodes.length; i++){
        var child = realTextboxParent.childNodes[i];
        if(child.className === 'note-editable'){
            document.getElementById(id).innerHTML = child.innerHTML;
            break;
        }
    }*/
    //Use jquery
    var editingBox = $(realTextboxParent).find('.note-editable');
    $('#'+id).html(editingBox.html());
    
    /*
    var jsfTextbox = document.getElementById(id);
    
    jsfTextbox.innerHTML = realContent; //For some weird reasons, this alone works.
    */
}
/*
$(document).ready(function () {
    var editors = document.getElementsByClassName('editor');
    for(var i=0; i<editors.length; i++) {
        var editor = editors[i];
        editor.addEventListener(
            "submit",reapply_textarea(editor.id));
    }
            
});*/