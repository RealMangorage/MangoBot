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

package org.mangorage.mangobot.modules.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.modules.music.MusicPlayer;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.ICommand;

public class VolumeCommand implements ICommand {
    @Override
    public CommandResult execute(Message message, Arguments args) {
        String VOLUME = args.getOrDefault(0, "10");
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        CommandResult result = CommandResult.FAIL;

        try {
            int volume = Integer.valueOf(VOLUME);
            if (volume <= 100) {
                MusicPlayer.getInstance(guild.getId()).setVolume(volume);
                channel.sendMessage("Volume set to " + volume).queue();
            } else
                channel.sendMessage("Max Volume allowed is 30").queue();

            result = CommandResult.PASS;
        } catch (NumberFormatException e) {
            channel.sendMessage("Invalid arguments.").queue();
        }

        return result;
    }

    /**
     * @return
     */
    @Override
    public String commandId() {
        return "setVolume";
    }


}
