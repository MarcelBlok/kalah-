package com.bol.kalah.game;

import com.bol.kalah.GameState;
import com.bol.kalah.Player;
import com.bol.kalah.events.*;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static com.bol.kalah.GameState.ENDED;
import static com.bol.kalah.GameState.IN_PROGRESS;
import static com.bol.kalah.PostedEvents.assertEventsPosted;
import static com.bol.kalah.game.PitGenerator.generatePit;
import static com.bol.kalah.game.PitGenerator.generateScoringPit;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class EngineTest {

    @Captor
    private ArgumentCaptor eventCaptor;
    @Mock
    private EventBus eventBus;
    @Mock
    private Board board;
    @Mock
    private Game game;
    @InjectMocks
    private Engine engine;

    @Test
    public void playPit_fetchesStonesFromPitAndStartsPuttingIntoNextPit() {
        UUID id = randomUUID();
        int stones = Integer.MAX_VALUE;
        Pit testPit = spy(generatePit(id, stones, Player.A));
        Pit testPit2 = spy(generatePit());

        givenBoardWithPits(testPit, testPit2);
        givenGame(IN_PROGRESS, Player.A);

        engine.playPit(new PlayPitEvent(id));

        assertThat(testPit.getAmount(), is(0));
        assertThatPutStonesEventIsTriggeredFor(Player.A, testPit2, stones);
    }

    @Test
    public void playPitWhenNotYourTurn_triggersWrongPlayer() {
        UUID id = randomUUID();

        givenBoardWithPit(spy(generatePit(id, Player.B)));
        givenGame(IN_PROGRESS, Player.A);

        engine.playPit(new PlayPitEvent(id));

        assertEventsPosted(eventBus, eventCaptor, WrongPlayerEvent.class);
    }

    @Test
    public void playEmptyPit_triggersCannotPlayEmptyPit() {
        UUID id = randomUUID();

        givenBoardWithPit(spy(generatePit(id, 0, Player.A)));
        givenGame(IN_PROGRESS, Player.A);

        engine.playPit(new PlayPitEvent(id));

        assertEventsPosted(eventBus, eventCaptor, CannotPlayEmptyPit.class);
    }

    @Test
    public void playPitWhenGameIsOver_triggersNoGameInProgress() {
        UUID id = randomUUID();

        givenBoardWithPit(spy(generatePit(id)));
        givenGame(ENDED, Player.A);

        engine.playPit(new PlayPitEvent(id));

        assertEventsPosted(eventBus, eventCaptor, NoGameInProgress.class);
    }

    @Test
    public void putMultipleStones_addsOneStoneAndContinuesPuttingIntoNextPit() {
        Pit testPit = spy(generatePit(randomUUID(), 1, Player.B));
        Pit testPit2 = spy(generatePit(randomUUID(), 1, Player.B));

        givenBoardWithPits(testPit, testPit2);

        engine.putStones(new PutStonesEvent(5, testPit, Player.B));

        assertThat(testPit.getAmount(), is(2));
        assertThatPutStonesEventIsTriggeredFor(Player.B, testPit2, 4);
    }

    @Test
    public void putMultipleStonesWhenNextPitIsOpponentsScoringPit_skipsOpponentsScoringPit() {
        ScoringPit opponentScoringPit = spy(generateScoringPit(Player.B));
        Pit testPit = spy(generatePit(1));
        Pit testPit2 = spy(generatePit(1));

        givenBoardWithPits(opponentScoringPit, testPit, testPit2);

        engine.putStones(new PutStonesEvent(2, opponentScoringPit, Player.A));

        assertThat(testPit.getAmount(), is(2));
        assertThatPutStonesEventIsTriggeredFor(Player.A, testPit2, 1);
    }

    @Test
    public void putLastStoneIntoNonEmptyPit_addsOneStoneAndTriggersEndOfMove() {
        Pit testPit = spy(generatePit(1));

        engine.putStones(new PutStonesEvent(1, testPit, Player.A));

        assertThat(testPit.getAmount(), is(2));
        assertEventsPosted(eventBus, eventCaptor, LastStoneEvent.class);
        assertThat(((LastStoneEvent) eventCaptor.getValue()).getPit(), is(testPit));
    }

    @Test
    public void putLastStoneIntoOwnEmptyPit_addsOneStoneAndTriggersCaptureAndEndOfMove() {
        Pit emptyPit = spy(generatePit(0, Player.A));

        givenGame(IN_PROGRESS, Player.A);

        engine.putStones(new PutStonesEvent(1, emptyPit, Player.A));

        assertThat(emptyPit.getAmount(), is(1));
        assertEventsPosted(eventBus, eventCaptor, CaptureEvent.class, LastStoneEvent.class);
        assertThat(((CaptureEvent) eventCaptor.getAllValues().get(0)).getPit(), is(emptyPit));
        assertThat(((CaptureEvent) eventCaptor.getAllValues().get(0)).getPlayerTurn(), is(Player.A));
        assertThat(((LastStoneEvent) eventCaptor.getAllValues().get(1)).getPit(), is(emptyPit));
    }

    @Test
    public void putLastStoneIntoOpponentsEmptyPit_addsOneStoneAndEndOfMove() {
        Pit emptyOpponentPit = spy(generatePit(0, Player.B));

        givenGame(IN_PROGRESS, Player.A);

        engine.putStones(new PutStonesEvent(1, emptyOpponentPit, Player.A));

        assertThat(emptyOpponentPit.getAmount(), is(1));
        assertEventsPosted(eventBus, eventCaptor, LastStoneEvent.class);
        assertThat(((LastStoneEvent) eventCaptor.getAllValues().get(0)).getPit(), is(emptyOpponentPit));
    }

    @Test
    public void putLastStoneIntoOpponentsScoringPit_skipsOpponentsScoringPit() {
        ScoringPit opponentScoringPit = spy(generateScoringPit(Player.B));
        Pit testPit = spy(generatePit(randomUUID(), 1, Player.A));

        givenBoardWithPits(opponentScoringPit, testPit);

        engine.putStones(new PutStonesEvent(1, opponentScoringPit, Player.A));

        assertThat(testPit.getAmount(), is(2));
        assertEventsPosted(eventBus, eventCaptor, LastStoneEvent.class);
        assertThat(((LastStoneEvent) eventCaptor.getValue()).getPit(), is(testPit));
    }

    @Test
    public void capture_getsOwnPitAndOpposingPit() {
        Pit testPit = spy(generatePit(2, Player.A));
        Pit opposingPit = spy(generatePit(5, Player.B));
        ScoringPit scoringPit = spy(generateScoringPit(0, Player.A));

        givenBoardWithOpposingPits(testPit, opposingPit);
        givenBoardWithScoringPit(scoringPit);

        engine.capture(new CaptureEvent(testPit, Player.A));

        assertThat(testPit.getAmount(), is(0));
        assertThat(opposingPit.getAmount(), is(0));
        assertThat(scoringPit.getAmount(), is(7));
    }

    @SuppressWarnings("SameParameterValue")
    private void givenGame(GameState state, Player turn) {
        given(game.getPlayerturn()).willReturn(turn);
        given(game.getState()).willReturn(state);
    }

    private void givenBoardWithScoringPit(ScoringPit scoringPit) {
        given(board.getScoringPit(scoringPit.getOwner())).willReturn(scoringPit);
    }

    private void givenBoardWithOpposingPits(Pit pit, Pit opposingPit) {
        given(board.opposingPit(pit)).willReturn(opposingPit);
    }

    private void givenBoardWithPits(Pit pit, Pit nextPit, Pit nextNextPit) {
        given(board.getNextPit(pit)).willReturn(nextPit);
        given(board.getNextPit(nextPit)).willReturn(nextNextPit);
    }

    private void givenBoardWithPits(Pit pit, Pit nextPit) {
        given(board.getPitById(pit.getId())).willReturn(pit);
        given(board.getNextPit(pit)).willReturn(nextPit);
    }

    private void givenBoardWithPit(Pit pit) {
        given(board.getPitById(pit.getId())).willReturn(pit);
    }

    private void assertThatPutStonesEventIsTriggeredFor(Player player, Pit pit, int noStones) {
        assertEventsPosted(eventBus, eventCaptor, PutStonesEvent.class);
        assertThat(((PutStonesEvent) eventCaptor.getValue()).getPlayerTurn(), is(player));
        assertThat(((PutStonesEvent) eventCaptor.getValue()).getPit(), is(pit));
        assertThat(((PutStonesEvent) eventCaptor.getValue()).getNumberOfStones(), is(noStones));
    }
}

class PitGenerator {

    static Pit generatePit() {
        return generatePit(randomUUID());
    }

    static Pit generatePit(UUID id) {
        return generatePit(id, Player.B);
    }

    static Pit generatePit(int amount) {
        return generatePit(randomUUID(), amount, Player.B);
    }

    static Pit generatePit(int amount, Player player) {
        return generatePit(randomUUID(), amount, player);
    }

    static Pit generatePit(UUID id, Player owner) {
        return generatePit(id, Integer.MAX_VALUE, owner);
    }

    static Pit generatePit(UUID id, int amount, Player owner) {
        return new Pit(id, amount, owner, 0);
    }

    static ScoringPit generateScoringPit(Player player) {
        return generateScoringPit(randomUUID(), Integer.MIN_VALUE, player);
    }

    static ScoringPit generateScoringPit(int amount, Player player) {
        return generateScoringPit(randomUUID(), amount, player);
    }

    static ScoringPit generateScoringPit(UUID id, int amount, Player player) {
        return new ScoringPit(id, amount, player, 0);
    }
}
