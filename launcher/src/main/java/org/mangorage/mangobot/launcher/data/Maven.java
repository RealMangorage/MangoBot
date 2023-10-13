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

package org.mangorage.mangobot.launcher.data;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Represents a Maven dependency.
 *
 * @param repository (https://s01.oss.sonatype.org/content/repositories/releases/)
 * @param groupId    (io.github.realmangorage)
 * @param artifactId (mangobot)
 */
public record Maven(String repository, String groupId, String artifactId) {

    public void downloadTo(String version, File dest) {
        String URL = "%s/%s/%s/%s/%s-%s-all.jar".formatted(repository, groupId.replace(".", "/"), artifactId, version, artifactId, version);
        try {
            FileUtils.copyURLToFile(new URL(URL), dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String downloadMetadata() {
        String url = repository + "/" + groupId.replace(".", "/") + "/" + artifactId + "/maven-metadata.xml";
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            return IOUtils.toString(new InputStreamReader(in));
        } catch (IOException e) {
            // handle exception
        }
        return null;
    }

    public static String parseLatestVersion(String metadata) {
        String[] lines = metadata.split("\n");
        for (String line : lines) {
            if (line.contains("<latest>")) {
                return line.substring(line.indexOf("<latest>") + 8, line.indexOf("</latest>"));
            }
        }
        return null;
    }


}
