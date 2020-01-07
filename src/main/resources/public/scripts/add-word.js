"use strict";

function addWord() {
    var inputs = document.querySelectorAll("input");

    var japanese = inputs[0].value;
    var romaji = inputs[1].value;
    var translation = inputs[2].value;
    var polish = inputs[3].value;

    if (polish != "") translation += "_" + polish;

    console.log("Adding word " + japanese);

    $.ajax({
        url: "/word/add?jp=" + japanese + "&ro=" + romaji + "&en=" + translation,
        success: function (result) {
            alert(result);
            $("form")[0].reset();
            location.reload();
        },
        error: function (e) {
            alert(e.responseText);
            location.reload();
        }
    });
}

document.querySelector("button").addEventListener("click", function (event) {
    event.preventDefault();
    addWord();
})
