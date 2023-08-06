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

package org.mangorage.mangobotapi;

import org.mangorage.mangobotapi.core.eventbus.EventBus;
import org.mangorage.mangobotapi.core.util.Lockable;
import org.mangorage.mangobotapi.core.util.MessageSettings;

import java.util.function.Consumer;

public class MangoBotAPIBuilder {
    private static final MangoBotAPIBuilder BUILDER = new MangoBotAPIBuilder();

    /**
     * Use this to configure the MangoBotAPI instance
     * <p>
     * DO NOT CALL MangoBotAPI before this!!! Otherwise, it will not work.
     * This is designed to prevent any further stuff from occuring AFTER
     * hooking into the API, so that properties cant be configured afterwards
     */
    public static void hook(Consumer<MangoBotAPIBuilder> builderConsumer) {
        if (!BUILDER.isBuilt()) {
            builderConsumer.accept(BUILDER);
            BUILDER.built.lock();
        }
    }

    protected static MangoBotAPI create() {
        return new MangoBotAPI(BUILDER);
    }

    private final Lockable built = new Lockable();
    private EventBus EVENT_BUS;
    private String COMMAND_PREFIX = "!";
    private MessageSettings DEFAULT_MESSAGE_SETTINGS;

    public void setEventBus(EventBus bus) {
        this.EVENT_BUS = bus;
    }

    public void setPrefix(String prefix) {
        this.COMMAND_PREFIX = prefix;
    }

    public void setMessageSettings(MessageSettings settings) {
        this.DEFAULT_MESSAGE_SETTINGS = settings;
    }

    protected EventBus getEventBus() {
        return EVENT_BUS;
    }

    protected String getCommandPrefix() {
        return COMMAND_PREFIX;
    }

    protected MessageSettings getDefaultMessageSettings() {
        return DEFAULT_MESSAGE_SETTINGS;
    }

    protected boolean isBuilt() {
        return built.isLocked();
    }
}
