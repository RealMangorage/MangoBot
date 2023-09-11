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

package org.mangorage.mangobotapi.core.script;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

public class Global {
    private final HashMap<Class<?>, HashMap<String, Object>> VARIABLES = new HashMap<>();


    @SuppressWarnings("unchecked")
    public <X> X get(Class<X> type, String id) {
        if (VARIABLES.containsKey(type) && VARIABLES.get(type).containsKey(id)) {
            return (X) VARIABLES.get(type).get(id);
        }

        return null;
    }

    public <X> void put(Class<X> type, String id, X value) {
        VARIABLES.computeIfAbsent(type, (a) -> new HashMap<>()).put(id, value);
    }

    public Class<?> getClassByName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Object createObjectByClass(Class<?> oClass, Object... params) {
        Class<?>[] paramtypes = (Class<?>[]) Arrays.stream(params).map(Object::getClass).toArray();
        try {
            return oClass.getConstructor(paramtypes).newInstance(params);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
