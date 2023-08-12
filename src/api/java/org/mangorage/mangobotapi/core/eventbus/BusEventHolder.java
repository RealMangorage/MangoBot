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

import org.mangorage.mangobotapi.core.eventbus.base.Event;
import org.mangorage.mangobotapi.core.eventbus.impl.IBusEventInvoker;
import org.mangorage.mangobotapi.core.eventbus.impl.IEvent;
import org.mangorage.mangobotapi.core.eventbus.impl.IlEventListener;

import java.util.function.Function;

public class BusEventHolder<T extends Event & IEvent<T>> extends EventHolder<T> {

    public static <X extends Event & IEvent<X>> BusEventHolder<X> createHolder(Class<X> type, Function<IlEventListener<X>[], IBusEventInvoker<X>> invokerFunction) {
        return new BusEventHolder<>(type, invokerFunction);
    }

    private final Function<IlEventListener<T>[], IBusEventInvoker<T>> invokerFactory;
    private IBusEventInvoker<T> invoker;

    protected BusEventHolder(Class<T> type, Function<IlEventListener<T>[], IBusEventInvoker<T>> invokerFactory) {
        super(type, null);
        this.invokerFactory = invokerFactory;
    }


    @Override
    public void post(T event) {
        invoker.invoke(event);
    }

    @Override
    protected void update() {
        this.invoker = invokerFactory.apply(getHandlers());
    }
}
