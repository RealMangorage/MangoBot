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

package org.mangorage.mangobot.core.permissions;

import net.dv8tion.jda.api.Permission;
import org.mangorage.mangobotapi.core.registry.APermission;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;

public class GlobalPermissions {
    public static final PermissionRegistry PERMISSIONS = PermissionRegistry.global();

    public static final APermission.Node PLAYING = APermission.Node.of("playing");
    public static final APermission.Node TRICK_ADMIN = APermission.Node.of("trickadmin");
    public static final APermission.Node PREFIX_ADMIN = APermission.Node.of("prefix");
    public static final APermission.Node RECORD_ADMIN = APermission.Node.of("record");


    static {
        PERMISSIONS.register(PLAYING, Permission.ADMINISTRATOR);
        PERMISSIONS.register(TRICK_ADMIN, Permission.ADMINISTRATOR);
        PERMISSIONS.register(PREFIX_ADMIN, Permission.ADMINISTRATOR);
        PERMISSIONS.register(RECORD_ADMIN, Permission.ADMINISTRATOR);
    }

    public static void init() {
    }
}
