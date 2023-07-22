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

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import org.mangorage.mangobot.core.music.SphnixModelLoader;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.function.Consumer;

public class VoiceChatTranscripter implements AudioReceiveHandler {
    private static final String FILE_RAW_OUTPUT = "data/guildata/%s/recordingdata/snipet_%s.wav";
    private static final String FILE_PROCESSED_OUTPUT = "data/guildata/%s/recordingdata/snipet_processed_%s.wav";

    private static final HashMap<String, VoiceChatTranscripter> VOICE_CHAT_TRANSCRIPTERS = new HashMap<>();

    public static VoiceChatTranscripter getInstance(String guildID) {
        VoiceChatTranscripter transcripter = VOICE_CHAT_TRANSCRIPTERS.getOrDefault(guildID, new VoiceChatTranscripter(guildID));
        if (!VOICE_CHAT_TRANSCRIPTERS.containsKey(guildID))
            VOICE_CHAT_TRANSCRIPTERS.put(guildID, transcripter);
        return transcripter;
    }

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(((3_840_000 * 6) / 60) * 5);
    private final String ID;
    private boolean recording = false;
    private int snipetCount = 0;
    private int timeElapsed = 0;
    private MessageChannelUnion channel;

    private VoiceChatTranscripter(String guildID) {
        this.ID = guildID;
        new File("data/guildata/%s/recordingdata/").mkdirs();
    }

    public void start(Message message, VoiceChannel channel) {
        this.recording = true;
        this.channel = message.getChannel();

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


    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        if (recording) {
            if (byteBuffer.remaining() <= 960) {
                processData(byteBuffer.duplicate(), snipetCount, timeElapsed);
                byteBuffer.clear();
                snipetCount++;
                timeElapsed = 0;
                return;
            }

            timeElapsed += 20;
            byteBuffer.put(combinedAudio.getAudioData(1));
        }
    }

    /**
     * @return
     */
    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    private void processData(ByteBuffer byteBuffer, int count, int timeElapsedTotal) {
        speech(byteBuffer, count, timeElapsedTotal);
    }

    private void speech(ByteBuffer byteBuffer, int count, int timeElapsedTotal) {

        try {
            ByteArrayInputStream IS = new ByteArrayInputStream(byteBuffer.array());
            AudioInputStream AIS = new AudioInputStream(IS, AudioReceiveHandler.OUTPUT_FORMAT, 960L * (timeElapsedTotal / 20));

            SphnixModelLoader.attempt(AIS, channel);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compressFile(File input, Consumer<File> fileConsumer, int snipetCount) {
        File output = new File(FILE_PROCESSED_OUTPUT.formatted(ID, snipetCount));

        AudioAttributes audio = new AudioAttributes();
        audio.setBitRate(16);
        audio.setSamplingRate(16_000);

        //Encoding attributes
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audio);
        attrs.setOutputFormat("s16le");


        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(input), output, attrs);
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }

        fileConsumer.accept(output);
    }

    public static void main(String[] args) throws EncoderException {
        Encoder enc = new Encoder();

        for (String supportedEncodingFormat : enc.getSupportedEncodingFormats()) {
            System.out.println(supportedEncodingFormat);
        }
    }
}
