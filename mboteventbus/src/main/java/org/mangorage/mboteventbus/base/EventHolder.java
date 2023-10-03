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

package org.mangorage.mboteventbus.base;

import org.mangorage.mboteventbus.impl.IEvent;
import org.mangorage.mboteventbus.impl.IEventInvoker;
import org.mangorage.mboteventbus.impl.IEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class EventHolder<T extends IEvent<T>> {
    public static <X extends IEvent<X>> EventHolder<X> create(Class<X> type, Function<IEventListener<X>[], IEventInvoker<X>> invokerFunction) {
        return new EventHolder<>(type, invokerFunction);
    }


    private final Function<IEventListener<T>[], IEventInvoker<T>> invokerFactory;
    protected volatile IEventInvoker<T> invoker;
    private final Object lock = new Object();
    public final Class<T> classType;
    private IEventListener<T>[] handlers;
    private int lastPriority = 0;

    @SuppressWarnings("unchecked")
    private EventHolder(Class<T> type, Function<IEventListener<T>[], IEventInvoker<T>> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.classType = type;

        this.handlers = (IEventListener<T>[]) Array.newInstance(IEventListener.class, 0);
        update();
    }

    public void post(T event) {
        invoker.invoke(event);
    }

    protected IEventListener<T>[] getHandlers() {
        return handlers;
    }

    public void addListener(IEvent<T> listener) {
        addListener(0, listener);
    }

    public void addListener(int priority, IEvent<T> listener) {
        addListener(priority, false, listener);
    }

    public void addListener(int priority, boolean recieveCancelled, IEvent<T> listener) {
        Objects.requireNonNull(listener, "Tried to register null Listener");

        synchronized (lock) {
            handlers = Arrays.copyOf(handlers, handlers.length + 1);
            handlers[handlers.length - 1] = new EventListener<>(priority, recieveCancelled, listener);
            if (lastPriority != priority)
                Arrays.sort(handlers, Comparator.<IEventListener<T>>comparingInt(IEventListener::getPriority).reversed());
            lastPriority = priority;
            update();
        }
    }

    public Class<T> getClassType() {
        return classType;
    }

    protected void update() {
        this.invoker = invokerFactory.apply(handlers);
    }
}
