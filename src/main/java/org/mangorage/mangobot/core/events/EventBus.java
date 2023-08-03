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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {
    public static EventBus create() {
        return new EventBus();
    }

    public static EventBus create(EventPriority defaultPriority) {
        return new EventBus(defaultPriority);
    }

    private final ConcurrentHashMap<Class<?>, EventListener<?>> listenerHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object, ArrayList<EventConsumer>> registeredObjects = new ConcurrentHashMap<>();
    private final EventPriority DEFAULT_PRIORITY;

    private EventBus() {
        this(EventPriority.HIGHEST);
    }

    private EventBus(EventPriority priority) {
        this.DEFAULT_PRIORITY = priority;
    }

    public void register(Object target) {
        // Do a scan of said Object!
        if (registeredObjects.containsKey(target))
            return;

        try {
            Class<?> targetClass = null;
            if (target instanceof Class<?> classZ)
                targetClass = classZ;

            Object realTarget = targetClass == null ? target : targetClass.newInstance();

            Arrays.stream(realTarget.getClass().getMethods()).filter(e -> e.getAnnotation(SubscribeEvent.class) != null).forEach(method -> {
                SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                EventPriority priority = annotation.priority() == EventPriority.DEFAULT ? DEFAULT_PRIORITY : annotation.priority();
                Class<?> eventClass = method.getParameters()[0].getType();
                System.out.println("""
                            Subscribing Event Listener ->
                                Listener Class: %s
                                Method: %s
                                Event Class: %s
                                Priortiy: %s
                        """.formatted(method.getDeclaringClass().getCanonicalName(), method.getName(), eventClass.getName(), priority));

                Consumer<?> eventConsumer = get(eventClass).addListener(priority, (e) -> {
                    try {
                        method.invoke(realTarget, e);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });

                registeredObjects.computeIfAbsent(target, (k) -> new ArrayList<>()).add(new EventConsumer(eventClass, priority, eventConsumer));
            });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void unregister(Object target) {
        if (!registeredObjects.containsKey(target))
            return;

        registeredObjects.get(target).forEach(e -> {
            Class<?> eventClass = e.getEventClass();
            EventPriority priority = e.getPriority();
            Consumer<?> consumer = e.getEventConsumer();

            get(eventClass).removeListener(priority, consumer);
        });

        registeredObjects.remove(target); // We handled the unrregistering of it already so now remove it!
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

        public Consumer<X> addListener(Consumer<X> eventConsumer) {
            return addListener(DEFAULT_PRIORITY, eventConsumer);
        }

        public Consumer<X> addListener(EventPriority priority, Consumer<X> eventConsumer) {
            LISTENERS.computeIfAbsent(priority, (key) -> new ArrayList<>()).add(eventConsumer);
            return eventConsumer;
        }

        public void removeListener(EventPriority priority, Consumer<?> consumer) {
            if (LISTENERS.containsKey(priority))
                LISTENERS.get(priority).remove(consumer);
        }
    }
}
