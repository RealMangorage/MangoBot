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

package org.mangorage.mangobot.core.config;

import net.dv8tion.jda.api.Permission;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;
import org.mangorage.mangobotapi.core.registry.UserPermission;

public class GlobalPermissions {
    public static final PermissionRegistry PERMISSIONS = PermissionRegistry.global();

    public static final UserPermission.Node PLAYING = UserPermission.Node.of("playing");
    public static final UserPermission.Node TRICK_ADMIN = UserPermission.Node.of("trickadmin");
    public static final UserPermission.Node PREFIX_ADMIN = UserPermission.Node.of("prefix");
    public static final UserPermission.Node MOD_MAIL = UserPermission.Node.of("mod_mail");


    static {
        PERMISSIONS.register(PLAYING, Permission.ADMINISTRATOR);
        PERMISSIONS.register(TRICK_ADMIN, Permission.ADMINISTRATOR);
        PERMISSIONS.register(PREFIX_ADMIN, Permission.ADMINISTRATOR);
        PERMISSIONS.register(MOD_MAIL, Permission.ADMINISTRATOR);
    }

    public static void init() {
    }
}
