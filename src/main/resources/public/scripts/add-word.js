"use strict";

function addWord() {
    var inputs = document.querySelectorAll("input");

    var japanese = inputs[0].value;
    var romaji = inputs[1].value;

    var translations = [];
    for(var i = 2; i < inputs.length; i += 2) {
        var translation = inputs[i].value;
        var polish = inputs[i+1].value;

        if (polish != "") translation += "_" + polish;
        if (translation != "") translations.push(translation);
    }

    console.log("Adding word " + japanese);

    $.ajax({
        url: "/word/add?jp=" + japanese + "&ro=" + romaji + "&en=" + translations,
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

function addNewTranslationInputs() {
    var translationsTable = document.querySelector("#translations-table");
    var translationRow = document.createElement("TR");
    translationRow.innerHTML =
    "                    <td><input pattern='[a-z]+'></td>" +
    "                    <td><input pattern='[a-zżźćńółęąś]+'></td>";
    translationsTable.appendChild(translationRow);
}

document.querySelector("#submit-word").addEventListener("click", function (event) {
    event.preventDefault();
    addWord();
});

document.querySelector("#translation-plus").addEventListener("click", function (event) {
  event.preventDefault();
  addNewTranslationInputs();
});
