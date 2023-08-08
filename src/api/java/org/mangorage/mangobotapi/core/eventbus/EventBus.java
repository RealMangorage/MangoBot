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

import org.mangorage.mangobotapi.core.events.impl.ICancellable;

import java.util.HashMap;

public class EventBus implements IEventBus {

    public static EventBus create() {
        return new EventBus(EventPriority.NORMAL);
    }

    private final HashMap<Class<?>, EventHolder<? extends IFunctionalEvent<?>>> EVENT_LISTENERS = new HashMap<>();
    private final EventPriority DEFAULT_PRIORITY;
    private boolean shutdown = true;

    protected EventBus(EventPriority priority) {
        this.DEFAULT_PRIORITY = priority;
    }

    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> void addListener(Class<T> type, IFunctionalEvent<T> event) {
        if (shutdown)
            throw new IllegalStateException("Unable to addListener when bus is shutdown");
        ((EventHolder<T>) EVENT_LISTENERS.computeIfAbsent(type, id -> EventHolder.create(type, callbacks -> e -> {
            ICancellable cancellable = null;
            if (e instanceof ICancellable cancel)
                cancellable = cancel;

            loop:
            for (IFunctionalEventListener<T> callback : callbacks) {
                callback.invoke(e);
                if (cancellable != null && cancellable.isCancelled())
                    break loop;
            }

        }))).addListener(DEFAULT_PRIORITY, event);
    }

    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> void addListener(EventPriority priority, Class<T> type, IFunctionalEvent<T> event) {
        if (shutdown)
            throw new IllegalStateException("Unable to addListener when bus is shutdown");
        ((EventHolder<T>) EVENT_LISTENERS.computeIfAbsent(type, id -> EventHolder.create(type, callbacks -> e -> {
            ICancellable cancellable = null;
            if (e instanceof ICancellable cancel)
                cancellable = cancel;

            loop:
            for (IFunctionalEventListener<T> callback : callbacks) {
                callback.invoke(e);
                if (cancellable != null && cancellable.isCancelled())
                    break loop;
            }
        }))).addListener(priority, event);
    }

    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> void addListener(EventBuilder<T> builder) {
        if (shutdown)
            throw new IllegalStateException("Unable to addListener when bus is shutdown");
        if (builder.getPriority() != null)
            addListener(EventPriority.NORMAL, builder.getClassType(), builder.getEvent());
        else
            addListener(builder.getPriority(), builder.getClassType(), builder.getEvent());
    }

    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> T post(T event) {
        if (shutdown)
            throw new IllegalStateException("Unable to post event when bus is shutdown");
        ((EventHolder<T>) EVENT_LISTENERS.get(event.getClass())).post(event);
        return event;
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
