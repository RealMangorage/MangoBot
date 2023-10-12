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

package org.mangorage.mangobotapi.core.commands;

import java.util.function.Function;

// TODO: Make this better?
public class Arguments {
    private static final Arguments EMPTY = new Arguments(new String[0]);

    public static Arguments empty() {
        return EMPTY;
    }

    public static Arguments of(String... args) {
        return new Arguments(args);
    }

    private final String[] args;

    private Arguments(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public String get(int index) {
        return index >= args.length ? null : args[index];
    }

    public String getOrDefault(int index, String value) {
        return index >= args.length ? value : args[index];
    }

    public boolean has(int index) {
        return index < args.length;
    }

    public String getFrom(int index) {
        StringBuilder result = new StringBuilder();
        boolean first = false;
        while (has(index)) {
            result.append(" ").append(get(index));
            index++;
        }

        return result.toString().trim();
    }

    /**
     * finds the arg and returns its value
     * using -> "-nodeID 10"
     * -> find("-nodeID") returns 10
     * <p>
     * returns null if it doesn't have anything after, or if it's unable to find arg.
     * Use findArgOrDefault to circumvent this!. If a value is provided but is
     * the next argyment (e.g "-nodeID -test") it will return "-test" instead of
     * "10" to fix this use findStrictArg(String arg, Predicate)
     *
     * @param arg
     * @return
     */
    public String findArg(String arg) {
        String result = null;
        int index = 0;

        loop:
        while (has(index)) {
            if (get(index).equals(arg)) {
                result = get(index + 1);
                break loop;
            }
            index++;
        }

        return result;
    }

    public String findArgOrDefault(String arg, String defaultValue) {
        String result = findArg(arg);
        return result == null ? defaultValue : result;
    }

    /**
     * @param arg
     * @param resolver -> This is will resolve the String into X
     * @param <X>
     * @return
     */
    public <X> X findArg(String arg, Function<String, X> resolver) {
        String result = findArg(arg);
        X resolved_result = null;

        if (result != null) {
            try {
                resolved_result = resolver.apply(result);
            } catch (Exception ignored) {
            }
        }

        return result == null || resolved_result == null ? null : resolved_result;
    }

    public <X> X findArgOrDefault(String arg, Function<String, X> resolver, X defaultValue) {
        String result = findArg(arg);
        X resolved_result = null;

        if (result != null) {
            try {
                resolved_result = resolver.apply(result);
            } catch (Exception ignored) {
            }
        }

        return result == null || resolved_result == null ? defaultValue : resolved_result;
    }

    public boolean hasArg(String arg) {
        int index = 0;
        while (has(index)) {
            if (get(index).equals(arg))
                return true;
            index++;
        }
        return false;
    }

    /**
     * Gets the index at where the arg begins,
     * next index should be value for that arg
     *
     * @param arg
     * @return
     */
    public int getArgIndex(String arg) {
        int index = 0;

        while (has(index)) {
            if (get(index).equals(arg))
                return index;
            index++;
        }

        return -1;
    }
}
