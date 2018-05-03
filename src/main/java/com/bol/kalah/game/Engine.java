package com.bol.kalah.game;

import com.bol.kalah.GameState;
import com.bol.kalah.Player;
import com.bol.kalah.events.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

@Component
public class Engine {
    private EventBus eventBus;
    private Board board;
    private Game game;

    Engine(EventBus eventBus, Board board, Game game) {
        this.eventBus = eventBus;
        this.board = board;
        this.game = game;

        eventBus.register(this);
    }

    @Subscribe
    public void playPit(PlayPitEvent e) {
        if (isGameInProgress()) {
            playPit(board.getPitById(e.getId()));
        } else {
            eventBus.post(new NoGameInProgress());
        }
    }

    @Subscribe
    public void putStones(PutStonesEvent pse) {
        Pit p = getNextPossiblePit(pse);
        p.addStone();

        if (isLastStone(pse)) {
            handleLastStone(p, pse.getPlayerTurn());
        } else {
            putNextStone(p, pse.getPlayerTurn(), pse.getNumberOfStones());
        }
    }

    @Subscribe
    public void capture(CaptureEvent ce) {
        board.getScoringPit(ce.getPlayerTurn()).addStones(board.opposingPit(ce.getPit()).fetchStones());
        board.getScoringPit(ce.getPlayerTurn()).addStones(ce.getPit().fetchStones());
    }

    private void playPit(Pit pit) {
        if (pitIsOfPlayerturn(pit)) {
            if (isEmptyPit(pit)) {
                eventBus.post(new CannotPlayEmptyPit());
            } else {
                eventBus.post(new PutStonesEvent(pit.fetchStones(), board.getNextPit(pit), pit.getOwner()));
            }
        } else {
            eventBus.post(new WrongPlayerEvent());
        }
    }

    private boolean isEmptyPit(Pit pit) {
        return pit.getAmount() == 0;
    }

    private boolean pitIsOfPlayerturn(Pit pit) {
        return pit.getOwner().equals(game.getPlayerturn());
    }

    private boolean isGameInProgress() {
        return game.getState().equals(GameState.IN_PROGRESS);
    }

    private Pit getNextPossiblePit(PutStonesEvent pse) {
        Pit p = pse.getPit();
        if (isOpponentsScoringPit(pse.getPlayerTurn(), p)) {
            p = board.getNextPit(p);
        }
        return p;
    }

    private void putNextStone(Pit pit, Player turn, int noStones) {
        eventBus.post(new PutStonesEvent(noStones - 1, board.getNextPit(pit), turn));
    }

    private void handleLastStone(Pit p, Player turn) {
        if (addedStoneToEmptyPit(p) && game.getPlayerturn().equals(p.getOwner())) {
            eventBus.post(new CaptureEvent(p, turn));
        }
        eventBus.post(new LastStoneEvent(p));
    }

    private boolean isOpponentsScoringPit(Player p, Pit nextPit) {
        return nextPit instanceof ScoringPit && nextPit.getOwner() != p;
    }

    private boolean addedStoneToEmptyPit(Pit p) {
        return p.getAmount() == 1;
    }

    private boolean isLastStone(PutStonesEvent e) {
        return e.getNumberOfStones() == 1;
    }
}

