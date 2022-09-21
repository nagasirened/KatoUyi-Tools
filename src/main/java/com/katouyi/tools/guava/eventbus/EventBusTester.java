package com.katouyi.tools.guava.eventbus;

import com.google.common.eventbus.EventBus;
import org.assertj.core.util.Maps;

public class EventBusTester {

    public static void main(String[] args) {
        EventBus eventBus = EventBusHelper.create();
        EventListener listener = new EventListener();
        eventBus.register( listener );

        eventBus.post( Maps.newHashMap("test", "value") );
    }

}
