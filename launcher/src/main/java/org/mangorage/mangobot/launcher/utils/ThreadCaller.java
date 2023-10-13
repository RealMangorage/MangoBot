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

package org.mangorage.mangobot.launcher.utils;

public class ThreadCaller {

    public static String getThreadTag() {
        return "[Thread -> %s]".formatted(Thread.currentThread().getName());
    }

    public static void println(String string) {
        System.out.println("%s %s".formatted(getThreadTag(), string));
    }

    public static void printf(String main, Object... args) {
        String result = main;
        if (args.length > 0)
            result = main.formatted(args);

        System.out.println("%s %s".formatted(getThreadTag(), result));
    }

    public static void execute(Runnable runnable) {
        execute(runnable, null);
    }

    public static void execute(Runnable runnable, String ThreadID) {
        Thread thread = new Thread(runnable);
        if (ThreadID != null)
            thread.setName(ThreadID);
        thread.start();
    }
}
