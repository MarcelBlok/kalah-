package com.bol.kalah.web;

import com.bol.kalah.events.StartGameEvent;
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

import static com.bol.kalah.PostedEvents.assertEventsPosted;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventBus eventBus;

    @Captor
    private ArgumentCaptor eventCaptor;

    @Test
    public void newGame_showsNewGameView() throws Exception {
        mockMvc.perform(get("/newgame"))
                .andExpect(status().isOk())
                .andExpect(view().name("game"));
    }

    @Test
    public void newGame_startsNewGame() throws Exception {

        mockMvc.perform(post("/newgame"))
                .andExpect(redirectedUrl("/"));

        assertEventsPosted(eventBus, eventCaptor, StartGameEvent.class);
    }
}

