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

package org.mangorage.mangobot.core.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.mangorage.mangobot.commands.core.AbstractCommand;
import org.mangorage.mangobot.core.Constants;

import java.util.Arrays;
import java.util.HashMap;

public class CommandRegistry {
    public enum CommandType {
        COMMAND,
        ALIAS,
        UNKNOWN
    }

    private static final CommandRegistry GLOBAL = new CommandRegistry(null);
    private static final HashMap<String, CommandRegistry> GUILDS = new HashMap<>();

    public static CommandRegistry guild(String guildID) {
        return GUILDS.computeIfAbsent(guildID, CommandRegistry::new);
    }

    public static CommandRegistry global() {
        return GLOBAL;
    }

    public static void handleMessage(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String guildID = event.getGuild().getId();
        String raw = message.getContentRaw();
        String[] rawArray = raw.split(" ");


        if (raw.startsWith(Constants.COMMAND_PREFIX)) {
            String command = rawArray[0].replaceFirst("!", "");
            String[] params = raw.replaceFirst("!" + command, "").trim().split(" ");

            CommandType globalType = GLOBAL.getType(command);
            CommandType guildType = CommandType.UNKNOWN;
            CommandRegistry guildRegistry = null;
            if (GUILDS.containsKey(guildID)) {
                guildRegistry = GUILDS.get(guildID);
                guildType = guildRegistry.getType(command);
            }

            if (globalType == CommandType.UNKNOWN && guildType == CommandType.UNKNOWN) {
                event.getMessage().reply("Invalid Command").queue();
                return;
            } else {
                if (globalType != CommandType.UNKNOWN)
                    GLOBAL.execute(message, command, params);
                else
                    guildRegistry.execute(message, command, params);
            }
        }
    }

    private final String guildID;
    private HashMap<String, CommandHolder<?>> COMMANDS = new HashMap<>();
    private HashMap<String, CommandAlias> COMMAND_ALIASES = new HashMap<>();

    private CommandRegistry(String guildID) {
        this.guildID = guildID;
    }

    public <T extends AbstractCommand> CommandHolder<T> register(String commandID, T command, CommandAlias.Builder... builders) {
        if (guildID == null && GLOBAL.getType(commandID) != CommandType.UNKNOWN)
            throw new IllegalStateException("Tried to register a command that has already been taken by the global registry: %s".formatted(commandID));

        CommandHolder<T> HOLDER = CommandHolder.create(commandID, command);

        if (COMMANDS.containsKey(commandID))
            throw new IllegalStateException("Tried to register a command using an ID that's already been used CommandID: %s".formatted(commandID));

        COMMANDS.put(commandID, HOLDER);
        Arrays.stream(builders).forEach(e -> register(HOLDER, e));

        return HOLDER;
    }


    public <T extends AbstractCommand> CommandAlias register(CommandHolder<T> holder, CommandAlias.Builder builder) {
        CommandAlias alias = builder.build(holder);
        String aliasID = alias.getID();

        if (COMMAND_ALIASES.containsKey(aliasID)) {
            CommandAlias taken = COMMAND_ALIASES.get(aliasID);
            throw new IllegalStateException("""
                    Tried to register a Command Alias that already is being used: %s
                                        
                    CommandID who is using it: %s
                    CommandID who tried to use it: %s
                    """.formatted(aliasID, taken.getCommandHolder().getID(), holder.getID()));
        }

        COMMAND_ALIASES.put(aliasID, alias);
        holder.addAlias(alias);

        return alias;
    }


    public CommandType getType(String value) {
        if (COMMANDS.containsKey(value))
            return CommandType.COMMAND;
        if (COMMAND_ALIASES.containsKey(value))
            return CommandType.ALIAS;
        return CommandType.UNKNOWN;
    }

    public CommandHolder<?> getCommandHolder(String command) {
        return switch (getType(command)) {
            case COMMAND -> COMMANDS.get(command);
            case ALIAS -> COMMAND_ALIASES.get(command).getCommandHolder();
            case UNKNOWN -> null;
        };
    }

    public void execute(Message message, String command, String[] args) {
        CommandType type = getType(command);
        CommandHolder<?> commandHolder = getCommandHolder(command);
        if (commandHolder == null)
            return;

        if (guildID == null && commandHolder.getCommand().isGuildOnly() && !message.isFromGuild())
            return;

        switch (type) {
            case COMMAND -> COMMANDS.get(command).getCommand().execute(message, args);
            case ALIAS -> COMMAND_ALIASES.get(command).execute(message, args);
        }
    }
}
