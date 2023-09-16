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

package org.mangorage.mangobot.test;

import org.mangorage.mangobotapi.core.script.Global;
import org.mangorage.mangobotapi.core.script.ScriptParser;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.function.Function;

public class Test {
    public static <X, Y> Function<X, Y> createFunc(Class<X> typeX, Class<Y> retY, Function<X, Y> func) {
        return func;
    }


    public static void main(String[] args) {
        ScriptParser.init();
        ScriptEngine engine = ScriptParser.get();

        engine.put("GLOBAL", new Global());
        engine.put("DOUBLE", createFunc(Long.class, Double.class, e -> {
            return (double) e;
        }));
        engine.put("INTEGER", createFunc(Long.class, Integer.class, Long::intValue));

        engine.put("FLOAT", createFunc(Long.class, Float.class, e -> {
            return (float) e;
        }));

        engine.put("toString", createFunc(Object.class, String.class, Object::toString));

        String ev = """
                function f() {
                    GLOBAL.out.println(GLOBAL.typeOf(GLOBAL.get("Test")));
                    GLOBAL.out.println(GLOBAL.get("Test").getClass());
                }
                """;

        var aa = engine.getFactory().getMethodCallSyntax(ev, "f");

        try {
            engine.eval(ev);
            Invocable inv = (Invocable) engine;
            inv.invokeFunction("f");
            System.out.println("OUTPUT -> %s".formatted(aa));
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
