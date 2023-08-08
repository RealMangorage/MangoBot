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

package org.mangorage.mangobotapi.core.events;

import org.mangorage.mangobotapi.core.eventbus.EventHolder;
import org.mangorage.mangobotapi.core.eventbus.EventPriority;
import org.mangorage.mangobotapi.core.eventbus.IFunctionalEventListener;

public class TestEvents {

    public static class Test {
        public String get() {
            return "WOO!";
        }
    }

    public static final EventHolder<TestEvent> EVENTSA = EventHolder.create(TestEvent.class, callbacks -> (e) -> {
        loop:
        for (IFunctionalEventListener<TestEvent> callback : callbacks) {
            if (e.isCancelled())
                break loop;
            else
                callback.invoke(e);
        }
    });

    public static void main(String[] args) {


        EVENTSA.addListener(EventPriority.NORMAL, (e) -> {
            System.out.println("Sample!");
        });


        EVENTSA.addListener(EventPriority.HIGHEST, (e) -> {
            System.out.println("Sample! HIGHEST");
            e.setCancelled(true);
        });


        EVENTSA.addListener(EventPriority.LOWEST, (e) -> {
            System.out.println("Sample! LOWEST");
        });


        EVENTSA.addListener(EventPriority.LOW, (e) -> {
            System.out.println("Sample! LOW");
        });

        EVENTSA.post(new TestEvent());
    }
}
