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

package org.mangorage.mangobotapi.core.reflection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DependencyLoader {

    public static List<Class> classes;
    public static boolean isLoaded = false;

    public static void init() {
        if (isLoaded) return;
        isLoaded = true;
        var loader = new DependencyLoader();
        try {
            //File file = new File("F:\\Discord Bot Projects\\mangobot\\lib");
            List<File> jars = Arrays.asList(new File("F:\\Discord Bot Projects\\mangobot\\lib").listFiles());
            URL[] urls = new URL[jars.size()];
            for (int i = 0; i < jars.size(); i++) {
                try {
                    urls[i] = jars.get(i).toURI().toURL();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            var a = new JarFileLoader(urls);

            List<String> classNames = new ArrayList<String>();
            for (File jar : jars) {
                ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));
                for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        // This ZipEntry represents a class. Now, what class does it represent?
                        String className = entry.getName().replace('/', '.'); // including ".class"
                        classNames.add(className.substring(0, className.length() - ".class".length()));
                    }
                }
            }

            Thread.currentThread().setContextClassLoader(a);

            List<Thread> threads = new ArrayList<>();
            ArrayList<URL> URLS = new ArrayList<>();

            for (String className : classNames) {
                var thread = new Thread(() -> {
                    try {
                        var c = a.loadClass(className, true);
                        System.out.println("%s".formatted(c.getCanonicalName()));

                    } catch (Exception e) {
                    }
                });
                thread.start();
            }

            for (String className : classNames) {
                var thread = new Thread(() -> {
                    try {
                        a.addFile(className);

                    } catch (Exception e) {
                    }
                });
                thread.start();
            }

            while (threads.stream().filter(thread -> thread.isAlive()).toList().size() > 0) {
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
