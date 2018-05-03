package com.bol.kalah.events;

import com.bol.kalah.game.Pit;
import com.bol.kalah.Player;

public class CaptureEvent {
    private Pit pit;
    private Player turn;

    public CaptureEvent(Pit pit, Player turn) {
        this.pit = pit;
        this.turn = turn;
    }

    public Pit getPit() {
        return pit;
    }

    public Player getPlayerTurn() {
        return turn;
    }
}
