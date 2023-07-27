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
import org.mangorage.mangobot.commands.core.CommandResult;
import org.mangorage.mangobot.core.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

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

    private static void check(List<RegistryBuilder> builders, List<RegistryBuilder.CommandType> commandTypes, List<RegistryBuilder.CommandAliasType> aliasTypes) {
        builders.forEach(registryBuilder -> {
            if (registryBuilder instanceof RegistryBuilder.CommandType<?> commandType) {
                commandTypes.add(commandType);
            } else if (registryBuilder instanceof RegistryBuilder.CommandAliasType<?> aliasType) {
                aliasTypes.add(aliasType);
            }
        });
    }

    public static void build() {
        HashMap<CommandRegistry, ArrayList<RegistryBuilder.CommandType>> COMMANDS_TYPES = new HashMap<>();
        HashMap<CommandRegistry, ArrayList<RegistryBuilder.CommandAliasType>> COMMAND_ALIASES_TYPES = new HashMap<>();

        COMMANDS_TYPES.computeIfAbsent(global(), (key) -> new ArrayList<>());
        COMMAND_ALIASES_TYPES.computeIfAbsent(global(), (key) -> new ArrayList<>());

        check(GLOBAL.REGISTRY_OBJECTS, COMMANDS_TYPES.get(global()), COMMAND_ALIASES_TYPES.get(global()));


        GUILDS.forEach((key, guild) -> {
            COMMANDS_TYPES.computeIfAbsent(guild, (keyb) -> new ArrayList<>());
            COMMAND_ALIASES_TYPES.computeIfAbsent(guild, (keyb) -> new ArrayList<>());
            check(guild.REGISTRY_OBJECTS, COMMANDS_TYPES.get(guild), COMMAND_ALIASES_TYPES.get(guild));
        });

        COMMANDS_TYPES.forEach((key, list) -> {
            list.forEach(e -> e.build(key));
        });

        COMMAND_ALIASES_TYPES.forEach((key, list) -> {
            list.forEach(e -> e.build(key));
        });
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
    private final List<RegistryBuilder> REGISTRY_OBJECTS = new ArrayList<>();
    private HashMap<String, CommandHolder<?>> COMMANDS = new HashMap<>();
    private HashMap<String, CommandAlias> COMMAND_ALIASES = new HashMap<>();

    private CommandRegistry(String guildID) {
        this.guildID = guildID;
    }

    public <T extends AbstractCommand> RegistryObject<CommandHolder<T>> registerOld(String ID, T command) {
        return register(() -> CommandHolder.create(ID, command));
    }

    public <T extends AbstractCommand> RegistryObject<CommandHolder<T>> registerOld(String ID, T command, CommandAlias.Builder... aliases) {
        return register(() -> CommandHolder.create(ID, command), aliases);
    }


    private <T extends AbstractCommand> CommandHolder<T> register(CommandHolder<T> holder, List<CommandAlias.Builder> aliases) {
        String commandID = holder.getID();

        if (guildID == null && GLOBAL.getType(commandID) != CommandType.UNKNOWN)
            throw new IllegalStateException("Tried to register a command that has already been taken by the global registry: %s".formatted(commandID));

        if (COMMANDS.containsKey(commandID))
            throw new IllegalStateException("Tried to register a command using an ID that's already been used CommandID: %s".formatted(commandID));

        COMMANDS.put(commandID, holder);
        aliases.forEach(e -> register(holder, e));

        return holder;
    }


    private <T extends AbstractCommand> CommandAlias register(CommandHolder<T> holder, CommandAlias.Builder builder) {
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

    public <T extends AbstractCommand> RegistryObject<CommandHolder<T>> register(Supplier<CommandHolder<T>> supplierObject, CommandAlias.Builder... aliases) {
        RegistryObject<CommandHolder<T>> registryObject = new RegistryObject<>();

        RegistryBuilder registryBuilder = new RegistryBuilder.CommandType<>(registryObject, supplierObject, Arrays.asList(aliases));
        REGISTRY_OBJECTS.add(registryBuilder);
        return registryObject;
    }

    public <T extends AbstractCommand> RegistryObject<CommandAlias> registerAlias(Supplier<CommandHolder<T>> supplierObject, CommandAlias.Builder alias) {
        RegistryObject<CommandAlias> registryObject = new RegistryObject<>();

        RegistryBuilder registryBuilder = new RegistryBuilder.CommandAliasType<>(registryObject, supplierObject, alias);
        REGISTRY_OBJECTS.add(registryBuilder);

        return registryObject;
    }


    public void example() {
        register(
                () -> CommandHolder.create("commandID", AbstractCommand.create(
                        (message, args) -> {
                            return CommandResult.PASS;
                        }, false)),
                CommandAlias.of(""));

        registerAlias(null, CommandAlias.of("test"));
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

    public abstract static class RegistryBuilder {

        public static class CommandType<T extends AbstractCommand> extends RegistryBuilder {
            private final RegistryObject<CommandHolder<T>> RO;
            private final Supplier<CommandHolder<T>> HOLDER_SUPPLIER;
            private final List<CommandAlias.Builder> ALIASES;

            public CommandType(RegistryObject<CommandHolder<T>> registryObject, Supplier<CommandHolder<T>> commandHolderSupplier, List<CommandAlias.Builder> aliases) {
                this.RO = registryObject;
                this.HOLDER_SUPPLIER = commandHolderSupplier;
                this.ALIASES = aliases;
            }

            /**
             * @param registry
             */
            @Override
            public void build(CommandRegistry registry) {
                RO.set(registry.register(HOLDER_SUPPLIER.get(), ALIASES));
            }
        }

        public static class CommandAliasType<T extends AbstractCommand> extends RegistryBuilder {
            private final RegistryObject<CommandAlias> RO;
            private final Supplier<CommandHolder<T>> HOLDER_SUPPLIER;
            private final CommandAlias.Builder ALIAS_BUILDER;

            public <X extends AbstractCommand> CommandAliasType(RegistryObject<CommandAlias> registryObject, Supplier<CommandHolder<T>> commandHolderSupplier, CommandAlias.Builder builder) {
                this.RO = registryObject;
                this.HOLDER_SUPPLIER = commandHolderSupplier;
                this.ALIAS_BUILDER = builder;
            }

            /**
             * @param registry
             */
            @Override
            public void build(CommandRegistry registry) {
                RO.set(registry.register(HOLDER_SUPPLIER.get(), ALIAS_BUILDER));
            }
        }


        abstract public void build(CommandRegistry registry);
    }
}
