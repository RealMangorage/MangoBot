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

import org.mangorage.mangobot.commands.core.AbstractCommand;

import java.util.List;
import java.util.function.Supplier;

public class CommandHolder<T extends AbstractCommand> implements Supplier<T> {

    public static <T extends AbstractCommand> CommandHolder<T> create(T value, List<CommandAlias> aliases) {
        return new CommandHolder<>(value, aliases);
    }

    private final T value;
    private final List<CommandAlias> aliases;

    private CommandHolder(T value, List<CommandAlias> aliases) {
        this.value = value;
        this.aliases = aliases;
    }

    public T get() {
        return value;
    }

    public List<CommandAlias> getAliases() {
        return aliases;
    }

}
