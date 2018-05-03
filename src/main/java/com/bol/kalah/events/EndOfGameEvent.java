package com.bol.kalah.events;

public class EndOfGameEvent implements GameEvent{
    private final int scoreA;
    private final int scoreB;

    public EndOfGameEvent(int scoreA, int scoreB) {
        this.scoreA = scoreA;
        this.scoreB = scoreB;
    }

    public int getScoreA() {
        return scoreA;
    }

    public int getScoreB() {
        return scoreB;
    }

    @Override
    public String getName() {
        return "endofgame";
    }
}
