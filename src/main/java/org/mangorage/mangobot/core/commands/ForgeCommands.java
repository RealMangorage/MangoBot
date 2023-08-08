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

package org.mangorage.mangobot.core.commands;

import org.mangorage.mangobot.commands.PingCommand;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.registry.APermission;
import org.mangorage.mangobotapi.core.registry.CommandRegistry;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;
import org.mangorage.mangobotapi.core.registry.RegistryObject;
import org.mangorage.mangobotapi.core.registry.commands.CommandHolder;

import static org.mangorage.mangobot.core.permissions.GlobalPermissions.TRICK_ADMIN;

public class ForgeCommands {
    public static final CommandRegistry COMMANDS = CommandRegistry.guild("1129059589325852724");
    public static final PermissionRegistry PERMISSIONS = PermissionRegistry.guild(COMMANDS.getID());

    public static final RegistryObject<CommandHolder<PingCommand>> PING = COMMANDS.register("pings", new PingCommand());


    static {
        PERMISSIONS.register(TRICK_ADMIN, APermission.of("1129067881842360381"));
        PERMISSIONS.register(TRICK_ADMIN, APermission.of("1129070272302022656"));

        /**
         COMMANDS.register("oldsl", new ReplyCommand(
         """
         # Forge Only Supports 1.19.x and 1.20.x
         The Forge Discord only supports 1.19.x and 1.20.x
         The Forge Project including everyone in this discord, and the forums, only supports the latest and LTS versions. Currently 1.19.x and 1.20.x.
         This is due to the Forge team having limited manpower as we are all people working in our free time. We can't possibly support all Minecraft versions that had a Forge build.
         Some say a version of Minecraft is the best but others say that about a different version.
         All of the older versions work and there are some other communities that offer support for older versions.
         """
         ));
         */


        /**
         COMMANDS.register("rules", new ReplyCommand("""
         # Rules
         1) Do not DM anyone without asking for permission first.
         2) No spamming. This includes emojis, GIFs, many single word messages or advertisements.
         3) Don't be a dick, This may be complex to some but just because you have an argument with someone doesn't mean they were being a dick.  You're allowed to disagree
         but keep it in the realm of Minecraft, and Forge specifically. Don't go on personal attacks, and don't go looking to be offended for others.
         4) Do not promote or participate in illegal activities, including piracy, and scams.
         5) Do not send content that is hateful or sexually explicit. This is not a NSFW server.
         6) You Must follow Discord's TOS

         # Moderation
         We have a 3 strike policy, Rule violations (listed above) can result in a strike.

         1) Strike one results in you being given a warning in the channel where the violation occurred stating which rule was broken.
         2) Strike two result in you being muted
         3) Strike three  will result in a ban

         # Ban Appeals
         <@155149108183695360> will you send you a forum when/if you are banned.
         ** If you believe anyone has broken any rules please do not hesitate to contact moderators by pinging <@&1129070272302022656> or sending them a Private Message. **
         **invite:** https://discord.minecraftforge.net/
         **backup:** https://discord.com/invite/UuM6bmAjXh
         """).notifications(false));
         **/
    }

    public static void init() {
        COMMANDS.register(Bot.EVENT_BUS);
    }
}
