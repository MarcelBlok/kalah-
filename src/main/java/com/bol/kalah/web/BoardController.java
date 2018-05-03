package com.bol.kalah.web;

import com.bol.kalah.GameState;
import com.bol.kalah.events.*;
import com.bol.kalah.game.Board;
import com.bol.kalah.game.Game;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class BoardController {

    private final Board board;
    private final Game game;
    private final EventBus eventBus;

    @Autowired
    public BoardController(Board board, Game game, EventBus eventBus) {
        this.board = board;
        this.game = game;
        this.eventBus = eventBus;
    }

    @GetMapping("/")
    public ModelAndView board() {
        if (game.getState().equals(GameState.NOT_STARTED)) {
            return new ModelAndView("forward:/newgame");
        }

        return new GameModelAndView()
                .addObject("pits", board.getPits())
                .addObject("turn", game.getPlayerturn());
    }

    @PostMapping("/play/{id}")
    @ResponseBody
    public List<GameEvent> play(@PathVariable(value = "id") final String id) {
        var eventCaptor = new EventCaptor();
        eventBus.register(eventCaptor);
        try {
            eventBus.post(new PlayPitEvent(UUID.fromString(id)));
        } catch (IllegalArgumentException e) {
            throw new InvalidId();
        } finally {
            eventBus.unregister(eventCaptor);
        }

        return eventCaptor.getEvents();
    }

    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    private static class InvalidId extends RuntimeException {
    }

    class GameModelAndView extends ModelAndView {
        GameModelAndView() {
            super("board");
        }
    }

    class EventCaptor {
        private List<GameEvent> events = new ArrayList<>();

        @Subscribe
        public void capture(PlayPitEvent e) {
            events.add(e);
        }

        @Subscribe
        public void capture(PutStonesEvent e) {
            events.add(e);
        }

        @Subscribe
        public void capture(WrongPlayerEvent e) {
            events.add(e);
        }

        @Subscribe
        public void capture(EndOfGameEvent e) {
            events.add(e);
        }

        List<GameEvent> getEvents() {
            return events;
        }
    }
}