package com.bol.kalah.events;

public class WrongPlayerEvent implements GameEvent {
    @Override
    public String getName() {
        return "wrongplayer";
    }
}
