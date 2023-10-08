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

package org.mangorage.mangobot.modules.requestpaste;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangobot.core.BotSettings;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;
import org.mangorage.mangobotapi.core.util.LazyReference;
import org.mangorage.mangobotapi.core.util.TaskScheduler;
import org.mangorage.mboteventbus.impl.IEventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PasteRequestModule {
    private static final LazyReference<GitHubClient> GITHUB_CLIENT = LazyReference.create(() -> new GitHubClient().setOAuth2Token(BotSettings.PASTE_TOKEN.get()));
    private static final List<String> VALID_EXTENSIONS = List.of(
            "txt",
            "log"
    );

    public static void register(IEventBus bus) {
        bus.addListener(DMessageRecievedEvent.class, PasteRequestModule::onMessage);
    }


    private static boolean validExtension(@Nullable String extension) {
        return extension != null && VALID_EXTENSIONS.contains(extension);
    }

    private static GistFile getData(InputStream stream, String fileName) {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (stream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var result = new GistFile();
        result.setContent(textBuilder.toString());
        result.setFilename(fileName);
        return result;
    }

    public static void onMessage(DMessageRecievedEvent event) {
        TaskScheduler.getExecutor().execute(() -> {
            var dEvent = event.get();

            if (!dEvent.isFromGuild()) return;
            if (!dEvent.getGuild().getId().equals("1129059589325852724")) return;

            var message = dEvent.getMessage();
            var attachments = message.getAttachments().stream().filter(attachment -> validExtension(attachment.getFileExtension())).toList();

            if (attachments.isEmpty()) return;

            StringBuilder results = new StringBuilder();

            GitHubClient CLIENT = GITHUB_CLIENT.get();
            GistService service = new GistService(CLIENT);

            Gist gist = new Gist();
            gist.setPublic(true);
            gist.setUser(new User().setName("MangoBot"));
            gist.setDescription("Automatically made from MangoBot.");

            HashMap<String, GistFile> FILES = new HashMap<>();
            attachments.forEach(attachment -> {
                try {
                    FILES.put(attachment.getFileName(), getData(attachment.getProxy().download().get(), attachment.getFileName()));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            gist.setFiles(FILES);

            try {
                var remote = service.createGist(gist);

                message.reply("gist -> %s".formatted(remote.getHtmlUrl())).setSuppressEmbeds(true).mentionRepliedUser(false).queue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }


}
