package com.bol.kalah.game;

import com.bol.kalah.Player;
import com.bol.kalah.game.Pit;

import java.util.UUID;

public class ScoringPit extends Pit {
    ScoringPit(UUID id, int amount, Player player, int index) {
        super(id, amount, player, index);
    }
}
