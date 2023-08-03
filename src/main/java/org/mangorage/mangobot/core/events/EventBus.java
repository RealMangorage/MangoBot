/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobot.core.events;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    public static final EventBus GLOBAL = new EventBus();


    private final HashMap<Class<?>, EventListener<?>> listenerHashMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <X> EventListener<X> get(Class<X> xClass) {
        return (EventListener<X>) listenerHashMap.computeIfAbsent(xClass, (k) -> new EventListener<>(DEFAULT_PRIORITY));
    }

    private final EventPriority DEFAULT_PRIORITY;

    private EventBus() {
        this(EventPriority.HIGHEST);
    }

    private EventBus(EventPriority priority) {
        this.DEFAULT_PRIORITY = priority;
    }

    @SuppressWarnings("unchecked")
    public <X> void post(X event) {
        ((EventListener<X>) get(event.getClass())).post(event);
    }

    public static final class EventListener<X> {
        private final EnumMap<EventPriority, List<Consumer<X>>> LISTENERS = new EnumMap<>(EventPriority.class);
        private final EventPriority DEFAULT_PRIORITY;

        private EventListener(EventPriority defaultPriority) {
            this.DEFAULT_PRIORITY = defaultPriority;
        }

        public void post(X event) {
            for (EventPriority value : EventPriority.values()) {
                List<Consumer<X>> consumers = LISTENERS.getOrDefault(value, null);
                if (consumers != null) consumers.forEach(e -> e.accept(event));
            }
        }

        public void register(Consumer<X> eventConsumer) {
            register(DEFAULT_PRIORITY, eventConsumer);
        }

        public void register(EventPriority priority, Consumer<X> eventConsumer) {
            LISTENERS.computeIfAbsent(priority, (key) -> new ArrayList<>()).add(eventConsumer);
        }
    }
}
