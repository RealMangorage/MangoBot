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

package org.mangorage.mangobotapi.core.registry;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.mangorage.mangobotapi.MangoBotAPI;
import org.mangorage.mangobotapi.core.AbstractCommand;
import org.mangorage.mangobotapi.core.events.CommandEvent;
import org.mangorage.mangobotapi.core.util.Arguments;
import org.mangorage.mangobotapi.core.util.CommandResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO: Figure out how to do proper registering
 * So that GLOBAL gets its commands registered first then anything afterwards
 */


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


    public static void build() {
        HashMap<CommandRegistry, ArrayList<RegistryObject<CommandHolder<?>>>> COMMANDS_TYPES = new HashMap<>();
        HashMap<CommandRegistry, ArrayList<RegistryObject<CommandAlias>>> COMMAND_ALIASES_TYPES = new HashMap<>();

        COMMANDS_TYPES.computeIfAbsent(global(), (key) -> new ArrayList<>());
        COMMAND_ALIASES_TYPES.computeIfAbsent(global(), (key) -> new ArrayList<>());


        global().REGISTRY_OBJECTS.forEach(ro -> {
            if (ro.getType() == CommandType.COMMAND)
                COMMANDS_TYPES.get(global()).add((RegistryObject<CommandHolder<?>>) ro);
            if (ro.getType() == CommandType.ALIAS)
                COMMAND_ALIASES_TYPES.get(global()).add((RegistryObject<CommandAlias>) ro);
        });

        GUILDS.forEach((key, guild) -> {
            COMMANDS_TYPES.computeIfAbsent(guild, (k) -> new ArrayList<>());
            COMMAND_ALIASES_TYPES.computeIfAbsent(guild, (k) -> new ArrayList<>());

            guild.REGISTRY_OBJECTS.forEach(ro -> {
                if (ro.getType() == CommandType.COMMAND)
                    COMMANDS_TYPES.get(guild).add((RegistryObject<CommandHolder<?>>) ro);
                if (ro.getType() == CommandType.ALIAS)
                    COMMAND_ALIASES_TYPES.get(guild).add((RegistryObject<CommandAlias>) ro);
            });
        });

        global().registerCommands(COMMANDS_TYPES.get(global()));
        GUILDS.forEach((key, guild) -> {
            guild.registerCommands(COMMANDS_TYPES.get(guild));
        });

        global().registerAliases(COMMAND_ALIASES_TYPES.get(global()));
        GUILDS.forEach((key, guild) -> {
            guild.registerAliases(COMMAND_ALIASES_TYPES.get(guild));
        });

        COMMANDS_TYPES.clear();
        COMMAND_ALIASES_TYPES.clear();

        global().clearRegistryBuilder();
        GUILDS.forEach((a, b) -> b.clearRegistryBuilder());
    }

    public static void handleMessage(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String commandPrefix = MangoBotAPI.getInstance().getCommandPrefix();
        String guildID = event.getGuild().getId();
        String raw = message.getContentRaw();
        String[] rawArray = raw.split(" ");
        Member member = event.getMember();


        if (raw.startsWith(commandPrefix)) {
            String command = rawArray[0].replaceFirst(commandPrefix, "");
            String[] params = raw.replaceFirst(commandPrefix + command, "").trim().split(" ");

            CommandType globalType = GLOBAL.getType(command);
            CommandType guildType = CommandType.UNKNOWN;
            CommandRegistry guildRegistry = null;
            if (GUILDS.containsKey(guildID)) {
                guildRegistry = GUILDS.get(guildID);
                guildType = guildRegistry.getType(command);
            }

            if (globalType == CommandType.UNKNOWN && guildType == CommandType.UNKNOWN) {
                CommandEvent commandEvent = MangoBotAPI.getInstance().getEventBus().post(new CommandEvent(message, command, Arguments.of(params)));
                if (!commandEvent.isHandled())
                    event.getMessage().reply("Invalid Command").queue();
            } else {
                if (globalType != CommandType.UNKNOWN)
                    GLOBAL.execute(message, command, params);
                else
                    guildRegistry.execute(message, command, params);
            }
        }
    }

    private final String guildID;
    private final List<RegistryObject<?>> REGISTRY_OBJECTS = new ArrayList<>();
    private HashMap<String, CommandHolder<?>> COMMANDS = new HashMap<>();
    private HashMap<String, CommandAlias> COMMAND_ALIASES = new HashMap<>();
    private final AtomicBoolean frozen = new AtomicBoolean(false);

    private CommandRegistry(String guildID) {
        this.guildID = guildID;
    }

    public void clearRegistryBuilder() {
        REGISTRY_OBJECTS.clear();
        //frozen.set(true);
    }

    public <X extends AbstractCommand> RegistryObject<CommandHolder<X>> register(String id, X command, CommandAlias.Builder... builders) {
        if (frozen.get())
            throw new IllegalStateException("Cannot register stuff to a frozen registry");
        RegistryObject<CommandHolder<X>> RO = new RegistryObject<>(CommandHolder.create(id, command), CommandType.COMMAND);
        REGISTRY_OBJECTS.add(RO);
        Arrays.asList(builders).forEach(e -> registerAlias(RO.get(), e));
        return RO;
    }

    public <X extends AbstractCommand> RegistryObject<CommandAlias> registerAlias(CommandHolder<X> holder, CommandAlias.Builder builder) {
        if (frozen.get())
            throw new IllegalStateException("Cannot register stuff to a frozen registry. Use CommandEvent to handle further command alias's");
        RegistryObject<CommandAlias> RO = new RegistryObject<>(builder.build(holder), CommandType.ALIAS);
        REGISTRY_OBJECTS.add(RO);
        return RO;
    }

    protected void registerCommand(CommandHolder<?> holder) {
        String id = holder.getID();

        CommandType type = global().getType(holder.getID());
        if (type != CommandType.UNKNOWN)
            throw new IllegalStateException("""
                    Tried to register already taken command %s:
                    """.formatted(id));
        COMMANDS.put(id, holder);
    }

    protected void registerAlias(CommandAlias alias) {
        String id = alias.getID();

        CommandType type = global().getType(alias.getID());
        if (type != CommandType.UNKNOWN)
            throw new IllegalStateException("""
                    Tried to register already taken command %s:
                    """.formatted(id));
        COMMAND_ALIASES.put(id, alias);
    }

    protected void registerCommands(ArrayList<RegistryObject<CommandHolder<?>>> REGISTRY_OBJECTS) {
        REGISTRY_OBJECTS.forEach(ro -> {
            registerCommand(ro.get());
        });
    }


    protected void registerAliases(ArrayList<RegistryObject<CommandAlias>> REGISTRY_OBJECTS) {
        REGISTRY_OBJECTS.forEach(ro -> {
            registerAlias(ro.get());
        });
    }

    public String getID() {
        return guildID;
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


        CommandResult result = switch (type) {
            case COMMAND -> COMMANDS.get(command).getCommand().execute(message, Arguments.of(args));
            case ALIAS -> COMMAND_ALIASES.get(command).execute(message, Arguments.of(args));
            case UNKNOWN -> null;
        };

        if (result != null)
            result.accept(message);

    }
}
