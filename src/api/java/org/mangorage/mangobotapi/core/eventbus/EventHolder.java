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

import org.mangorage.mangobotapi.core.eventbus.impl.IEvent;
import org.mangorage.mangobotapi.core.eventbus.impl.IEventInvoker;
import org.mangorage.mangobotapi.core.eventbus.impl.IlEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class EventHolder<T extends IEvent<T>> {


    public static <X extends IEvent<X>> EventHolder<X> create(Class<X> type, Function<IlEventListener<X>[], IEventInvoker<X>> invokerFunction) {
        return new EventHolder<>(type, invokerFunction);
    }


    private final Function<IlEventListener<T>[], IEventInvoker<T>> invokerFactory;
    protected volatile IEventInvoker<T> invoker;
    private final Object lock = new Object();
    public final Class<T> classType;
    private IlEventListener<T>[] handlers;
    private EventPriority lastPriority = null;

    @SuppressWarnings("unchecked")
    private EventHolder(Class<T> type, Function<IlEventListener<T>[], IEventInvoker<T>> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.classType = type;

        this.handlers = (IlEventListener<T>[]) Array.newInstance(IlEventListener.class, 0);
        update();
    }

    @SuppressWarnings("unchecked")
    public EventHolder(Class<T> type) {
        this.invokerFactory = null;
        this.classType = type;

        this.handlers = (IlEventListener<T>[]) Array.newInstance(IlEventListener.class, 0);
    }

    public void post(T event) {
        invoker.invoke(event);
    }

    protected IlEventListener<T>[] getHandlers() {
        return handlers;
    }

    public void addListener(IEvent<T> listener) {
        addListener(EventPriority.NORMAL, listener);
    }

    public void addListener(EventPriority priority, IEvent<T> listener) {
        Objects.requireNonNull(listener, "Tried to register null Listener");
        Objects.requireNonNull(priority, "Tried to register with null priority");

        synchronized (lock) {
            handlers = Arrays.copyOf(handlers, handlers.length + 1);
            handlers[handlers.length - 1] = new EventListener<>(priority, listener);
            if (lastPriority != priority)
                handlers = resort();
            lastPriority = priority;
            update();
        }
    }

    public Class<T> getClassType() {
        return classType;
    }

    @SuppressWarnings("unchecked")
    private IlEventListener<T>[] resort() {
        return Arrays.stream(handlers)
                .sorted(Comparator.comparing(w -> w.getPriority().ordinal()))
                .toArray(length -> (IlEventListener<T>[]) Array.newInstance(IlEventListener.class, length));
    }

    protected void update() {
        this.invoker = invokerFactory.apply(handlers);
    }
}
