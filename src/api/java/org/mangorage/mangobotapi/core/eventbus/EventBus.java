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

public class EventBus {

    public static EventBus create() {
        return new EventBus();
    }

    public final HashMap<Class<?>, EventHolder<? extends IFunctionalEvent<?>>> TEST = new HashMap<>();


    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> void addListener(Class<T> type, IFunctionalEvent<T> event) {
        ((EventHolder<T>) TEST.computeIfAbsent(type, id -> EventHolder.create(type, callbacks -> e -> {
            ICancellable cancellable = null;
            if (e instanceof ICancellable cancel)
                cancellable = cancel;

            loop:
            for (IFunctionalEventListener<T> callback : callbacks) {
                if (cancellable != null && cancellable.isCancelled())
                    break loop;
                else
                    callback.invoke(e);
            }
        }))).addListener(event);
    }

    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> void addListener(EventPriority priority, Class<T> type, IFunctionalEvent<T> event) {
        ((EventHolder<T>) TEST.computeIfAbsent(type, id -> EventHolder.create(type, callbacks -> e -> {
            ICancellable cancellable = null;
            if (e instanceof ICancellable cancel)
                cancellable = cancel;

            loop:
            for (IFunctionalEventListener<T> callback : callbacks) {
                if (cancellable != null && cancellable.isCancelled())
                    break loop;
                else
                    callback.invoke(e);
            }
        }))).addListener(priority, event);
    }

    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> void addListener(EventBuilder<T> builder) {
        ((EventHolder<T>) TEST.get(builder.getClassType())).addListener(builder.getEvent());
    }

    public <T extends IFunctionalEvent<T>> void registerEvent(EventHolder<T> holder) {
        if (!TEST.containsKey(holder.getClassType()))
            TEST.put(holder.getClassType(), holder);
    }

    @SuppressWarnings("unchecked")
    public <T extends IFunctionalEvent<T>> T post(T event) {
        ((EventHolder<T>) TEST.get(event.getClass())).post(event);
        return event;
    }

    public void startup() {
    }

    public void shutdown() {
    }
}
