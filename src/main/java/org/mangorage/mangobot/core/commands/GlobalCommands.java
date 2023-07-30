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

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.AliasTestCommand;
import org.mangorage.mangobot.commands.ReplyCommand;
import org.mangorage.mangobot.commands.music.PauseCommand;
import org.mangorage.mangobot.commands.music.PlayCommand;
import org.mangorage.mangobot.commands.music.PlayingCommand;
import org.mangorage.mangobot.commands.music.QueueCommand;
import org.mangorage.mangobot.commands.music.StopCommand;
import org.mangorage.mangobot.commands.music.VolumeCommand;
import org.mangorage.mangobot.core.Constants;
import org.mangorage.mangobot.core.Util;
import org.mangorage.mangobot.core.commands.guildcommands.ForgeCommands;
import org.mangorage.mangobot.core.commands.registry.CommandAlias;
import org.mangorage.mangobot.core.commands.registry.CommandHolder;
import org.mangorage.mangobot.core.commands.registry.CommandRegistry;
import org.mangorage.mangobot.core.commands.registry.RegistryObject;
import org.mangorage.mangobot.core.commands.util.CommandResult;
import org.mangorage.mangobot.core.music.recorder.VoiceChatRecorder;
import org.mangorage.mangobot.core.music.recorder.VoiceRelay;

public class GlobalCommands {
    public static final CommandRegistry GLOBAL = CommandRegistry.global();

    public static final RegistryObject<CommandHolder<AbstractCommand>> TRICK = GLOBAL.register(() -> CommandHolder.create("trick", new AliasTestCommand("coolTrick!")));

    static {
        GLOBAL.register("speak", new ReplyCommand("I have spoken!"));

        GLOBAL.register("terminate", AbstractCommand.create((message, args) -> {
            if (message.getAuthor().getId().equals("194596094200643584")) {
                message.getChannel().sendMessage("Terminating Bot").queue();
                System.exit(0);
            } else {
                message.getChannel().sendMessage("Unable to Terminate bot. Only MangoRage can do this!").queue();
            }
            return CommandResult.PASS;
        }, false));


        GLOBAL.register("record", AbstractCommand.create(((message, arg) -> {
            String[] args = arg.getArgs();
            MessageChannelUnion channel = message.getChannel();
            if (args.length > 1) {
                String channelID = args[0];
                VoiceChannel channelC = message.getGuild().getChannelById(VoiceChannel.class, channelID);
                if (channelC != null) {
                    Integer seconds = Util.parseStringIntoInteger(args[1]);
                    if (seconds != null && seconds <= 3600 && seconds > 0)
                        VoiceChatRecorder.getInstance(message.getGuild().getId()).record(message, seconds, channelC);
                    else
                        channel.sendMessage("Max allowed time to record is 3600 seconds").queue();
                }
            } else {
                Integer seconds = Util.parseStringIntoInteger(args[0]);
                if (seconds != null && seconds <= 3600 && seconds > 0)
                    VoiceChatRecorder.getInstance(message.getGuild().getId()).record(message, seconds);
                else
                    channel.sendMessage("Max allowed time to record is 3600 seconds").queue();
            }
            return CommandResult.PASS;
        }), true));

        GLOBAL.register("sendRecording", AbstractCommand.create((message, args) -> {
            MessageChannelUnion channelUnion = message.getChannel();
            message.reply("sending").queue();
            VoiceChatRecorder.getInstance(message.getGuild().getId()).compressFile((file) -> {
                channelUnion.sendMessage("Here!").addFiles(FileUpload.fromData(file)).queue();
            }, message);


            return CommandResult.PASS;
        }, false));

        GLOBAL.register("testRelay", AbstractCommand.create(((message, args) -> {
            GuildVoiceState state = message.getMember().getVoiceState();
            if (state != null && state.inAudioChannel()) {
                VoiceChannel voiceChannel = state.getChannel().asVoiceChannel();
                VoiceRelay.getInstance(message.getGuild().getId()).join(voiceChannel);
                message.reply("Connecting").queue();
            }

            return CommandResult.PASS;
        }), true));


        if (Constants.USE_MUSIC) {
            GLOBAL.register("play", new PlayCommand());
            GLOBAL.register("stop", new StopCommand());
            GLOBAL.register("queue", new QueueCommand());
            GLOBAL.register("pause", new PauseCommand());
            GLOBAL.register("setVolume", new VolumeCommand());
            GLOBAL.register("playing", new PlayingCommand());
        }


        RegistryObject<CommandAlias> ALIAS = GLOBAL.registerAlias(() -> ForgeCommands.PING.get(), CommandAlias.of("pingGlobal"));
    }

    public static void init() {
    }

}
