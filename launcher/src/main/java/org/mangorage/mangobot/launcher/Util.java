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

package org.mangorage.mangobot.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Util {
    private static final String BOT_URL = "https://s01.oss.sonatype.org/content/repositories/releases/io/github/realmangorage/org/mangorage/mangobot/{VERSION}/mangobot-{VERSION}-all.jar";
    private static final String BOT_DIR = "bot/";
    private static final String DATA_DIR = "botdata/";

    private static final Gson GSON = new GsonBuilder().create();

    public static String text(String url) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            return IOUtils.toString(new InputStreamReader(in));
        } catch (IOException e) {
            // handle exception
        }
        return null;
    }

    public static void downloadBot(String version) {
        String URL = BOT_URL.replace("{VERSION}", version);
        System.out.println(URL);
        createDirs();

        try {
            FileUtils.copyURLToFile(new URL(URL), new File("%s/mangobot.jar".formatted(BOT_DIR)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static boolean createDirs() {
        File file = new File(BOT_DIR);
        if (!file.exists())
            return file.mkdirs();
        return false;
    }

    public static boolean deleteOldBot() {
        File file = new File(BOT_DIR);
        if (file.exists()) {
            File bot = new File("%s/mangobot.jar".formatted(BOT_DIR));
            if (bot.exists())
                return bot.delete();
        }
        return false;
    }

    public static void saveVersion(String version) {
        saveObjectToFile(GSON, new Version(version), DATA_DIR, "version.json");
    }

    public static Version getVersion() {
        File file = new File("%s/version.json".formatted(DATA_DIR));
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            return null;
        return loadJsonToObject(new Gson(), "%s/version.json".formatted(DATA_DIR), Version.class);
    }


    public static void saveObjectToFile(Gson gson, Object object, String directory, String fileName) {
        try {
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

    public static <T> T loadJsonToObject(Gson gson, String file, Class<T> cls) {
        try {
            return gson.fromJson(Files.readString(Path.of(file)), cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
