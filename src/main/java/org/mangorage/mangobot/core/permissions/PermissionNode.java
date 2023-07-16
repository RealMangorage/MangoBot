package org.mangorage.mangobot.core.permissions;

import net.dv8tion.jda.api.entities.Member;
import java.util.ArrayList;
import java.util.List;

public class PermissionNode {
    private final List<String> ROLE_IDS = new ArrayList<>();
    public PermissionNode(String... ROLES) {
        this.ROLE_IDS.addAll(ROLE_IDS);
    }

    public boolean hasPermissions(Member user) {
        boolean hasPerm = user.getRoles().stream().filter(e -> ROLE_IDS.contains(e.getId())).count() > 0;
        return hasPerm;
    }
}
