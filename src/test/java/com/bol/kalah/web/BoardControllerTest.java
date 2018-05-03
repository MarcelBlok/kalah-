package com.bol.kalah.web;

import com.bol.kalah.GameState;
import com.bol.kalah.Player;
import com.bol.kalah.events.PlayPitEvent;
import com.bol.kalah.game.Board;
import com.bol.kalah.game.Game;
import com.bol.kalah.game.Pit;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.bol.kalah.PostedEvents.assertEventsPosted;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Game game;

    @MockBean
    private Board board;

    @MockBean
    private EventBus eventBus;

    @Captor
    private ArgumentCaptor eventCaptor;

    @Test
    public void unstartedGame_forwardsToNewGameView() throws Exception {
        given(game.getState()).willReturn(GameState.NOT_STARTED);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/newgame"));
    }

    @Test
    public void startedGame_showsBoard() throws Exception {
        List<Pit> pits = List.of(new Pit(UUID.randomUUID(), 1, Player.B, 1));
        given(game.getState()).willReturn(GameState.IN_PROGRESS);
        given(game.getPlayerturn()).willReturn(Player.A);
        given(board.getPits()).willReturn(pits);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("board"))
                .andExpect(model().attribute("turn", Player.A))
                .andExpect(model().attribute("pits", pits));
    }

    @Test
    public void postPlayOfPit_playsPitAndReturnsEvents() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(post("/play/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        assertEventsPosted(eventBus, eventCaptor, PlayPitEvent.class);
    }

    @Test
    public void postPlayOfPitWithInvalidUUID_fails() throws Exception {
        mockMvc.perform(post("/play/noid"))
                .andExpect(status().is4xxClientError());
    }
}