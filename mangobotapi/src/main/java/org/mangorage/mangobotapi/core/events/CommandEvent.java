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

package org.mangorage.mangobotapi.core.events;

import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mboteventbus.base.Event;

public abstract class CommandEvent<Type> extends Event<Type> {
    private final String command;
    private final Arguments arguments;

    private boolean handled = false;
    private CommandResult result;
    private Exception exception;

    public CommandEvent(String command, Arguments arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public boolean isHandled() {
        return handled;
    }

    public CommandResult getCommandResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }

    public void setHandled(CommandResult result) {
        if (handled) return;
        this.handled = true;
        this.result = result;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }


    public String getCommand() {
        return command;
    }

    public Arguments getArguments() {
        return arguments;
    }
}
