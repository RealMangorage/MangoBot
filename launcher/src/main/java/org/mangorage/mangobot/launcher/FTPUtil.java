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

package org.mangorage.mangobot.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.mangorage.mangobot.launcher.data.FTPSettings;
import org.mangorage.mangobot.launcher.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FTPUtil {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DATA_FOLDER = "/data";
    private static final String RESOURCES_FOLDER = "/botresources";

    public static void main(String[] args) {
        FTPSettings SETTINGS = Util.loadJsonToObject(GSON, "F:\\Discord Bot Projects\\mangobot\\secret\\settings.json", FTPSettings.class);

    }

    public static void downloadFromPebble(FTPSettings settings) {
        FTPClient client = new FTPClient();
        try {
            client.connect(settings.host(), settings.port());
            client.login(settings.username(), settings.password());

            downloadRecursive(client, "F:\\Discord Bot Projects\\mangobot\\data\\", DATA_FOLDER);
            downloadRecursive(client, "F:\\Discord Bot Projects\\mangobot\\botresources\\", RESOURCES_FOLDER);

            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void downloadRecursive(FTPClient client, String localDirectory, String remoteDirectory) throws IOException {
        for (FTPFile ftpFile : client.listFiles(remoteDirectory)) {
            if (ftpFile.isDirectory())
                downloadRecursive(client, localDirectory + ftpFile.getName() + "\\", remoteDirectory + "/" + ftpFile.getName());
            else {
                File file = new File(localDirectory + ftpFile.getName());
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileOutputStream stream = new FileOutputStream(localDirectory + ftpFile.getName());
                client.retrieveFile(remoteDirectory + "/" + ftpFile.getName(), stream);
                System.out.println("Downloaded " + ftpFile.getName());
            }
        }
    }

}
