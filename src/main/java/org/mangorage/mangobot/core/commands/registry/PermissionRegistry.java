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

package org.mangorage.mangobot.core.commands.registry;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class PermissionRegistry {
    private static final HashMap<String, PermissionRegistry> REGISTRY = new HashMap<>();
    private static final PermissionRegistry GLOBAL = new PermissionRegistry(null);

    public static PermissionRegistry global() {
        return GLOBAL;
    }

    public static PermissionRegistry guild(String id) {
        return REGISTRY.computeIfAbsent(id, PermissionRegistry::new);
    }

    public static boolean hasNeededPermission(Member member, APermission.Node node) {
        if (guild(member.getGuild().getId()).hasPermission(member, node))
            return true;

        return global().hasPermission(member, node);
    }

    private final String ID;
    private final HashMap<APermission.Node, ArrayList<APermission>> PERMISSIONS = new HashMap<>();
    private final HashMap<APermission.Node, ArrayList<Permission>> DISCORD_PERMISSIONS = new HashMap<>(); // Global only

    private PermissionRegistry(String id) {
        this.ID = id;
    }

    public void register(APermission.Node node, Permission... permissions) {
        if (ID != null)
            throw new IllegalStateException("Unable to register permissions on a GUILD level, this is for Global permissions only...");
        DISCORD_PERMISSIONS.computeIfAbsent(node, (key) -> new ArrayList<>());
        DISCORD_PERMISSIONS.get(node).addAll(Arrays.asList(permissions));
    }

    public void register(APermission.Node node, APermission... permissions) {
        if (ID == null)
            throw new IllegalStateException("Unable to register permissions on a GLOBAL level, this is for guilds only...");

        PERMISSIONS.computeIfAbsent(node, (key) -> new ArrayList<>());
        PERMISSIONS.get(node).addAll(Arrays.asList(permissions));
    }

    public boolean hasPermission(Member member, APermission.Node node) {
        // Check for Guild Perms first -> Discord Perms
        if (ID == null) {
            ArrayList<Permission> permissions = DISCORD_PERMISSIONS.get(node);
            EnumSet<Permission> memberPerms = member.getPermissions();

            if (permissions == null)
                return false;

            return memberPerms.stream().anyMatch(permissions::contains);
        } else {
            ArrayList<APermission> permissions = PERMISSIONS.get(node);
            List<String> rolesIDs = member.getRoles().stream().map(ISnowflake::getId).toList();

            if (permissions == null)
                return false;

            return rolesIDs.stream().anyMatch(permissions.stream().map(APermission::getID).toList()::contains);
        }
    }

}
