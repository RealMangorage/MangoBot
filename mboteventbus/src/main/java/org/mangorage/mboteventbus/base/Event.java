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

/**
 * When overriding seenPhase()
 * Make sure to call IPhase.super.seenPhase() first always!
 */

public abstract class Event<EventType> implements ICancellable, IResult, IPhase, IEvent<EventType> {
    private boolean cancelled = false;
    private Result result = Result.DEFAULT;
    private int phase = 0;


    public boolean isCancellable() {
        return false;
    }

    public boolean hasResult() {
        return false;
    }

    public boolean hasPhase() {
        return false;
    }


    /**
     * @param cancel
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * @return
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @return
     */
    @Override
    public int getPhase() {
        return phase;
    }

    /**
     * @param priority
     * @return
     */
    @Override
    public boolean seenPhase(int priority) {
        return phase == priority;
    }

    /**
     * @param result
     */
    @Override
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @return
     */
    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public void indirectInvoke(EventType event) {
        // Do nothing!
    }
}
