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

package org.mangorage.mangobotapi.core.util;

public class Arguments {
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

    public boolean hasArg(String arg) {
        int index = 0;
        while (has(index)) {
            if (get(index).equals(arg))
                return true;
            index++;
        }
        return false;
    }

    public int getArgIndex(String arg) {
        int index = 0;

        while (has(index)) {
            if (get(index).equals(arg))
                return index;
            index++;
        }

        return -1;
    }

    public static void main(String[] args) {
        Arguments arguments = Arguments.of("-a", "value", "howdy", "okay", "eeeek!", "-v", "cool!");
        System.out.println(arguments.findArg("-a"));
        System.out.println(arguments.findArg("-v"));
    }
}
