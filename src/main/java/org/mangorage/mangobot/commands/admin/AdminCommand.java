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

package org.mangorage.mangobot.commands.admin;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.core.commands.util.Arguments;
import org.mangorage.mangobot.core.commands.util.CommandResult;
import org.mangorage.mangobot.core.permissions.PermissionNode;

@Deprecated
public class AdminCommand extends AbstractCommand {
    private final PermissionNode node;

    public AdminCommand(PermissionNode node) {
        this.node = node;
    }

    @Override
    public CommandResult execute(Message message, Arguments args) {
        if (node.hasPermissions(message.getMember())) {
            message.getChannel().sendMessage("Sufficent permissions").queue();
        } else {
            message.getChannel().sendMessage("Insufficent permisisons").queue();
        }
        return CommandResult.PASS;
    }
}
