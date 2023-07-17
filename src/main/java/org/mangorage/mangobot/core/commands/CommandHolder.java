package org.mangorage.mangobot.core.commands;

import org.mangorage.mangobot.commands.AbstractCommand;

public class CommandHolder<T extends AbstractCommand> {

    public static <T extends AbstractCommand> CommandHolder<T> create(T value) {
        return new CommandHolder<>(value);
    }

    private final T value;
    private CommandHolder(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

}
