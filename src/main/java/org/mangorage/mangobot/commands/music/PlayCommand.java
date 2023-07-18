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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.MusicPlayer;
import org.mangorage.mangobot.core.audio.MusicUtil;

public class PlayCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        String URL = args[0];
        MessageChannelUnion channel = message.getChannel();
        MusicPlayer player = MusicPlayer.getInstance();

        if (URL.length() > 0) {
            if (!player.isPlaying()) {
                player.load(URL, e -> {
                    switch (e.getReason()) {
                        case SUCCESS -> {
                            MusicUtil.connectToAudioChannel(message.getMember());
                            player.add(e.getTrack());
                            player.play();
                            channel.sendMessage("Started playing: " + e.getTrack().getInfo().title).queue();
                        }
                        case FAILED -> {
                            channel.sendMessage("Failed").queue();
                        }
                        case NO_MATCHES -> {
                            channel.sendMessage("No matches was found!").queue();
                        }
                    }
                });
            } else
                channel.sendMessage("Already playing!").queue();
        } else {
            if (player.isPlaying()) {
                MusicPlayer.getInstance().resume();
                channel.sendMessage("Resumed playing: " + MusicPlayer.getInstance().getPlaying().getInfo().title).queue();
            } else {
                channel.sendMessage("Nothing is currently playing.").queue();
            }
        }

        return CommandResult.PASS;
    }
}
