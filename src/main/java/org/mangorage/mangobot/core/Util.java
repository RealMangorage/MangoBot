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


import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class Util {
    public static Integer parseStringIntoInteger(String s) {
        Integer res = null;

        try {
            res = Integer.parseInt(s);
        } catch (NumberFormatException e) {

        }
        return res;
    }

    // execute runnable on a thread
    public static void call(Runnable runnable) {
        new Thread(runnable).start();
    }


    public static void copyDir(ClassLoader classLoader, String resPath, Path target) throws IOException, URISyntaxException {
        System.out.println("copyDir(" + resPath + ", " + target + ")");

        URI uri = classLoader.getResource(resPath).toURI();

        BiPredicate<Path, BasicFileAttributes> foreach = (p, a) -> copy(p, a, Path.of(target.toString(), p.toString())) && false;

        try (var fs = FileSystems.newFileSystem(uri, Map.of())) {
            final Path subdir = fs.getPath(resPath);
            for (Path root : fs.getRootDirectories()) {
                System.out.println("root: " + root);
                try (Stream<Path> stream = Files.find(subdir, Integer.MAX_VALUE, foreach)) {
                    stream.count();
                }
            }
        }
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
