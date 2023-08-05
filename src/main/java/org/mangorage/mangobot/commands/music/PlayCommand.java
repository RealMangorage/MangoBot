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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.core.music.MusicPlayer;
import org.mangorage.mangobot.core.music.MusicUtil;
import org.mangorage.mangobot.core.permissions.GlobalPermissions;
import org.mangorage.mangobotapi.core.AbstractCommand;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;
import org.mangorage.mangobotapi.core.util.Arguments;
import org.mangorage.mangobotapi.core.util.CommandResult;

public class PlayCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, Arguments arg) {
        String[] args = arg.getArgs();

        String URL = args[0];
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        MusicPlayer player = MusicPlayer.getInstance(guild.getId());
        GuildVoiceState voiceState = message.getMember().getVoiceState();
        Member member = message.getMember();

        if (member != null && !PermissionRegistry.hasNeededPermission(member, GlobalPermissions.PLAYING)) {
            return CommandResult.NO_PERMISSION;
        }

        if (voiceState.inAudioChannel()) {
            if (URL.length() > 0) {
                if (!player.isPlaying()) {
                    System.out.println(String.join(" ", args));

                    player.load(String.join(" ", args), e -> {
                        switch (e.getReason()) {
                            case SUCCESS -> {
                                MusicUtil.connectToAudioChannel(voiceState.getChannel().asVoiceChannel());
                                player.add(e.getTrack());
                                player.play();
                                MessageEmbed embed = new EmbedBuilder()
                                        .setTitle(e.getTrack().getInfo().title, e.getTrack().getInfo().uri)
                                        .build();
                                channel.sendMessage("Playing: ").addEmbeds(embed).queue();
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
                    player.resume();
                    AudioTrack track = player.getPlaying();
                    MessageEmbed embed = new EmbedBuilder()
                            .setTitle(track.getInfo().title, track.getInfo().uri)
                            .build();
                    channel.sendMessage("Resumed playing: ").addEmbeds(embed).queue();
                } else {
                    if (!player.isQueueEmpty()) {
                        MusicUtil.connectToAudioChannel(voiceState.getChannel().asVoiceChannel());
                        player.play();
                        AudioTrack track = player.getPlaying();
                        MessageEmbed embed = new EmbedBuilder()
                                .setTitle(track.getInfo().title, track.getInfo().uri)
                                .build();
                        channel.sendMessage("Started playing: ").addEmbeds(embed).queue();
                    } else
                        channel.sendMessage("Nothing is currently playing.").queue();
                }
            }
        } else
            channel.sendMessage("Must be in a voice channel!").queue();

        return CommandResult.PASS;
    }
}
