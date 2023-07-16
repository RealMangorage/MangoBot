package org.mangorage.mangobot.core;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.commands.ReplyCommand;
import org.mangorage.mangobot.commands.music.PlayCommand;
import org.mangorage.mangobot.commands.music.StopCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandManager {
    private static final CommandManager INSTANCE = new CommandManager();

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    private final HashMap<String, AbstractCommand> COMMANDS = new HashMap<>();

    public void register() {
        COMMANDS.put("speak", new ReplyCommand("I have spoken!"));
        COMMANDS.put("paste", new ReplyCommand(
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
        COMMANDS.put("sl", new ReplyCommand(
                """
                        # Forge Only Supports 1.19.x and 1.20.x
                        The Forge Discord only supports 1.19.x and 1.20.x
                        The Forge Project including everyone in this discord, and the forums, only supports the latest and LTS versions. Currently 1.19.x and 1.20.x.
                        This is due to the Forge team having limited manpower as we are all people working in our free time. We can't possibly support all Minecraft versions that had a Forge build.
                        Some say a version of Minecraft is the best but others say that about a different version.
                        All of the older versions work and there are some other communities that offer support for older versions.
                        """
        ));
        COMMANDS.put("neoforge", new ReplyCommand(
                """
You mentioned NeoForge. NeoForge is a fork of MinecraftForge by many former MC Forge staff and the old discord. We are not affiliated with NeoForge and to get support with NeoForge you should consider joining their discord if you are not already banned. https://discord.neoforged.net/
                        """
        ));
        COMMANDS.put("notforge", new ReplyCommand(
                """
                        We only support Minecraft Forge on this discord server, the attached info uses other modloaders. We do not support other modloaders such as Fabric, Quilt, FeatureCreep, LiteLoader, Rift, NeoForge, or any other modloaders mods out of the box. You should contact the developers or the modloader or abstraction layer of your issue.
                        """
        ));
        if (Constants.USE_MUSIC) {
            COMMANDS.put("play", new PlayCommand());
            COMMANDS.put("stop", new StopCommand());
        }
    }

    public String[] handleCommand(String command, String content) {
        String params = content.replaceFirst("!" + command, " ").trim();
        return params.split(" ");
    }

    public void handleCommand(String command, Message message) {
        CommandResult result = COMMANDS.get(command).execute(message, handleCommand(command, message.getContentDisplay()));
        if (result == CommandResult.FAIL)
            message.getChannel().sendMessage("Command failed");
    }

    public boolean isValid(String command) {
        return COMMANDS.containsKey(command);
    }
}
