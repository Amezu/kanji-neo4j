"use strict";

function editKanji() {
    var inputs = document.querySelectorAll("input");

    var reading = inputs[0].value;
    var strokes = inputs[1].value;

    $.ajax({
        url: "update?strokes=" + strokes + "&read=" + reading,
        success: function (result) {
            alert(result);
            $("form")[0].reset();
            location.href += "/..";
        },
        error: function (e) {
            alert(e.responseText);
        }
    });
}

$("form").submit(function (event) {
    event.preventDefault();
    editKanji();
    return false;
});
