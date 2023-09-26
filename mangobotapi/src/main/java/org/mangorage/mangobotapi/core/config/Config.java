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

package org.mangorage.mangobotapi.core.config;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Pattern;

// Simple Key -> Value System
public class Config {
    public static final Pattern CONFIG_REGEX = Pattern.compile("^\\s*([\\w.\\-]+)\\s*(=)\\s*(['][^']*[']|[\"][^\"]*[\"]|[^#]*)?\\s*(#.*)?$");

    private final Path file;

    private final HashMap<String, String> ENTRIES = new HashMap<>();

    public Config(String directory, String filename) {
        String dir = directory
                .replaceAll("\\\\", "/")
                .replaceFirst("\\.env$", "")
                .replaceFirst("/$", "");

        String location = dir + "/" + filename;
        String lowerLocation = location.toLowerCase();
        Path path = lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")
                ? Paths.get(URI.create(location))
                : Paths.get(location);

        this.file = path;
        if (Files.exists(path)) {
            try {
                Files.readAllLines(path).forEach(e -> {
                    if (CONFIG_REGEX.matcher(e).matches()) {
                        var a = e.split("=", 2);
                        ENTRIES.put(a[0], a[1]);
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }

    public String get(String ID) {
        return ENTRIES.get(ID);
    }

    public void set(String ID, String value) {
        ENTRIES.put(ID, value);
    }

    public void save() {
        try {
            var writer = Files.newBufferedWriter(file);
            ENTRIES.entrySet().stream().forEach(e -> {
                var line = "%s=%s".formatted(e.getKey(), e.getValue());
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
