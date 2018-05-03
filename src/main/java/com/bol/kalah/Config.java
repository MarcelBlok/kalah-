package com.bol.kalah;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.bol.kalah")
public class Config {

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }
}
