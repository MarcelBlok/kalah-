$(function() {
    var frozen = false;

    $("[pit-area]").click(function () {
        if (!frozen) {
            frozen = true;
            $.post("/play/" + $(this).attr("pit-id"), function (data) {
                data.push({name: "endofturn"});
                playEvents(data);
            });
        }
    });

    function playEvents(events) {
        event = events.shift();

        switch (event.name) {
            case "wrongplayer":
                alert("It is not your turn !!!");
                break;
            case "playpit":
                var pit = $("[pit-id='" + event.id + "']");
                pit.animate({backgroundColor: "#00CC00"}, 300);
                pit.find(".label").text("0");
                break;
            case "putstones":
                var pit = $("[pit-id='" + event.pit.id + "']");
                var amount = Number(pit.attr("pit-amount")) + 1;
                pit.animate({backgroundColor: "#0000CC"}, 300);
                $("[pit-id='" + event.pit.id + "'] .label").text(amount);
                break;
            case "endofgame":
                alert("Game Over\n\nFinal score\nPlayer A: " + event.scoreA + "\nPlayer B: " + event.scoreB);
                window.location.href = "/newgame";
                break;
            case "endofturn":
            default:
                window.location.href = "/";
        }

        setTimeout(function () {
            playEvents(events)
        }, 300);
    }
});