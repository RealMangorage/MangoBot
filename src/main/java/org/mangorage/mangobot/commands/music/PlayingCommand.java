package org.mangorage.mangobot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.Music;
import org.mangorage.mangobot.core.audio.TrackScheduler;

public class PlayingCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        MessageChannelUnion channel = message.getChannel();

        if (Music.trackScheduler.getStatus() == TrackScheduler.Status.PLAYING) {
            AudioTrack track = Music.audioPlayer.getPlayingTrack();
            channel.sendMessage("""
                    Playing: %p
                    %t / %b
                    """
                    .replaceFirst("%p", track.getInfo().title)
                    .replaceFirst("%t", track.getPosition() + "")
                    .replaceFirst("%b", track.getDuration() + "")
            ).queue();
        }


        return CommandResult.PASS;
    }
}
