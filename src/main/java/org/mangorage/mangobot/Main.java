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
import org.mangorage.mangobot.core.util.BotSettings;
import org.mangorage.mangobotapi.core.util.APIUtil;

import javax.swing.*;
import java.awt.*;
import java.io.Console;


public class Main {
    public Main() {
        main(new String[]{});
    }

    public static void main(String[] args) {
        var currentToken = BotSettings.BOT_TOKEN.get();
        if (currentToken.equalsIgnoreCase("empty"))
            requestBotToken(false);
        if (!APIUtil.isValidBotToken(currentToken))
            requestBotToken(true);

        Runtime.getRuntime().addShutdownHook(new Thread(Bot::close));
        Bot.initiate(BotSettings.BOT_TOKEN.get());
    }


    private static void requestBotToken(boolean wasInvalid) {
        String message = "Enter your Bot Token to get started...";
        if (wasInvalid) {
            System.out.println("Current Bot Token detected as invalid...");
            System.out.println("Please re-enter your Discord Bot's Token:");
            message = "Re-Enter your Bot Token";
        } else {
            System.out.println("Please enter your Discord Bot's Token:");
        }

        if (GraphicsEnvironment.isHeadless()) {
            BotSettings.BOT_TOKEN.set("empty");
            System.out.println("Manually set the token under botresources/.env");
            System.exit(0);
        }


        final String token;
        if (System.console() == null) {
            final JPasswordField pf = new JPasswordField();
            pf.setEchoChar('#');
            int reponse = JOptionPane.showConfirmDialog(
                    null,
                    pf,
                    message,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (reponse == JOptionPane.OK_CANCEL_OPTION || reponse == JOptionPane.CLOSED_OPTION)
                System.exit(0);

            token = reponse == JOptionPane.OK_OPTION ? new String(pf.getPassword()) : "";

        } else {
            Console c = System.console();
            char[] chars = c.readPassword("Enter Bot Token:");
            token = String.valueOf(chars);
        }

        if (APIUtil.isValidBotToken(token)) {
            BotSettings.BOT_TOKEN.set(token);
            System.out.println("Configured the Bot Token. Proceeding to init Bot.");
        } else {
            requestBotToken(true);
        }
    }
}
