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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    public static EventBus create() {
        return new EventBus();
    }

    public static EventBus create(EventPriority defaultPriority) {
        return new EventBus(defaultPriority);
    }

    private final HashMap<Class<?>, EventListener<?>> listenerHashMap = new HashMap<>();
    private final EventPriority DEFAULT_PRIORITY;

    private EventBus() {
        this(EventPriority.HIGHEST);
    }

    private EventBus(EventPriority priority) {
        this.DEFAULT_PRIORITY = priority;
    }

    public void register(Object target) {
        // Do a scan of said Object!
        try {
            Class<?> targetClass = null;
            if (target instanceof Class<?> classZ)
                targetClass = classZ;

            Object realTarget = targetClass == null ? target : targetClass.newInstance();

            Arrays.stream(realTarget.getClass().getMethods()).filter(e -> e.getAnnotation(SubscribeEvent.class) != null).forEach(method -> {
                SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                Class<?> eventClass = method.getParameters()[0].getType();
                System.out.println("""
                            Subscribing Event Listener ->
                                Listener Class: %s
                                Method: %s
                                Event Class: %s
                                Priortiy: %s
                        """.formatted(method.getDeclaringClass().getCanonicalName(), method.getName(), eventClass.getName(), annotation.priority()));

                get(eventClass).addListener((e) -> {
                    try {
                        method.invoke(realTarget, e);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <X> EventListener<X> get(Class<X> xClass) {
        return (EventListener<X>) listenerHashMap.computeIfAbsent(xClass, (k) -> new EventListener<>(DEFAULT_PRIORITY));
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
            for (EventPriority value : EventPriority.get()) {
                List<Consumer<X>> consumers = LISTENERS.getOrDefault(value, null);
                if (consumers != null) consumers.forEach(e -> e.accept(event));
            }
        }

        public void addListener(Consumer<X> eventConsumer) {
            addListener(DEFAULT_PRIORITY, eventConsumer);
        }

        public void addListener(EventPriority priority, Consumer<X> eventConsumer) {
            LISTENERS.computeIfAbsent(priority, (key) -> new ArrayList<>()).add(eventConsumer);
        }
    }
}
