package com.bol.kalah.events;

import com.bol.kalah.game.Pit;

public class LastStoneEvent {
    private Pit pit;

    public LastStoneEvent(Pit pit) {
        this.pit = pit;
    }

    public Pit getPit() {
        return pit;
    }
}
