package org.mangorage.mangobot.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.mangorage.mangobot.core.audio.Music;
import org.mangorage.mangobot.core.commands.CommandManager;
import org.mangorage.mangobot.core.events.EventListener;

import java.util.EnumSet;

import static org.mangorage.mangobot.core.Constants.STARTUP_MESSAGE;

public class Bot {
    private static Bot INSTANCE = null;

    public static void init() {
        INSTANCE = new Bot();
    }

    private final JDA BOT;

    public static JDA getInstance() {
        return INSTANCE.BOT;
    }

    public Bot() {
        System.out.println(STARTUP_MESSAGE);

        CommandManager.getInstance().register(); // Register commands!
        Music.init(); // Init Music!

        JDABuilder builder = JDABuilder.createDefault(Constants.dotEnv.get("DISCORD_TOKEN"));

        builder.setActivity(Activity.of(Activity.ActivityType.PLAYING, "MinecraftForge: The Awakening of Herobrine Modpack"));
        builder.setStatus(OnlineStatus.ONLINE);

        EnumSet<GatewayIntent> intents = EnumSet.of(
                // Enables MessageReceivedEvent for guild (also known as servers)
                GatewayIntent.GUILD_MESSAGES,
                // Enables the event for private channels (also known as direct messages)
                GatewayIntent.DIRECT_MESSAGES,
                // Enables access to message.getContentRaw()
                GatewayIntent.MESSAGE_CONTENT,
                // Enables MessageReactionAddEvent for guild
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                // Enables MessageReactionAddEvent for private channels
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.SCHEDULED_EVENTS
        );

        EnumSet<CacheFlag> cacheFlags = EnumSet.of(
                CacheFlag.EMOJI
        );

        builder.setEnabledIntents(intents);
        builder.enableCache(cacheFlags);

        builder.setEventManager(new AnnotatedEventManager());
        builder.addEventListeners(new EventListener());

        this.BOT = builder.build();
    }

}
