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

import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.BotSettings;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;


/**
 * TODO: Use Log4J as our logger instead of System.out
 */
public class Core {

    public static void main(String[] args) {
        if (BotSettings.BOT_TOKEN.get().equalsIgnoreCase("empty")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Please enter your Discord Bot's Token:");

            final String token;
            final String message = "Enter Bot Token";
            if (System.console() == null) {
                final JPasswordField pf = new JPasswordField();
                token = JOptionPane.showConfirmDialog(null, pf, message,
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION
                        ? new String(pf.getPassword()) : "";

            } else {
                Console c = System.console();
                char[] chars = c.readPassword("Enter Bot Token:");
                token = String.valueOf(chars);
            }

            BotSettings.BOT_TOKEN.set(token);
            System.out.println("Configured the Bot Token. Proceeding to init Bot.");
        }


        Runtime.getRuntime().addShutdownHook(new Thread(Bot::close));
        Bot.initiate(BotSettings.BOT_TOKEN.get());
    }
}