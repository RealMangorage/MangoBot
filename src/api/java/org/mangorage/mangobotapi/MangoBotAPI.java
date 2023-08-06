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
import org.mangorage.mangobotapi.core.eventbus.EventPriority;
import org.mangorage.mangobotapi.core.events.LoadEvent;
import org.mangorage.mangobotapi.core.events.RegistryEvent;
import org.mangorage.mangobotapi.core.events.SaveEvent;
import org.mangorage.mangobotapi.core.events.ShutdownEvent;
import org.mangorage.mangobotapi.core.events.StartupEvent;
import org.mangorage.mangobotapi.core.registry.CommandRegistry;
import org.mangorage.mangobotapi.core.util.MessageSettings;


public class MangoBotAPI {
    private static final MangoBotAPI INSTANCE = MangoBotAPIBuilder.create();

    public static MangoBotAPI getInstance() {
        return INSTANCE;
    }

    private final EventBus EVENT_BUS;
    private final String COMMAND_PREFIX;
    private final MessageSettings DEFAULT_MESSAGE_SETTINGS;

    protected MangoBotAPI(MangoBotAPIBuilder builder) {
        this.EVENT_BUS = builder.getEventBus();
        this.COMMAND_PREFIX = builder.getCommandPrefix();
        this.DEFAULT_MESSAGE_SETTINGS = builder.getDefaultMessageSettings();
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

    public Runnable startup() {
        EVENT_BUS.startup();

        StartupEvent.addListener(EVENT_BUS, EventPriority.HIGHEST, (event) -> {
            switch (event.phase()) {
                case STARTUP -> {

                }
                case REGISTRATION -> {
                    EVENT_BUS.post(new RegistryEvent(CommandRegistry.CommandType.COMMAND));
                    EVENT_BUS.post(new RegistryEvent(CommandRegistry.CommandType.ALIAS));
                }
                case FINISHED -> {
                    EVENT_BUS.post(new LoadEvent());
                }
            }
        });

        ShutdownEvent.addListener(EVENT_BUS, EventPriority.HIGHEST, (event -> {
            switch (event.phase()) {
                case PRE -> {
                }
                case POST -> {
                    EVENT_BUS.post(new SaveEvent());
                }
            }
        }));

        return () -> {
            for (StartupEvent.Phase phase : StartupEvent.Phase.values())
                EVENT_BUS.post(new StartupEvent(phase));
        };
    }

    public void shutdown() {
        for (ShutdownEvent.Phase phase : ShutdownEvent.Phase.values())
            EVENT_BUS.post(new ShutdownEvent(phase));
        EVENT_BUS.shutdown();
    }
}
