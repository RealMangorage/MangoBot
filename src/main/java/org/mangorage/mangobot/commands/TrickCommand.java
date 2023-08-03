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

package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.core.commands.guilds.ForgeCommands;
import org.mangorage.mangobot.core.commands.registry.CommandAlias;
import org.mangorage.mangobot.core.commands.registry.CommandRegistry;
import org.mangorage.mangobot.core.commands.registry.PermissionRegistry;
import org.mangorage.mangobot.core.commands.util.Arguments;
import org.mangorage.mangobot.core.commands.util.CommandResult;

import java.util.HashMap;

public class TrickCommand extends AbstractCommand {

    private final CommandRegistry registry;
    private final HashMap<String, String> CONTENT = new HashMap<>();

    public TrickCommand(CommandRegistry registry) {
        this.registry = registry;
    }


    @Override
    public CommandResult execute(Message message, Arguments args) {
        Member member = message.getMember();
        String type = args.get(0);
        String id = args.get(1);
        String content = args.getFrom(2);

        if (type.equals("-a") && id != null && content != null) {
            if (!PermissionRegistry.hasNeededPermission(member, ForgeCommands.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;
            CONTENT.computeIfAbsent(id, (idb) -> content);
            registry.registerAlias(ForgeCommands.TRICK.get(), CommandAlias.of(id, (command, messageb, argsb) -> {
                return command.execute(messageb, Arguments.of("-s", id));
            }));
            CommandRegistry.build();
            message.reply("Added: " + id).queue();
            return CommandResult.PASS;
        } else if (type.equals("-r") && id != null) {
            if (!PermissionRegistry.hasNeededPermission(member, ForgeCommands.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;
        } else if (type.equals("-s") && id != null) {
            MessageChannelUnion channel = message.getChannel();
            channel.sendMessage(CONTENT.get(id)).queue();
            return CommandResult.PASS;
        }

        return CommandResult.FAIL;
    }
}
