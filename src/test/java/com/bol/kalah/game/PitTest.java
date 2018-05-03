package com.bol.kalah.game;

import com.bol.kalah.Player;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PitTest {

    @Test
    public void initializedGame() {
        UUID id = UUID.randomUUID();
        Pit pit = new Pit(id, 12, Player.B, 3);

        assertThat(pit.getAmount(), is(12));
        assertThat(pit.getOwner(), is(Player.B));
        assertThat(pit.getIndex(), is(3));
        assertThat(pit.getId(), is(id));
    }

    @Test
    public void fetchingStones_fetchesAllStones() {
        int amount = 8;
        Pit pit = new Pit(UUID.randomUUID(), amount, Player.A, 0);

        int fetched = pit.fetchStones();

        assertThat(fetched, is(amount));
        assertThat(pit.getAmount(), is(0));
    }

    @Test
    public void addStone_addsStone() {
        int amount = 8;
        Pit pit = new Pit(UUID.randomUUID(), amount, Player.A, 0);

        pit.addStone();

        assertThat(pit.getAmount(), is(amount + 1));
    }

    @Test
    public void addStones_addsStones() {
        int amount = 5;
        int add = 4;
        Pit pit = new Pit(UUID.randomUUID(), amount, Player.A, 0);

        pit.addStones(add);

        assertThat(pit.getAmount(), is(amount + add));
    }

    @Test
    public void setAmount_getsAmount() {
        int amount = 42;
        Pit pit = new Pit(UUID.randomUUID(), 1, Player.A, 2);

        pit.setAmount(amount);

        assertThat(pit.getAmount(), is(amount));
    }
}