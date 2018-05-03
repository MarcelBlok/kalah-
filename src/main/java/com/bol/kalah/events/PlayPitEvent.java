package com.bol.kalah.events;

import java.util.UUID;

public class PlayPitEvent implements GameEvent {
    private final UUID id;

    public PlayPitEvent(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return "playpit";
    }
}
