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

package org.mangorage.mangobotapi.core.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mangorage.mangobotapi.core.util.APIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DataHandler<T> {
    private static final Gson GSON_EXPOSE = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final Gson GSON_NON_EXPOSE = new Gson();


    public static List<File> getFiles(ArrayList<File> files, String dir, int depthLimit, int depth) {
        File file = new File(dir);
        File[] filesArray = file.listFiles();
        if (file.isDirectory() && filesArray != null) {
            for (File f : filesArray) {
                if (f.isDirectory()) {
                    if (depth < depthLimit)
                        getFiles(files, f.getAbsolutePath(), depthLimit, +1);
                } else {
                    files.add(f);
                }
            }
        }
        return files;
    }

    public static String getDirectoryWithArgs(String path, String... args) {
        String result = path;
        for (String arg : args) {
            result = "%s/%s".formatted(result, arg);
        }

        return result;
    }

    /**
     * @param objectLoadingConsumer
     * @param type
     * @param directory
     * @param fileName              -> %s if you want to wildcard it.
     * @param <T>
     * @return
     */
    public static <T> DataHandler<T> create(Consumer<T> objectLoadingConsumer, Class<T> type, String directory, String fileName) {
        return create(objectLoadingConsumer, type, directory, fileName, Properties.create());
    }

    /**
     * @param objectLoadingConsumer
     * @param type
     * @param directory
     * @param fileName              -> %s if you want to wildcard it.
     * @param properties            -> properties for this DataHandler
     * @param <T>
     * @return
     */
    public static <T> DataHandler<T> create(Consumer<T> objectLoadingConsumer, Class<T> type, String directory, String fileName, Properties properties) {
        return new DataHandler<>(objectLoadingConsumer, type, directory, fileName, properties);
    }

    public static class Properties {
        public static Properties create() {
            return new Properties();
        }

        private boolean useExpose = false;
        private int depthLimit = 1;

        private Properties() {
        }

        /**
         * Wether or not we should use -> {@link com.google.gson.annotations.Expose}
         *
         * @return
         */
        public Properties useExposeAnnotation() {
            this.useExpose = true;
            return this;
        }

        public Properties setDepthLimit(int depthLimit) {
            this.depthLimit = depthLimit;
            return this;
        }

        public boolean usesExposeAnnotation() {
            return useExpose;
        }

        public int getDepthLimit() {
            return depthLimit;
        }

        private Properties copy() {
            if (useExpose)
                return new Properties().setDepthLimit(depthLimit).useExposeAnnotation();
            else
                return new Properties().setDepthLimit(depthLimit);
        }
    }


    private final Consumer<T> objectLoadingConsumer;
    private final Class<T> type;
    private final String directory;
    private final String fileName;
    private final Properties properties;

    private DataHandler(Consumer<T> objectLoadingConsumer, Class<T> type, String directory, String fileName, Properties properties) {
        this.objectLoadingConsumer = objectLoadingConsumer;
        this.type = type;
        this.directory = directory;
        this.fileName = fileName;
        this.properties = properties.copy();
    }

    private Gson getGson() {
        return properties.usesExposeAnnotation() ? GSON_EXPOSE : GSON_NON_EXPOSE;
    }

    /**
     * @param object -> object to serialize
     * @param args   -> used if there is any %s in the directory
     */
    public void save(T object, String... args) {
        save(fileName, object, args);
    }

    /**
     * @param object   -> object to serialize
     * @param fileName -> the name for the file. Includes extension
     * @param args     -> used if there is any %s in the directory
     */
    public void save(String fileName, T object, String... args) {
        // Make it so args can be construected and that we add /arg1/arg2/arg3 to the directory
        APIUtil.saveObjectToFile(getGson(), object, getDirectoryWithArgs(directory, args), fileName);
    }


    /**
     * Loads all objects in the directory
     */
    public void loadAll() {
        File file = new File(directory);
        if (!file.exists() && !file.mkdirs())
            return;

        List<File> files = getFiles(new ArrayList<>(), directory, properties.getDepthLimit(), 0);
        for (File f : files) {

            if (!fileName.equals("%s"))
                if (!f.getName().equals(fileName)) continue; // Don't bother loading if it's not the file we want

            T t = APIUtil.loadJsonToObject(getGson(), f.getAbsolutePath(), type);
            objectLoadingConsumer.accept(t);
        }
    }
}
