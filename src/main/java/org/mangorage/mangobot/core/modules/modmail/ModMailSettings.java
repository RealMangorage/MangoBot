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

package org.mangorage.mangobot.core.modules.modmail;

import com.google.gson.annotations.Expose;
import org.mangorage.mangobot.core.Util;

public class ModMailSettings {
    @Expose
    private final String guildID;
    @Expose
    private String categoryID = "none";
    @Expose
    private final String transcriptChannelID = "none";
    @Expose
    private final String joinMessage = """
            The %s ModMail Team will be with you shortly!
            """;
    @Expose
    private String guildName;

    public ModMailSettings(String guildID, String categoryID, String guildName) {
        this.guildID = guildID;
        this.categoryID = categoryID;
        this.guildName = guildName;
    }

    public String getGuildID() {
        return guildID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public void save() {
        Util.saveObjectToFile(this, ModMailHandler.SAVEDIR_GUILDS.formatted(guildID), "settings.json");
    }
}
