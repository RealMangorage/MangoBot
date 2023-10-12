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

import java.io.File;
import java.io.IOException;

public class Launcher {
    public static final String METADATA = "https://s01.oss.sonatype.org/content/repositories/releases/io/github/realmangorage/org/mangorage/mangobot/maven-metadata.xml";


    public static void main(String[] args) {
        System.out.println("Checking for Updates...");
        String[] metadata = Util.text(METADATA).split("/n");

        String version = null;
        for (String line : metadata) {
            if (line.contains("<version>")) {
                version = line.substring(line.indexOf("<version>") + 9, line.indexOf("</version>"));
                break;
            }
        }

        if (version != null) {
            System.out.println("Found latest Version: " + version);
            // Handle check for updates...
            Version currentVersion = Util.getVersion();
            if (currentVersion == null) {
                System.out.println("No current version found, downloading latest version...");
                Util.downloadBot(version);
                Util.saveVersion(version);
                startBot(version);
            } else {
                if (currentVersion.verison().equals(version)) {
                    System.out.println("No updates found, starting bot...");
                    startBot(version);
                } else {
                    System.out.println("Found new version, downloading...");
                    Util.downloadBot(version);
                    Util.saveVersion(version);
                    startBot(version);
                }
            }
        } else {
            System.out.println("Unable to start Bot. No Version Found...");
        }
    }

    public static void startBot(String version) {
        System.out.println("Starting bot now... Version: %s".formatted(version));

        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "mangobot.jar");
        pb.directory(new File("bot/"));
        pb.redirectOutput(new File("botdata/bot.log"));
        try {
            Process p = pb.start();
            System.out.println("Bot Started! PID: %s".formatted(p.pid()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
