package com.bol.kalah.game;

import com.bol.kalah.Player;
import com.bol.kalah.events.EndOfGameEvent;
import com.bol.kalah.events.LastStoneEvent;
import com.bol.kalah.events.StartGameEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Board {
    private static final int NO_PLAYERS = 2;
    private static final int NO_SCORING_PITS = NO_PLAYERS;
    private static final int NIL_SCORE = 0;
    private static final int ZERO_BASED_OFFSET = -1;

    private int noPits = 6;
    private int noStones = 6;

    private List<Pit> pits;
    private EventBus eventBus;

    public Board(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    private static int firstPitIndex() {
        return 1 + ZERO_BASED_OFFSET;
    }

    @Subscribe
    public void startGame(StartGameEvent sge) {
        pits = new ArrayList<>(NO_PLAYERS * noPits + NO_SCORING_PITS);
        createPitsForPlayer(Player.A);
        createPitsForPlayer(Player.B);
    }

    @Subscribe
    public void checkEndOfGame(LastStoneEvent e) {
        if (getTotalStonesInPlay(Player.A) == 0 || getTotalStonesInPlay(Player.B) == 0) {
            eventBus.post(new EndOfGameEvent(getScoringPit(Player.A).getAmount(), getScoringPit(Player.A).getAmount()));
        }
    }

    public List<Pit> getPits() {
        return pits;
    }

    Pit getScoringPit(Player player) {
        return getPit(player, noPits);
    }

    Pit getPit(Player player, int index) {
        return pits.stream()
                .filter(p -> p.getOwner().equals(player))
                .filter(p -> p.getIndex() == index)
                .findFirst()
                .orElseThrow(NoSuchPit::new);
    }

    Pit getPitById(UUID id) {
        return pits.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchPit::new);
    }

    Pit opposingPit(Pit pit) {
        return getPit(pit.getOwner().getOpponent(), lastPitIndex() - pit.getIndex());
    }

    Pit getNextPit(Pit pit) {
        return getNextPit(pit.getOwner(), pit.getIndex());
    }

    private long getTotalStonesInPlay(Player pl) {
        return pits.stream()
                .filter(p -> p.getOwner().equals(pl))
                .filter(p -> !(p instanceof ScoringPit))
                .mapToInt(Pit::getAmount)
                .sum();
    }

    private Pit getNextPit(Player player, int index) {
        if (index == noPits) {
            index = firstPitIndex();
            player = player.getOpponent();
        } else {
            index++;
        }
        return getPit(player, index);
    }

    private void createPitsForPlayer(Player player) {
        for (int i = firstPitIndex(); i < noPits; i++) {
            pits.add(new Pit(UUID.randomUUID(), noStones, player, i));
        }
        pits.add(new ScoringPit(UUID.randomUUID(), NIL_SCORE, player, noPits));
    }

    private int lastPitIndex() {
        return noPits + ZERO_BASED_OFFSET;
    }

    static class NoSuchPit extends RuntimeException {
    }
}
