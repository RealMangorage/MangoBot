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

package org.mangorage.mangobotapi.core.eventbus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class EventBus {
    public static Logger LOGGER = Logger.getLogger(EventBus.class.getName());
    public static EventBus create() {
        return create(EventPriority.HIGHEST);
    }

    public static EventBus create(EventPriority defaultPriority) {
        return new EventBus(defaultPriority);
    }

    private final ConcurrentHashMap<Class<?>, EventListener<?>> listenerHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object, ArrayList<EventConsumer>> registeredObjects = new ConcurrentHashMap<>();
    private final EventPriority DEFAULT_PRIORITY;
    private boolean shutdown = true;

    private EventBus(EventPriority priority) {
        this.DEFAULT_PRIORITY = priority;
    }

    public void startup() {
        this.shutdown = false;
        System.out.println("EventBus will now accept listeners/posts");
    }

    public void shutdown() {
        this.shutdown = true;
        System.out.println("Future actions will be ignored including registering/posting events");
    }

    public void register(Object target) {
        // Do a scan of said Object!
        if (registeredObjects.containsKey(target) || shutdown)
            return;

        try {
            Class<?> targetClass = null;
            if (target instanceof Class<?> classZ)
                targetClass = classZ;

            targetClass.getDeclaredConstructor().trySetAccessible();
            Object realTarget = targetClass == null ? target : targetClass.getDeclaredConstructor().newInstance();


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
            LOGGER.warning("Error while registering Event Handler -> %s".formatted(target.toString()));
        }
    }

    public void unregister(Object target) {
        if (!registeredObjects.containsKey(target) || shutdown)
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
        return (EventListener<X>) listenerHashMap.computeIfAbsent(xClass, (k) -> new EventListener<>(this, DEFAULT_PRIORITY));
    }

    @SuppressWarnings("unchecked")
    public <X> X post(X event) {
        if (shutdown)
            return null;
        System.out.println("Attempting to post event: %s".formatted(event.getClass()));
        return ((EventListener<X>) get(event.getClass())).post(event);
    }

    public static final class EventListener<X> {
        private final EnumMap<EventPriority, CopyOnWriteArrayList<Consumer<X>>> LISTENERS = new EnumMap<>(EventPriority.class);
        private final EventBus BUS;
        private final EventPriority DEFAULT_PRIORITY;

        private EventListener(EventBus bus, EventPriority defaultPriority) {
            this.BUS = bus;
            this.DEFAULT_PRIORITY = defaultPriority;
        }

        public X post(X event) {
            if (BUS.shutdown)
                return null;
            for (EventPriority value : EventPriority.get()) {
                List<Consumer<X>> consumers = LISTENERS.getOrDefault(value, null);
                if (consumers != null) consumers.forEach(e -> e.accept(event));
            }
            return event;
        }

        public Consumer<X> addListener(Consumer<X> eventConsumer) {
            if (BUS.shutdown)
                return null;
            return addListener(DEFAULT_PRIORITY, eventConsumer);
        }

        public Consumer<X> addListener(EventPriority priority, Consumer<X> eventConsumer) {
            if (BUS.shutdown)
                return null;
            LISTENERS.computeIfAbsent(priority, (key) -> new CopyOnWriteArrayList<>()).add(eventConsumer);
            return eventConsumer;
        }

        public void removeListener(EventPriority priority, Consumer<?> consumer) {
            if (BUS.shutdown)
                return;
            if (LISTENERS.containsKey(priority))
                LISTENERS.get(priority).remove(consumer);
        }
    }
}
