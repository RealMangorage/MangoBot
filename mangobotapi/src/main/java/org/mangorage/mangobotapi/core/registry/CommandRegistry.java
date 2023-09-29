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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.mangorage.mangobotapi.MangoBotAPI;
import org.mangorage.mangobotapi.core.commands.AbstractCommand;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandAlias;
import org.mangorage.mangobotapi.core.commands.CommandHolder;
import org.mangorage.mangobotapi.core.commands.CommandPrefix;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.eventbus.EventBus;
import org.mangorage.mangobotapi.core.events.CommandEvent;
import org.mangorage.mangobotapi.core.events.RegistryEvent;
import org.mangorage.mangobotapi.core.events.StartupEvent;
import org.mangorage.mangobotapi.core.util.MessageSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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

    private static final CommandRegistry GLOBAL = new CommandRegistry();
    private static final HashMap<String, CommandRegistry> GUILDS = new HashMap<>();
    private static final MessageSettings DEFAULT_SETTINGS = MangoBotAPI.getInstance().getDefaultMessageSettings();

    public static CommandRegistry guild(String guildID) {
        Objects.requireNonNull(guildID);
        return GUILDS.computeIfAbsent(guildID, CommandRegistry::new);
    }

    public static CommandRegistry global() {
        return GLOBAL;
    }

    public static void handleMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // Handle Message and prefix
        String Prefix = event.isFromGuild() ? CommandPrefix.getPrefix(event.getGuild().getId()) : CommandPrefix.DEFAULT;

        Message message = event.getMessage();
        String rawMessage = message.getContentRaw();
        if (rawMessage.startsWith(Prefix)) {
            String[] command_pre = rawMessage.split(" ");
            String command = command_pre[0].replaceFirst(Prefix, "");
            Arguments arguments = Arguments.of(Arguments.of(command_pre).getFrom(1).split(" "));

            var commandEvent = new CommandEvent(event.getMessage(), command, arguments);
            MangoBotAPI.getInstance().getEventBus().post(commandEvent);
            if (commandEvent.isHandled()) {
                commandEvent.getCommandResult().accept(message);
            } else {
                DEFAULT_SETTINGS.apply(message.reply("Invalid Command")).queue();
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

    private CommandRegistry() {
        this(null);
    }

    public <X extends AbstractCommand> RegistryObject<CommandHolder<X>> register(String id, X command, CommandAlias.Builder... builders) {
        if (frozen.get())
            throw new IllegalStateException("Cannot register stuff to a frozen registry");
        RegistryObject<CommandHolder<X>> RO = new RegistryObject<>(CommandHolder.create(id, command), CommandType.COMMAND);
        REGISTRY_OBJECTS.add(RO);
        Arrays.asList(builders).forEach(e -> registerAlias(RO.get(), e));
        return RO;
    }

    public void registryEvent(RegistryEvent event) {
        REGISTRY_OBJECTS.forEach(registryObject -> {
            if (registryObject.getType() == CommandType.COMMAND && registryObject.getType() == event.type()) {
                CommandHolder<?> holder = (CommandHolder<?>) registryObject.get();
                COMMANDS.put(holder.getID(), holder);
            } else if (registryObject.getType() == CommandType.ALIAS && registryObject.getType() == event.type()) {
                CommandAlias alias = (CommandAlias) registryObject.get();
                COMMAND_ALIASES.put(alias.getID(), alias);
            }
        });
    }

    public void startupEvent(StartupEvent event) {
        if (event.phase() == StartupEvent.Phase.FINISHED) {
            System.out.println("Clearing Registry Objects and Freezing Registry for id: %s".formatted(guildID));
            REGISTRY_OBJECTS.clear();
            frozen.set(true);
        }
    }

    public void commandEvent(CommandEvent event) {
        if (!event.isHandled()) {
            CommandType type = getType(event.getCommand());
            if (type == CommandType.COMMAND && COMMANDS.containsKey(event.getCommand())) {
                event.setHandled(COMMANDS.get(event.getCommand()).getCommand().execute(event.getMessage(), event.getArguments()));
            } else if (type == CommandType.ALIAS && COMMAND_ALIASES.containsKey(event.getCommand())) {
                event.setHandled(COMMAND_ALIASES.get(event.getCommand()).execute(event.getMessage(), event.getArguments()));
            }
        }
    }

    public void register(EventBus bus) {
        bus.addListener(StartupEvent.class, this::startupEvent);
        bus.addListener(RegistryEvent.class, this::registryEvent);
        bus.addListener(CommandEvent.class, this::commandEvent);
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

        if (guildID == null || (commandHolder.getCommand().isGuildOnly() && !message.isFromGuild()))
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
