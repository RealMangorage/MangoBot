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

package org.mangorage.mangobotapi.core.registry.commands;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobotapi.core.AbstractCommand;
import org.mangorage.mangobotapi.core.util.Arguments;
import org.mangorage.mangobotapi.core.util.CommandResult;

import java.util.function.Supplier;


public class CommandAlias {
    public static CommandAlias.Builder of(@NotNull String Alias, @NotNull ICommandAlias aliasHandler) {
        return new CommandAlias.Builder(Alias, aliasHandler);
    }

    public static CommandAlias.Builder of(@NotNull String Alias) {
        return of(Alias, AbstractCommand::execute);
    }

    public static CommandAlias build(String Alias, CommandHolder<?> holder) {
        return new CommandAlias(Alias, () -> holder, AbstractCommand::execute);
    }

    private final String ID;
    private final Supplier<CommandHolder<?>> originalCommandHolderSupplier;
    private final ICommandAlias alias;

    private CommandAlias(String ID, Supplier<CommandHolder<?>> originalCommandHolderSupplier, ICommandAlias alias) {
        this.ID = ID;
        this.originalCommandHolderSupplier = originalCommandHolderSupplier;
        this.alias = alias;
    }

    public String getID() {
        return ID;
    }

    public CommandHolder<? extends AbstractCommand> getCommandHolder() {
        return originalCommandHolderSupplier.get();
    }

    public CommandResult execute(Message message, Arguments args) {
        return alias.execute(originalCommandHolderSupplier.get().getCommand(), message, args);
    }

    @FunctionalInterface
    public interface ICommandAlias {
        CommandResult execute(AbstractCommand command, Message message, Arguments args);
    }

    public static class Builder {
        private final String AlliasID;
        private final ICommandAlias alliasHandler;

        private Builder(String ID, ICommandAlias alliasHandler) {
            this.AlliasID = ID;
            this.alliasHandler = alliasHandler;
        }

        public CommandAlias build(CommandHolder<?> commandHolder) {
            return new CommandAlias(AlliasID, () -> commandHolder, alliasHandler);
        }

    }
}
