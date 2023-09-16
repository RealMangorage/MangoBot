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

package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.core.music.MusicPlayer;
import org.mangorage.mangobotapi.core.AbstractCommand;
import org.mangorage.mangobotapi.core.registry.commands.Arguments;
import org.mangorage.mangobotapi.core.registry.commands.CommandResult;

public class VolumeCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, Arguments args) {
        String VOLUME = args.getOrDefault(0, "10");
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        CommandResult result = CommandResult.FAIL;

        try {
            int volume = Integer.valueOf(VOLUME);
            if (volume <= 30) {
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
}
