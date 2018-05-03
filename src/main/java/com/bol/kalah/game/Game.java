package com.bol.kalah.game;

import com.bol.kalah.GameState;
import com.bol.kalah.Player;
import com.bol.kalah.events.EndOfGameEvent;
import com.bol.kalah.events.LastStoneEvent;
import com.bol.kalah.events.StartGameEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

@Component
public class Game {

    private Player playerTurn;
    private GameState state = GameState.NOT_STARTED;

    public Game(EventBus eventBus) {
        eventBus.register(this);
    }

    @Subscribe
    public void lastStone(LastStoneEvent e) {
        if (e.getPit() instanceof ScoringPit && e.getPit().getOwner() == playerTurn) {
            playerTurn = playerTurn;
        } else {
            playerTurn = playerTurn.getOpponent();
        }
    }

    @Subscribe
    public void startGame(StartGameEvent e) {
        playerTurn = Player.A;
        state = GameState.IN_PROGRESS;
    }

    @Subscribe
    public void endGame(EndOfGameEvent e) {
        state = GameState.ENDED;
    }

    public Player getPlayerturn() {
        return playerTurn;
    }

    public GameState getState() {
        return state;
    }

    void setPlayerTurn(Player player) {
        playerTurn = player;
    }
}
