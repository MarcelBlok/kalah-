package com.bol.kalah.web;

import com.bol.kalah.events.StartGameEvent;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GameController {

    private final EventBus eventBus;

    @Autowired
    public GameController(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @GetMapping("/newgame")
    public ModelAndView newGame() {
        return new ModelAndView("game");
    }

    @PostMapping("/newgame")
    public ModelAndView startNewGame() {
        eventBus.post(new StartGameEvent());

        return new ModelAndView("redirect:/");
    }
}
