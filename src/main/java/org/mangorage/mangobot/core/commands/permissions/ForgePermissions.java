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

package org.mangorage.mangobot.core.commands.permissions;

import org.mangorage.mangobotapi.core.registry.APermission;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;

import static org.mangorage.mangobot.core.commands.ForgeCommands.COMMANDS;
import static org.mangorage.mangobot.core.commands.permissions.GlobalPermissions.RECORD_ADMIN;
import static org.mangorage.mangobot.core.commands.permissions.GlobalPermissions.TRICK_ADMIN;

public class ForgePermissions {
    public static final PermissionRegistry PERMISSIONS = PermissionRegistry.guild(COMMANDS.getID());


    static {
        // Admin Role
        PERMISSIONS.register(TRICK_ADMIN, APermission.of("1129067881842360381"));
        // Moderators Role
        PERMISSIONS.register(TRICK_ADMIN, APermission.of("1129070272302022656"));
        // Mango Bot Tester Role
        PERMISSIONS.register(TRICK_ADMIN, APermission.of("1150880910745538631"));

        // Admin Role
        PERMISSIONS.register(RECORD_ADMIN, APermission.of("1129067881842360381"));
        // Moderator Role
        PERMISSIONS.register(RECORD_ADMIN, APermission.of("1130506065381957642"));
        // Moderators Role
        PERMISSIONS.register(RECORD_ADMIN, APermission.of("1129070272302022656"));
    }
}
