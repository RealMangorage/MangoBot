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

package org.mangorage.mangobotapi.core.eventbus.base;


import org.mangorage.mangobotapi.core.eventbus.annotations.SubscribeEvent;
import org.mangorage.mangobotapi.core.eventbus.impl.IEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple utility class for scanning Classes/Objects
 * via Java reflection.
 * <p>
 * Cant be extended/Intiated
 * Can only be used as a utillity class, I.E static methods only
 */

public final class EventScanner {
    private static final Logger LOGGER = Logger.getLogger(EventScanner.class.getName());

    @SuppressWarnings("rawtypes")
    public static List<EventBuilder> scan(Object object) {
        final ArrayList<EventBuilder> BUILDERS = new ArrayList<>();
        boolean requireStatic = object instanceof Class<?>;
        Class<?> scanning = requireStatic ? (Class<?>) object : object.getClass();
        String name = scanning.getName();

        // Handle scanning

        for (Method method : scanning.getMethods()) {
            SubscribeEvent se = method.getAnnotation(SubscribeEvent.class);
            int modfiers = method.getModifiers();
            if (se != null) {
                if (!Modifier.isStatic(modfiers) && requireStatic) {
                    LOGGER.warning("Unable to register %s. Register as Object and not Class to fix. Or make it a static func".formatted(method.getName()));
                    continue;
                }
                if (!Modifier.isPublic(modfiers)) {
                    LOGGER.warning("Unable to register %s. Cant register private listeners");
                    continue;
                }
                if (method.getParameterCount() == 0 || method.getParameterCount() > 1) {
                    LOGGER.warning("Unable to register %s. Invalid listener".formatted(method.getName()));
                    continue;
                }

                Parameter parameter = method.getParameters()[0];
                LOGGER.info("""
                        Registering Listener:
                        Object/Class: %s
                        Method: %s
                        Type: %s
                        """.formatted(
                        name,
                        method.getName(),
                        parameter.getType().getName()
                ));
                if (IEvent.class.isAssignableFrom(parameter.getType())) {
                    BUILDERS.add(new EventBuilder<>(parameter.getType(), e -> {
                        try {
                            method.invoke(object, e);
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }));
                }
            }
        }

        return BUILDERS;
    }
}
