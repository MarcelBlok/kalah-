package com.bol.kalah.events;

import com.bol.kalah.game.Pit;
import com.bol.kalah.Player;
import org.springframework.boot.jackson.JsonComponent;

public class PutStonesEvent implements GameEvent {
    private final int no;
    private final Pit pit;
    private Player playerTurn;

    public PutStonesEvent(int no, Pit pit, Player turn) {
        this.no = no;
        this.pit = pit;
        this.playerTurn = turn;
    }

    public int getNumberOfStones() {
        return no;
    }

    public Pit getPit() {
        return pit;
    }

    public Player getPlayerTurn() {
        return playerTurn;
    }

    @Override
    public String getName() {
        return "putstones";
    }
}
