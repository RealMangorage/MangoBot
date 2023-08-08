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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class EventHolder<T extends IFunctionalEvent<T>> {

    public static <X extends IFunctionalEvent<X>> EventHolder<X> create(Class<X> type, Function<IFunctionalEventListener<X>[], IFunctionalEventInvoker<X>> invokerFunction) {
        return new EventHolder<>(type, invokerFunction);
    }


    protected volatile IFunctionalEventInvoker<T> invoker;
    private final Object lock = new Object();
    public final Class<T> classType;
    private final Function<IFunctionalEventListener<T>[], IFunctionalEventInvoker<T>> invokerFactory;
    private IFunctionalEventListener<T>[] handlers;
    private EventPriority lastPriority = null;

    @SuppressWarnings("unchecked")
    protected EventHolder(Class<T> type, Function<IFunctionalEventListener<T>[], IFunctionalEventInvoker<T>> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.classType = type;

        this.handlers = (IFunctionalEventListener<T>[]) Array.newInstance(IFunctionalEventListener.class, 0);
        update();
    }

    public void post(T event) {
        invoker.invoke(event);
    }

    public void addListener(IFunctionalEvent<T> listener) {
        addListener(EventPriority.NORMAL, listener);
    }

    public void addListener(EventPriority priority, IFunctionalEvent<T> listener) {
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
    private IFunctionalEventListener<T>[] resort() {
        return Arrays.stream(handlers)
                .sorted(Comparator.comparing(w -> w.getPriority().ordinal()))
                .toArray(length -> (IFunctionalEventListener<T>[]) Array.newInstance(IFunctionalEventListener.class, length));
    }

    private void update() {
        this.invoker = invokerFactory.apply(handlers);
    }
}
