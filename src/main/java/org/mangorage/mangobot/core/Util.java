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

package org.mangorage.mangobot.core;


import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class Util {
    public static Integer parseStringIntoInteger(String s) {
        Integer res = null;
        try {
            res = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }
        return res;
    }

    public static void saveObjectToFile(Object object, String directory, String fileName) {
        try {
            Gson gson = new Gson();
            String jsonData = gson.toJson(object);

            File dirs = new File(directory);
            if (!dirs.exists() && !dirs.mkdirs()) return;
            Files.writeString(Path.of("%s/%s".formatted(directory, fileName)), jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFile(String directory, String fileName) {
        try {
            Files.delete(Path.of("%s/%s".formatted(directory, fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadJsonToObject(String file, Class<T> cls) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(Files.readString(Path.of(file)), cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // execute runnable on a thread
    public static void call(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static boolean copy(Path from, BasicFileAttributes a, Path target) {
        System.out.println("Copy " + (a.isDirectory() ? "DIR " : "FILE") + " => " + target);
        try {
            if (a.isDirectory())
                Files.createDirectories(target);
            else if (a.isRegularFile())
                Files.copy(from, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }
}
