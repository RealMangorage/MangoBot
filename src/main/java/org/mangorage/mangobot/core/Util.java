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

package org.mangorage.mangobot.core;


import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.core.commands.CommandManager;

import static org.mangorage.mangobot.core.Constants.COMMAND_PREFIX;

public class Util {
    public static boolean handleMessage(Message message) {
        String messageContent = message.getContentDisplay();
        String commandFull = messageContent.split(" ")[0];

        if (messageContent.startsWith(COMMAND_PREFIX)) {
            String command = commandFull.substring(1);
            if (CommandManager.getInstance().isValidCommand(command)) {
                CommandManager.getInstance().handleCommand(command, message);
                return true;
            } else if (CommandManager.getInstance().isValidCommandAlias(command)) {
                CommandManager.getInstance().handleCommandAlias(command, message);
                return true;
            }
        }

        return false;
    }
}
