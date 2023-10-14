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

import org.mangorage.mangobot.launcher.data.Maven;
import org.mangorage.mangobot.launcher.data.Version;
import org.mangorage.mangobot.launcher.utils.Util;

import java.io.File;

public class Launcher {
    public static final Maven MAVEN = new Maven(
            "https://s01.oss.sonatype.org/content/repositories/releases",
            "io.github.realmangorage",
            "mangobot"
    );

    public static void main(String[] args) {
        System.out.println("Checking for Updates...");

        File dest = new File("bot/mangobot.jar");
        File parent = dest.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            System.out.println("Unable to create directories for bot jar...");
            return;
        }

        String metadata = MAVEN.downloadMetadata();
        if (metadata != null) {
            String version = Maven.parseLatestVersion(metadata);

            if (version != null) {
                System.out.println("Found latest Version: " + version);
                // Handle check for updates...
                Version currentVersion = Util.getVersion();
                if (currentVersion == null) {
                    System.out.println("No current version found, downloading latest version...");
                    MAVEN.downloadTo(version, dest);
                    Util.saveVersion(version);
                } else {
                    if (currentVersion.version().equals(version)) {
                        System.out.println("No updates found, starting bot...");
                    } else {
                        System.out.println("Found new version, downloading...");
                        MAVEN.downloadTo(version, dest);
                        Util.saveVersion(version);
                    }
                }
            }
        }

        var version = Util.getVersion();
        if (version != null) {
            //startBot(version.version());
        } else {
            System.out.println("Unable to find Bot jar...");
        }
    }

    public static void startBot(String version) {
        System.out.println("Starting bot now... Version: %s".formatted(version));

        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "mangobot.jar");
        pb.directory(new File("bot/"));
        pb.redirectOutput(new File("botdata/bot.log"));
        new Monitor(pb);
    }

}
