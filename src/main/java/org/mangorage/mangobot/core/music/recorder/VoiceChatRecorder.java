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

package org.mangorage.mangobot.core.music.recorder;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;
import org.mangorage.Main;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.Util;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.function.Consumer;

public class VoiceChatRecorder implements AudioReceiveHandler {
    private static final HashMap<String, VoiceChatRecorder> RECORDERS = new HashMap<>();
    private static final String STARTED_RECORDING = "Started Recording for %s seconds.";
    private static final String STARTED_RECORDING_LEFT = "Started Recording for %s seconds. %s seconds left.";
    private static final String FILE_RAW_OUTPUT = "botresources/guildata/%s/recordingdata/recording_raw.wav";
    private static final String FILE_COMPRESSED_OUTPUT = "botresources/guildata/%s/recordingdata/recording_compressed.mp3";
    private static final String FILE_FOLDER_OUTPUT = "botresources/guildata/%s/recordingdata";

    public static VoiceChatRecorder getInstance(String guildID) {
        VoiceChatRecorder recorder = RECORDERS.getOrDefault(guildID, new VoiceChatRecorder(guildID));
        if (!RECORDERS.containsKey(guildID))
            RECORDERS.put(guildID, recorder);
        return recorder;
    }

    private final ByteBuffer byteBuffer = ByteBuffer.allocate((3_840_000 * 6) * 60);
    private final String id;
    private boolean recording = false;
    private Message message;
    private Message botMessage;
    private int timeLeft;
    private int timeElapsed = 0;
    private int seconds;

    public VoiceChatRecorder(String guildID) {
        this.id = guildID;

        // Create the output folder for this guild
        File file = new File(FILE_FOLDER_OUTPUT.formatted(id));
        if (!file.exists())
            file.mkdirs();
    }

    public void record(Message message, int seconds) {
        GuildVoiceState state = message.getMember().getVoiceState();
        if (state != null && state.inAudioChannel()) {
            if (recording) {
                message.getChannel().sendMessage("Already recording!").queue();
            } else {
                message.reply(STARTED_RECORDING.formatted(seconds)).submit().thenAccept((botMessage) -> this.botMessage = botMessage);
                startRecording(seconds, message, state.getChannel().asVoiceChannel());
            }
        } else {
            message.getChannel().sendMessage("Must be in voice chat or use ChannelID in the command as an argument, !record ID seconds").queue();
        }
    }

    public void record(Message message, int seconds, VoiceChannel channel) {
        if (channel != null) {
            if (recording) {
                message.getChannel().sendMessage("Already recording!").submit().thenAccept((botMessage) -> this.botMessage = botMessage);
            } else {
                message.reply(STARTED_RECORDING.formatted(seconds)).queue();
                startRecording(seconds, message, channel);
            }
        }
    }

    private void startRecording(int seconds, Message message, VoiceChannel channel) {
        this.message = message;
        this.recording = true;
        this.timeLeft = seconds * 1000;
        this.seconds = seconds;
        this.timeElapsed = 0;

        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();

        audioManager.setReceivingHandler(this);

        audioManager.setSelfDeafened(true);
        audioManager.setSelfMuted(false);
        audioManager.setAutoReconnect(true);
        audioManager.setSpeakingMode(SpeakingMode.SOUNDSHARE);
        audioManager.setConnectTimeout(30_000);

        audioManager.openAudioConnection(channel);
    }

    private void stopRecording() {
        Guild guild = Bot.getInstance().getGuildById(id);
        guild.getAudioManager().setReceivingHandler(null);
        guild.getAudioManager().closeAudioConnection();

        message.reply("Finished Recording, Processing... please wait upto 10 minutes").queue();

        File unCompressedFile = writeToFile();
        this.byteBuffer.clear(); // We have no use for this anymore
        this.seconds = -1; // We have no use for this anymore
        this.timeElapsed = -1; // We have no use for this anymore

        // We make sure to set recording to false and message to null after we are done needing them!
        Util.call(() ->
                compressFile(unCompressedFile, (file) -> {
                    message.reply("Finished Recording!").addFiles(FileUpload.fromData(file, "VC recording.mp3")).queue((message) -> {
                        // Delete files, no need to have them. They take space which is bad.
                        file.delete();
                        unCompressedFile.delete();
                    });
                    this.recording = false;
                    this.message = null;
                    this.botMessage = null;
                }));

    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        if (recording) {
            if (byteBuffer.remaining() <= 960 || timeLeft <= 0) {
                stopRecording();
                return;
            }

            timeLeft -= 20;
            timeElapsed += 20;
            if (timeLeft % 1000 == 0)
                botMessage.editMessage(STARTED_RECORDING_LEFT.formatted(seconds, timeLeft / 1000)).queue();
            byteBuffer.put(combinedAudio.getAudioData(1));
        }
    }

    private File writeToFile() {
        File output;
        try {
            ByteArrayInputStream IS = new ByteArrayInputStream(byteBuffer.array());
            AudioInputStream AIS = new AudioInputStream(IS, AudioReceiveHandler.OUTPUT_FORMAT, 960L * (timeElapsed / 20));

            output = new File(FILE_RAW_OUTPUT.formatted(id));

            AudioSystem.write(AIS, AudioFileFormat.Type.WAVE, output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public void compressFile(Consumer<File> fileConsumer) {
        Util.call(() -> compressFile(new File(FILE_RAW_OUTPUT.formatted(id)), fileConsumer));
    }

    private void compressFile(File unCompressedFile, Consumer<File> fileConsumer) {
        String output = FILE_COMPRESSED_OUTPUT.formatted(id);

        FFmpegProbeResult result;

        try {
            result = Main.FFmpegLoader.getProbe().probe(unCompressedFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(result)
                .overrideOutputFiles(true)
                .addOutput(output)
                .setTargetSize(25_000_000) // 25MB
                .setFormat("mp3")
                .setAudioCodec("libmp3lame")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(Main.FFmpegLoader.get(), Main.FFmpegLoader.getProbe());
        executor.createJob(builder).run();

        fileConsumer.accept(new File(output));
    }

}
