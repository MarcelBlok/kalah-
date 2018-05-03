package com.bol.kalah.game;

import com.bol.kalah.Player;
import com.bol.kalah.events.EndOfGameEvent;
import com.bol.kalah.events.LastStoneEvent;
import com.bol.kalah.events.StartGameEvent;
import com.google.common.eventbus.EventBus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.bol.kalah.PostedEvents.assertEventsPosted;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class BoardTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Captor
    private ArgumentCaptor eventCaptor;

    @Mock
    private EventBus eventBus;
    @InjectMocks
    private Board board;

    @Test
    public void startGame_startsGame() {
        startGame();

        assertBoardSetup(Player.A);
        assertBoardSetup(Player.B);
    }

    @Test
    public void getPit_getsPit() {
        startGame();
        assertGetCorrectPit(Player.A);
        assertGetCorrectPit(Player.B);
    }

    @Test
    public void getPitById_getsPit() {
        startGame();

        var player = Player.A;
        for (int i = 0; i < 6; i++) {
            var pit = board.getPit(player, i);
            assertThat(board.getPitById(pit.getId()), is(pit));
        }
    }

    @Test
    public void cannotGetNonExistingPit() {
        expectedException.expect(Board.NoSuchPit.class);
        startGame();
        board.getPit(Player.A, 7);
    }

    @Test
    public void opposingPits_lineUp() {
        startGame();

        assertOpposingPits(Player.A);
        assertOpposingPits(Player.B);
    }

    @Test
    public void nextNonScoringPit_getsNextPit() {
        startGame();

        assertNextNonScoringPits(Player.A);
        assertNextNonScoringPits(Player.B);
    }

    @Test
    public void nextScoringPit_getsNextPit() {
        startGame();

        assertThat(board.getNextPit(board.getPit(Player.A, 6)).getIndex(), is(0));
        assertThat(board.getNextPit(board.getPit(Player.A, 6)).getOwner(), is(Player.B));

        assertThat(board.getNextPit(board.getPit(Player.B, 6)).getIndex(), is(0));
        assertThat(board.getNextPit(board.getPit(Player.B, 6)).getOwner(), is(Player.A));
    }

    @Test
    public void allPitsEmptyForPlayerA_triggersEndOfGame() {
        startGame();
        for (int i = 0; i < 6; i++) {
            board.getPit(Player.A, i).fetchStones();
        }

        board.checkEndOfGame(new LastStoneEvent(mock(Pit.class)));

        assertEventsPosted(eventBus, eventCaptor, EndOfGameEvent.class);
    }

    private void startGame() {
        board.startGame(new StartGameEvent());
    }

    private void assertNextNonScoringPits(Player player) {
        for (int i = 0; i < 5; i++) {
            assertThat(board.getNextPit(board.getPit(player, i)).getIndex(), is(i + 1));
            assertThat(board.getNextPit(board.getPit(player, i)).getOwner(), is(player));
        }
    }

    private void assertGetCorrectPit(Player player) {
        for (int i = 0; i < 6; i++) {
            assertThat(board.getPit(player, i).getIndex(), is(i));
            assertThat(board.getPit(player, i).getOwner(), is(player));
        }
    }

    private void assertOpposingPits(Player player) {
        for (int i = 0; i < 6; i++) {
            assertThat(board.opposingPit(board.getPit(player, i)).getIndex(), is(5 - i));
            assertThat(board.opposingPit(board.getPit(player, i)).getOwner(), is(player.getOpponent()));
        }
    }

    private void assertBoardSetup(Player player) {
        assertThat(board.getScoringPit(player).getIndex(), is(6));
        assertThat(board.getScoringPit(player).getAmount(), is(0));
        for (int i = 0; i < 6; i++) {
            assertThat(board.getPit(player, i).getAmount(), is(6));
        }
    }
}