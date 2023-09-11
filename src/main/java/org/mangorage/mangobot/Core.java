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

package org.mangorage.mangobot;

import org.apache.logging.log4j.core.layout.YamlLayout;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.settings.MSettings;


/**
 * TODO: Use Log4J as our logger instead of System.out
 */
public class Core {

    public static void main(String[] args) {
        org.apache.logging.log4j.core.layout.YamlLayout.Builder builder = new YamlLayout.Builder();
        if (MSettings.BOT_TOKEN.get().equals("UNCHANGED"))
            throw new IllegalStateException("Must set BOT_TOKEN in .env found inside of botresources to a bot token!");

        Runtime.getRuntime().addShutdownHook(new Thread(Bot::close));
        // Test BCD

        Bot.initiate(MSettings.BOT_TOKEN.get());
    }
}