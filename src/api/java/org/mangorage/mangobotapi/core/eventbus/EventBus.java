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

import org.mangorage.mangobotapi.core.eventbus.annotations.SubscribeEvent;
import org.mangorage.mangobotapi.core.eventbus.base.Event;
import org.mangorage.mangobotapi.core.eventbus.impl.IEvent;
import org.mangorage.mangobotapi.core.eventbus.impl.IEventBus;
import org.mangorage.mangobotapi.core.eventbus.impl.IEventListener;
import org.mangorage.mangobotapi.core.eventbus.scanner.EventScanner;
import org.mangorage.mangobotapi.core.events.tests.SampleEvent;

import java.util.Arrays;
import java.util.HashMap;
/*
    TODO: Remove EventPriority and use Integer's to determine event priority
    TODO: Change RequiredClass to Marker
    TODO: Add a way to post to all listeners even after event has been cancelled,
     only to those Listeners that can recieve a Cancelled event
 */

public class EventBus implements IEventBus {

    public static void main(String[] args) {
        IEventBus bus = create();
        bus.startup();
        bus.addListener(SampleEvent.class, e -> {
            System.out.println("WOO!");
        });
        bus.register(bus);
        bus.post(new SampleEvent());
    }


    @SubscribeEvent(priority = EventPriority.NORMAL, recieveCancelled = true)
    public void onEvent(SampleEvent event) {
        System.out.println("WOOOP!!!!");
    }

    public static EventBus create() {
        return new EventBus(EventPriority.NORMAL, null);
    }

    public static EventBus create(Class<?> requiredClass) {
        return new EventBus(EventPriority.NORMAL, requiredClass);
    }


    private final HashMap<Class<?>, EventHolder<?>> EVENT_LISTENERS = new HashMap<>();
    private final EventPriority DEFAULT_PRIORITY;
    private final Class<?> REQUIRED_CLASS;
    private boolean shutdown = true;

    protected EventBus(EventPriority priority, Class<?> requiredClass) {
        this.DEFAULT_PRIORITY = priority;
        this.REQUIRED_CLASS = requiredClass;
    }

    @Override
    public <T extends Event & IEvent<T>> void addListener(Class<T> type, IEvent<T> event) {
        addListener(DEFAULT_PRIORITY, type, event);
    }

    @Override
    public <T extends Event & IEvent<T>> void addListener(EventPriority priority, Class<T> type, IEvent<T> event) {
        addListener(priority, type, false, event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event & IEvent<T>> void addListener(EventPriority priority, Class<T> type, boolean recieveCancelled, IEvent<T> event) {
        if (shutdown)
            throw new IllegalStateException("Unable to addListener when bus is shutdown");

        if (REQUIRED_CLASS != null)
            if (!Arrays.stream(type.getClass().getInterfaces()).toList().contains(REQUIRED_CLASS))
                throw new IllegalStateException(
                        "Tried to add a listener whose eventtype isnt compatible with this EventBus Must implement: %s"
                                .formatted(REQUIRED_CLASS.getCanonicalName()));

        ((EventHolder<T>) EVENT_LISTENERS.computeIfAbsent(type, id -> EventHolder.create(type, callbacks -> e -> {
            for (IEventListener<T> callback : callbacks) {
                EventListener<T> listener = (EventListener<T>) callback;
                if (!listener.canRecieveCancelled() && (e.isCancellable() && e.isCancelled()))
                    continue;

                callback.invoke(e);
            }
        }))).addListener(priority, event);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T extends Event & IEvent<T>> void addListener(EventBuilder<T> builder) {
        addListener(
                builder.getPriority() == null ? DEFAULT_PRIORITY : builder.getPriority(),
                (Class) builder.getClassType(),
                builder.canRecieveCanclled(),
                (IEvent) builder.getEvent()
        );
    }


    @SuppressWarnings("unchecked")
    @Override
    public void register(Object object) {
        EventScanner.scan(object, this).forEach(this::addListener);
    }


    @SuppressWarnings("unchecked")
    public <T extends Event & IEvent<T>> boolean post(T event) {
        if (shutdown)
            throw new IllegalStateException("Unable to post event when bus is shutdown");

        if (REQUIRED_CLASS != null)
            if (!Arrays.stream(event.getClass().getInterfaces()).toList().contains(REQUIRED_CLASS))
                throw new IllegalStateException(
                        "Tried to post an event that isnt compatible with this EventBus Must implement: %s"
                                .formatted(REQUIRED_CLASS.getCanonicalName()));

        if (!EVENT_LISTENERS.containsKey(event.getClass()))
            System.out.println("Attemped to post %s to the event bus however there are no listeners, skipping!".formatted(event.getClass()));
        else
            ((EventHolder<T>) EVENT_LISTENERS.get(event.getClass())).post(event);

        return event.isCancellable() && event.isCancelled();
    }

    public void startup() {
        if (!shutdown)
            throw new IllegalStateException("Already started the EventBus");
        System.out.println("EventBus will now recieve posts/addListeners");
        this.shutdown = false;
    }

    public void shutdown() {
        if (shutdown)
            throw new IllegalStateException("Already shutdown the EventBus");
        System.out.println("EventBus will now no longer recieve posts/addListeners");
        this.shutdown = true;
    }
}
