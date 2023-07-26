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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import org.mangorage.mangobot.commands.AliasTestCommand;
import org.mangorage.mangobot.commands.PingCommand;
import org.mangorage.mangobot.commands.ReplyCommand;
import org.mangorage.mangobot.commands.core.AbstractCommand;
import org.mangorage.mangobot.commands.core.CommandResult;
import org.mangorage.mangobot.commands.music.PauseCommand;
import org.mangorage.mangobot.commands.music.PlayCommand;
import org.mangorage.mangobot.commands.music.PlayingCommand;
import org.mangorage.mangobot.commands.music.QueueCommand;
import org.mangorage.mangobot.commands.music.StopCommand;
import org.mangorage.mangobot.commands.music.VolumeCommand;
import org.mangorage.mangobot.core.Constants;
import org.mangorage.mangobot.core.Util;
import org.mangorage.mangobot.core.music.recorder.VoiceChatRecorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * TODO: Overhaul the CommandManager to create a registration system
 * that allows us to have command alias's on a global and per server basis.
 * <p>
 * CommandHolder<AbstractCommand> PING =
 * register(
 * "ping",
 * new ReplyCommand("ping!"),
 * CommandAliases.of("pingEagle", (CommandHolder, Message, Args) -> {
 * // Command Holder is PING
 * CommandHolder.execute(Message, Args)
 * })
 * );
 */

@Deprecated(forRemoval = true, since = "7/26/23")
public class CommandManager {
    private static final CommandManager INSTANCE = new CommandManager();

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    private final HashMap<String, CommandHolder<?>> COMMANDS = new HashMap<>();
    private final HashMap<String, CommandAlias> COMMANDS_ALIASES = new HashMap<>();


    public void register() {
        CommandHolder<AbstractCommand> TRICK = register(
                "trick",
                new AliasTestCommand("coolTrick!"),
                CommandAlias.of(
                        "tickCool",
                        (command, message, args) -> command.execute(message, new String[]{"cool!"})
                )
        );


        register("speak", new ReplyCommand("I have spoken!"));
        register("paste", new ReplyCommand(
                """
                        Please use a paste site for large blocks of code/logs, instead of dumping it in chat or taking a screenshot.
                                        
                        Here's a list of some paste sites and their size limits:
                            https://gist.github.com/:          [Free] [SignUp] 100MB
                            https://paste.gemwire.uk/:      [Free] 10MB
                            https://paste.ee/:                       [Free] 1MB, [SignUp] 6MB
                            https://pastebin.com/:             [Free] 512KB, [SignUp] [Paid] 10MB
                            https://hastebin.com/:             [Free] 400KB
                            https://paste.centos.org/:       [Free] 1023KB
                            https://mclo.gs/:                        [Free] 15MB
                        """
        ));
        register("sl", new ReplyCommand(
                """
                        # Forge Only Supports 1.19.x and 1.20.x
                        The Forge Discord only supports 1.19.x and 1.20.x
                        The Forge Project including everyone in this discord, and the forums, only supports the latest and LTS versions. Currently 1.19.x and 1.20.x.
                        This is due to the Forge team having limited manpower as we are all people working in our free time. We can't possibly support all Minecraft versions that had a Forge build.
                        Some say a version of Minecraft is the best but others say that about a different version.
                        All of the older versions work and there are some other communities that offer support for older versions.
                        """
        ));
        register("neoforge", new ReplyCommand(
                """
                        You mentioned NeoForge. NeoForge is a fork of MinecraftForge by many former MC Forge staff and the old discord. We are not affiliated with NeoForge and to get support with NeoForge you should consider joining their discord if you are not already banned. https://discord.neoforged.net/
                                                """
        ));
        register("notforge", new ReplyCommand(
                """
                        We only support Minecraft Forge on this discord server, the attached info uses other modloaders. We do not support other modloaders such as Fabric, Quilt, FeatureCreep, LiteLoader, Rift, NeoForge, or any other modloaders mods out of the box. You should contact the developers or the modloader or abstraction layer of your issue.
                        """
        ));
        register("java", new ReplyCommand(
                """
                        You can download Java from the Adoptium project: https://adoptium.net/temurin/releases/
                        Select the Version dropdown option for the Java version you wish to download.
                        1.18 and later need Java 17
                        1.17 needs Java 16
                        1.16.5 and older need Java 8
                        """
        ));
        register("log", new ReplyCommand(
                """
                        To diagnose your issue we need the game log; please provide the logs/debug.log file, in the minecraft directory, and put it in one of the following sites (preferably the first one):
                                                
                        Here's a list of some paste sites and their size limits:
                            https://gist.github.com/:      [Free] [SignUp] 100MB
                            https://paste.gemwire.uk/:  [Free] 10MB
                            https://paste.ee/:                   [Free] 1MB, [SignUp] 6MB
                            https://pastebin.com/:          [Free] 512KB, [SignUp] [Paid] 10MB
                            https://hastebin.com/:          [Free] 400KB
                            https://gist.github.com/:      [Free] [SignUp] 100MB
                        """
        ));
        register("tryitandsee", new ReplyCommand("https://tryitands.ee", false),
                CommandAlias.of("test")
        );

        register("terminate", AbstractCommand.create((message, args) -> {
            if (message.getAuthor().getId().equals("194596094200643584")) {
                message.getChannel().sendMessage("Terminating Bot").queue();
                System.exit(0);
            } else {
                message.getChannel().sendMessage("Unable to Terminate bot. Only MangoRage can do this!").queue();
            }
            return CommandResult.PASS;
        }, false));
        register("pings", new PingCommand());

        register("record", AbstractCommand.create(((message, args) -> {
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

        register("sendRecording", AbstractCommand.create((message, args) -> {
            MessageChannelUnion channelUnion = message.getChannel();
            message.reply("sending").queue();
            VoiceChatRecorder.getInstance(message.getGuild().getId()).compressFile((file) -> {
                channelUnion.sendMessage("Here!").addFiles(FileUpload.fromData(file)).queue();
            }, message);


            return CommandResult.PASS;
        }, false));

        /**
         register("startTranscripting", AbstractCommand.create(((message, args) -> {
         MessageChannelUnion channel = message.getChannel();
         if (args.length > 1) {
         String channelID = args[0];
         VoiceChannel channelC = message.getGuild().getChannelById(VoiceChannel.class, channelID);
         if (channelC != null) {
         VoiceChatTranscripter.getInstance(message.getGuild().getId()).start(message, channelC);
         }
         } else {
         GuildVoiceState state = message.getMember().getVoiceState();
         if (state != null && state.inAudioChannel()) {
         VoiceChatTranscripter.getInstance(message.getGuild().getId()).start(message, state.getChannel().asVoiceChannel());
         }
         }
         return CommandResult.PASS;
         }), true));
         **/

        if (Constants.USE_MUSIC) {
            register("play", new PlayCommand());
            register("stop", new StopCommand());
            register("queue", new QueueCommand());
            register("pause", new PauseCommand());
            register("setVolume", new VolumeCommand());
            register("playing", new PlayingCommand());
        }

        registerAliases();
    }


    private void registerAliases() {
        COMMANDS.forEach((commandID, commandHolder) -> {
            commandHolder.getAliases().forEach(commandAlias -> {
                if (COMMANDS_ALIASES.containsKey(commandAlias.getID()))
                    throw new IllegalStateException("""
                                
                                Tried to register a command alias using an ID that's already been used AliasID
                                CommandID who had it First -> %s
                                CommandID who tried to use it -> %s
                            """
                            .formatted(COMMANDS_ALIASES.get(commandAlias.getID()).getCommandHolder().getID(), commandID)
                    );

                COMMANDS_ALIASES.put(commandAlias.getID(), commandAlias);
            });
        });
    }


    private <T extends AbstractCommand> CommandHolder<T> register(String commandID, T command, CommandAlias.Builder... aliases) {
        ArrayList<CommandAlias> ALIASES = new ArrayList<>();

        CommandHolder<T> HOLDER = CommandHolder.create(commandID, command, ALIASES);

        Arrays.stream(aliases).toList().forEach(e -> ALIASES.add(e.build(HOLDER)));

        if (COMMANDS.containsKey(commandID))
            throw new IllegalStateException("Tried to register a command using an ID that's already been used CommandID: %s".formatted(commandID));

        COMMANDS.put(commandID, HOLDER);

        return HOLDER;
    }

    public String[] handleCommandArgs(String command, String content) {
        String params = content.replaceFirst("!" + command, " ").trim();
        return params.split(" ");
    }

    public void handleCommand(String command, Message message) {
        AbstractCommand commandExecutable = COMMANDS.get(command).getCommand();
        if (!message.isFromGuild() && commandExecutable.isGuildOnly())
            return;

        CommandResult result = commandExecutable.execute(message, handleCommandArgs(command, message.getContentDisplay()));
        if (result == CommandResult.FAIL)
            message.getChannel().sendMessage("Command failed");

    }

    public void handleCommandAlias(String command, Message message) {
        CommandAlias commandAliasExecutable = COMMANDS_ALIASES.get(command);
        AbstractCommand commandExecutable = commandAliasExecutable.getCommandHolder().getCommand();
        if (!message.isFromGuild() && commandExecutable.isGuildOnly())
            return;

        CommandResult result = commandAliasExecutable.execute(message, handleCommandArgs(command, message.getContentDisplay()));
        if (result == CommandResult.FAIL)
            message.getChannel().sendMessage("Command failed");

    }

    public boolean isValidCommand(String command) {
        return COMMANDS.containsKey(command);
    }

    public boolean isValidCommandAlias(String alias) {
        return COMMANDS_ALIASES.containsKey(alias);
    }
}
