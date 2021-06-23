package ru.rien.bot.objects;

import com.google.common.collect.Sets;
import ru.rien.bot.modules.command.Command;

import java.util.Arrays;
import java.util.Set;

public class GuildSettings {

    private boolean deleteCommands;
    private Set<Long> channelBlacklist;
    private Set<Long> userBlacklist;
    private Set<String> blacklistCommands;

    public GuildSettings() {
        this.deleteCommands = true;
        this.channelBlacklist = Sets.newConcurrentHashSet();
        this.userBlacklist = Sets.newConcurrentHashSet();
        this.blacklistCommands = Sets.newConcurrentHashSet();
    }

    public Set<String> getBlacklistCommands() {
        return blacklistCommands;
    }

    public void removeBlackListCommands(String command) {
        this.blacklistCommands.remove(command);
    }

    public void addBlackListCommands(String command) {
        this.blacklistCommands.add(command);
    }

    public boolean shouldDeleteCommands() {
        return this.deleteCommands;
    }

    public void setDeleteCommands(boolean deleteCommands) {
        this.deleteCommands = deleteCommands;
    }

    public Set<Long> getChannelBlacklist() {
        return channelBlacklist;
    }

    public void addChannelToBlacklist(long channelId) {
        this.channelBlacklist.add(channelId);
    }

    public void removeChannelFromBlacklist(long channelId) {
        this.channelBlacklist.remove(channelId);
    }

    public Set<Long> getUserBlacklist() {
        return userBlacklist;
    }

    public void addUserToBlacklist(long userId) {
        this.userBlacklist.add(userId);
    }

    public void removeUserFromBlacklist(long userId) {
        this.userBlacklist.remove(userId);
    }

    @Override
    public String toString() {
        return String.format("{deleteCommands: %b, channelBlacklist: %s, userBlacklist: %s}",
                deleteCommands,
                Arrays.toString(channelBlacklist.toArray()),
                Arrays.toString(userBlacklist.toArray())
        );
    }
}