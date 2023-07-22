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

package org.mangorage;

import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.music.SphnixModelLoader;
import org.mangorage.mangobot.core.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class Main {


    public static void main(String[] args) throws IOException, URISyntaxException {
        File libraries = new File("botresources/");
        if (!libraries.exists()) {
            if (args.length > 0) {
                Installer.installResources(Path.of("botresources/"), new URL("file:/F:/Discord%20Bot%20Projects/mangobot/build/resources/main"), "botresources/");
            } else {
                Installer.installResources(Path.of("botresources/"), Main.class.getProtectionDomain().getCodeSource().getLocation(), "botresources/");
            }
            System.out.println("Installed Bot Resources!");
        }

        if (Settings.BOT_TOKEN.get().equals("UNCHANGED"))
            throw new IllegalStateException("Must set BOT_TOKEN in .env found inside of botresources to a bot token!");

        SphnixModelLoader.init();

        Runtime.getRuntime().addShutdownHook(new Thread(Bot::close));

        try {
            Bot.init();
        } catch (InvalidTokenException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}