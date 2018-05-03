package com.bol.kalah;

public enum Player {
    A(1),
    B(0);

    static {
        A.setOther(B);
        B.setOther(A);
    }

    private Player other;
    private int number;

    Player(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public Player getOpponent() {
        return other;
    }

    private void setOther(Player o) {
        other = o;
    }
}
