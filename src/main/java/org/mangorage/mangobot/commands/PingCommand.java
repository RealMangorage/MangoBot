package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

public class PingCommand extends AbstractEmbedCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        message.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Please disable pings when replying to others")
                        .setImage("https://images-ext-2.discordapp.net/external/nUYuix4co3xyLw0ZFtBh5r2uxogGjmgr-4OTPW4cl8I/https/i.imgur.com/7YCb3AN.png")
                        .setColor(Color.WHITE)
                        .setDescription("""
                                So you don't accidentally break a rule or annoy others, make sure to turn off pings when replying to others. Discord currently turns this on every time you start a reply, so please be vigilant. Thank you.
                                
                                Go vote on [Discord Feedback](https://support.discord.com/hc/en-us/community/posts/360052518273-Replies-remember-setting) so they make changes.
                                """)
                        .build()).queue();

        return CommandResult.PASS;
    }
}
