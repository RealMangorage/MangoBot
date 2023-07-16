package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

public class ReplyEmbedCommand extends AbstractEmbedCommand {
    @Override
    public CommandResult execute(Message message, String... args) {
        Collection<MessageEmbed> C = new ArrayList<>();
        C.add(
                new EmbedBuilder()
                        .setTitle("Title")
                        .setAuthor("MangoRage")
                        .setImage("https://preview.redd.it/rcpgibdaqkl61.png?width=1920&format=png&auto=webp&s=c5b0c957843c37c6a7b6cc734c3934465c955415")
                        .setFooter("Footer")
                        .setColor(Color.WHITE)
                        .setDescription("Description")
                        .setThumbnail("https://oyster.ignimgs.com/mediawiki/apis.ign.com/minecraft/b/b4/Herobrine.png")
                        .appendDescription("Additional Description")
                        .addField("test", "value", false)
                        .addField("test", "value inline", true)
                        .setTimestamp(Time.from(Instant.now()).toInstant())
                        .build());
        message.replyEmbeds(C).queue();
        return CommandResult.PASS;
    }
}
