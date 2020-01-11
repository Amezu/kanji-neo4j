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

function addNewTranslationInputs() {
    var translationsTable = document.querySelector("#translations-table");
    translationsTable.innerHTML +=
    "                <tr>" +
    "                    <td><input class='translation' pattern='[a-z]+'></td>" +
    "                    <td><input class='translation' pattern='[a-zżźćńółęąś]+'></td>" +
    "                </tr>";
    document.querySelector("#translation-plus").addEventListener("click", addNewTranslationInputs);
}

document.querySelector("#add-word").addEventListener("click", function (event) {
    event.preventDefault();
    addWord();
});

document.querySelector("#translation-plus").addEventListener("click", addNewTranslationInputs);
