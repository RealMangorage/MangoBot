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

package org.mangorage.mangobot.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.mangorage.mangobot.core.commands.GlobalCommands;
import org.mangorage.mangobot.core.commands.guildcommands.ForgeCommands;
import org.mangorage.mangobot.core.events.EventListener;
import org.mangorage.mangobot.core.settings.Settings;

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

        GlobalCommands.init();
        ForgeCommands.init();

        JDABuilder builder = JDABuilder.createDefault(Settings.BOT_TOKEN.get());

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
        builder.setEnableShutdownHook(true);

        this.BOT = builder.build();
    }

    public static void close() {
        if (INSTANCE != null) {
            getInstance().getEventManager().getRegisteredListeners().forEach(e -> getInstance().removeEventListener(e));
            getInstance().shutdown();
            System.out.println("Terminating Bot! Closing Program!");
        }
    }

}
