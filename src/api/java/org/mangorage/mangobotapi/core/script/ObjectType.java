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

import java.util.Objects;
import java.util.function.Predicate;

public enum ObjectType {
    INTEGER(o -> o instanceof Integer),
    DOUBLE(o -> o instanceof Double),
    FLOAT(o -> o instanceof Float),
    LONG(o -> o instanceof Long),
    NUMBER(o -> o instanceof Number),
    BOOLEAN(o -> o instanceof Boolean),
    STRING(o -> o instanceof String),
    OBJECT(o -> !Objects.isNull(o)),
    NULL(Objects::isNull),
    UNDEFINED(o -> false);

    public static ObjectType typeOf(Object o) {
        for (ObjectType type : ObjectType.values())
            if (type.is(o))
                return type;
        return UNDEFINED;
    }

    private final Predicate<Object> predicate;

    ObjectType(Predicate<Object> predicate) {
        this.predicate = predicate;
    }

    public boolean is(Object o) {
        return predicate.test(o);
    }
}
