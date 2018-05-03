package com.bol.kalah.game;

import com.bol.kalah.Player;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.UUID;

public class Pit {

    private final UUID id;
    private int amount;
    private Player owner;
    private int index;

    public Pit(UUID id, int amount, Player owner, int index) {
        this.id = id;
        this.amount = amount;
        this.owner = owner;
        this.index = index;
    }

    public UUID getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public int getIndex() {
        return index;
    }

    public Player getOwner() {
        return owner;
    }

    void setAmount(Integer amount) {
        this.amount = amount;
    }

    int fetchStones() {
        int stones = amount;
        amount = 0;
        return stones;
    }

    void addStone() {
        this.amount += 1;
    }

    void addStones(int amount) {
        this.amount += amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (getClass() != obj.getClass()) return false;

        final Pit other = (Pit) obj;
        return Objects.equal(id, other.id)
                && Objects.equal(amount, other.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, amount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("amount", amount)
                .add("index", index)
                .add("owner", owner)
                .toString();
    }
}
