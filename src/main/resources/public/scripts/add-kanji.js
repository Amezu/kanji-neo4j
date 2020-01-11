"use strict";

function addKanji() {
    var inputs = document.querySelectorAll("input");

    var character = inputs[0].value;
    var reading = inputs[1].value;
    var strokes = inputs[2].value;

    console.log("Adding kanji " + character);

    $.ajax({
        url: "/kanji/add?char=" + character + "&strokes=" + strokes + "&read=" + reading,
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

document.querySelector("#submit-kanji").addEventListener("click", function (event) {
    event.preventDefault();
    addKanji();
})
