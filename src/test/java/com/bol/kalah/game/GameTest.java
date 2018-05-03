package com.bol.kalah.game;

import com.bol.kalah.GameState;
import com.bol.kalah.Player;
import com.bol.kalah.events.EndOfGameEvent;
import com.bol.kalah.events.LastStoneEvent;
import com.bol.kalah.events.StartGameEvent;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameTest {

    @Mock
    private EventBus eventBus;
    @InjectMocks
    private Game game;

    private static Pit generatePit(int amount, Player owner) {
        return new Pit(UUID.randomUUID(), amount, owner, 0);
    }

    private static ScoringPit generateScoringPit(int amount, Player owner) {
        return new ScoringPit(UUID.randomUUID(), amount, owner, 0);
    }

    @Test
    public void unstartedGame() {
        verify(eventBus).register(game);
        assertThat(game.getPlayerturn(), is(nullValue()));
        assertThat(game.getState(), is(GameState.NOT_STARTED));
    }

    @Test
    public void setPlayerTurn_returnsPlayerTurn() {
        game.setPlayerTurn(Player.B);

        assertThat(game.getPlayerturn(), is(Player.B));
    }

    @Test
    public void startGame() {
        game.startGame(new StartGameEvent());

        assertThat(game.getPlayerturn(), is(Player.A));
        assertThat(game.getState(), is(GameState.IN_PROGRESS));
    }

    @Test
    public void endGame_changesState() {
        game.endGame(new EndOfGameEvent(0, 0));

        assertThat(game.getState(), is(GameState.ENDED));
    }

    @Test
    public void lastStoneIsPlacedInNonScoringPit_changesTurn() {
        Pit testPit = spy(generatePit(4, Player.A));
        game.startGame(new StartGameEvent());
        Player playerturn = game.getPlayerturn();

        game.lastStone(new LastStoneEvent(testPit));

        assertThat(game.getPlayerturn(), not(is(playerturn)));
    }

    @Test
    public void lastStoneIsPlacedInScoringPit_anotherTurn() {
        Pit scoringPit = spy(generateScoringPit(3, Player.A));
        game.startGame(new StartGameEvent());
        Player playerturn = game.getPlayerturn();

        game.lastStone(new LastStoneEvent(scoringPit));

        assertThat(game.getPlayerturn(), is(playerturn));
    }
}