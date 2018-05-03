package com.bol.kalah;

import com.google.common.eventbus.EventBus;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PostedEvents {
    static public void assertEventsPosted(EventBus eventBus, ArgumentCaptor eventCaptor, Class... classes) {
        verify(eventBus, times(classes.length)).post(eventCaptor.capture());

        List<Class> eventTypes = new ArrayList<>();
        for (Object o : eventCaptor.getAllValues()) {
            eventTypes.add(o.getClass());
        }
        assertThat(eventTypes, contains(classes));
    }
}
