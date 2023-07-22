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

package org.mangorage.mangobot.core.music;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.Main;

import java.io.InputStream;


public class SphnixModelLoader {

    public static final StreamSpeechRecognizer recognizer;
    private static boolean running = false;

    static {
        try {
            System.out.println("Loading models...");

            Configuration configuration = new Configuration();

            // Load model from the jar
            configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");

            // You can also load model from folder
            // configuration.setAcousticModelPath("file:en-us");

            configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

            recognizer = new StreamSpeechRecognizer(configuration);
            InputStream stream = Main.class.getResourceAsStream("/edu/cmu/sphinx/demo/aligner/10001-90210-01803.wav");

            stream.skip(44);

            // Simple recognition with generic model
            recognizer.startRecognition(stream);
            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {

                System.out.format("Hypothesis: %s\n", result.getHypothesis());

                System.out.println("List of recognized words and their times:");
                for (WordResult r : result.getWords()) {
                    System.out.println(r);
                }

                System.out.println("Best 3 hypothesis:");
                for (String s : result.getNbest(3))
                    System.out.println(s);

            }
            recognizer.stopRecognition();
        } catch (Exception E) {
            throw new IllegalStateException();
        }


    }

    public static void attempt(InputStream stream, MessageChannelUnion channelUnion) {
        if (running) return;
        running = true;

        recognizer.startRecognition(stream);
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            System.out.format("Hypothesis: %s For US", result.getHypothesis());

            System.out.println("List of recognized words and their times:  For US");
            if (result.getWords() != null) {
                for (WordResult r : result.getWords()) {
                    System.out.println(r);
                }
            }

            System.out.println("Best 3 hypothesis: For US");

            for (String s : result.getNbest(3))
                System.out.println(s);

            channelUnion.sendMessage(result.getHypothesis()).queue();

        }

        running = false;
    }

    public static void init() {
    }
}
