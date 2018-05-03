package com.bol.kalah.game;

import com.bol.kalah.Config;
import com.bol.kalah.Player;
import com.bol.kalah.events.PlayPitEvent;
import com.bol.kalah.events.StartGameEvent;
import com.google.common.eventbus.EventBus;
import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.google.common.collect.Lists.reverse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = {Config.class})
@SuppressWarnings("unused")
public class StepDefinitions implements En {

    private static final Player PLAYER_SHOWN_UPSIDEDOWN = Player.B;

    @Autowired
    private Game game;
    @Autowired
    private Board board;
    @Autowired
    private EventBus eventBus;

    public StepDefinitions() {
        Given("^the following board:$", (DataTable board) -> {
                    setScores(board, Player.A);
                    setScores(board, Player.B);
                    System.out.print("");
                }
        );

        Given("^it is the turn of player (.+)$", (String p) ->
                game.setPlayerTurn(Player.valueOf(p))
        );

        When("^player (.+) plays from pit (\\d+)$", (String player, Integer pit) ->
                eventBus.post(new PlayPitEvent(board.getPit(Player.valueOf(player), pit - 1).getId()))
//                eventBus.post(new PlayPitEvent(Player.valueOf(player), pit - 1))
        );

        Then("^the board is:$", (DataTable board) -> {
                    assertAmounts(board, Player.A);
                    assertAmounts(board, Player.B);
                }
        );

        Then("^it is player (.+)'s turn$", (String p) ->
                assertThat(
                        "It is not player's " + p + " turn",
                        game.getPlayerturn(), is(Player.valueOf(p))
                )
        );
    }

    @Before
    public void beforeScenario() {
        eventBus.post(new StartGameEvent());
    }

    private void assertAmounts(DataTable board, Player p) {
        List<Integer> amounts = getSideDataFor(board, p);
        for (int i = 0; i < amounts.size(); i++) {
            assertThat(this.board.getPit(p, i).getAmount(), is(amounts.get(i)));
        }
    }

    private void setScores(DataTable board, Player p) {
        List<Integer> amounts = getSideDataFor(board, p);
        for (int i = 0; i < amounts.size(); i++) {
            this.board.getPit(p, i).setAmount(amounts.get(i));
        }
    }

    private List<Integer> getSideDataFor(DataTable board, Player player) {
        List<Integer> sideData = board.asLists(Integer.class).get(player.getNumber());
        List<Integer> retVal = player == PLAYER_SHOWN_UPSIDEDOWN ? reverse(sideData) : sideData;
        return retVal.subList(1, retVal.size());
    }
}
