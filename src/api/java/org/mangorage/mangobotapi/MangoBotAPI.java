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

public class MangoBotAPI {
    private static final MangoBotAPI INSTANCE = new MangoBotAPI();

    public static MangoBotAPI getInstance() {
        return INSTANCE;
    }


    private EventBus EVENT_BUS;
    private String COMMAND_PREFIX = "!";
    private MessageSettings DEFAULT_MESSAGE_SETTINGS;
    private Lockable built = new Lockable();

    public void setEventBus(EventBus bus) {
        this.EVENT_BUS = bus;
    }

    public void setPrefix(String prefix) {
        this.COMMAND_PREFIX = prefix;
    }

    public void setMessageSettings(MessageSettings settings) {
        this.DEFAULT_MESSAGE_SETTINGS = settings;
    }

    public EventBus getEventBus() {
        return EVENT_BUS;
    }

    public String getCommandPrefix() {
        return COMMAND_PREFIX;
    }

    public MessageSettings getDefaultMessageSettings() {
        return DEFAULT_MESSAGE_SETTINGS;
    }

    public void build() {
        this.built.lock();
    }

    public boolean isBuilt() {
        return built.isLocked();
    }
}
