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

import org.mangorage.mangobotapi.core.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandHolder<T extends AbstractCommand> {

    public static <T extends AbstractCommand> CommandHolder<T> create(String id, T value) {
        return new CommandHolder<>(id, value);
    }

    private final String id;
    private final T value;
    private final List<CommandAlias> aliases = new ArrayList<>();

    private CommandHolder(String id, T value) {
        this.id = id;
        this.value = value;
    }

    public String getID() {
        return id;
    }

    public T getCommand() {
        return value;
    }

    public List<CommandAlias> getAliases() {
        return aliases;
    }

    public void addAlias(CommandAlias alias) {
        this.aliases.add(alias);
    }

}
