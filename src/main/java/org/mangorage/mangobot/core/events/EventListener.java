package org.mangorage.mangobot.core.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.mangorage.mangobot.core.Util;


public class EventListener {

    @SubscribeEvent
    public void messageRecieved(MessageReceivedEvent event) {
        Util.handleMessage(event.getMessage());
    }
}
