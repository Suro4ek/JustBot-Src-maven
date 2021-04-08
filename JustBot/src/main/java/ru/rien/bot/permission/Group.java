package ru.rien.bot.permission;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class Group {

    private long id;
    private final Set<String> permissions = Sets.newConcurrentHashSet();
    private String name;
    private String roleId;
    private boolean def = false;

    private Group() {
    }
    Group(String name) {
        this.name = name;
    }


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addPermission(String permission) {
        return permissions.add(permission);
    }

    public boolean addPermission(List<String> permission) {
        return permissions.addAll(permission);
    }


    public boolean removePermission(String permission) {
        return permissions.remove(permission);
    }

    public Permission.Reply hasPermission(Permission permission) {
        for (String s : permissions) {
            if (new PermissionNode(s.substring(s.startsWith("-") ? 1 : 0)).test(permission.getPermission())) {
                if (s.startsWith("-"))
                    return Permission.Reply.DENY;
                return Permission.Reply.ALLOW;
            }
        }
        return Permission.Reply.NEUTRAL;
    }

    public void linkRole(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleId() {
        return roleId;
    }
}