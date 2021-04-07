package ru.rien.bot.modules.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.PerGuildPermissions;

import java.util.EnumSet;

public interface Command {

    void execute(CommandEvent event);

    String getCommand();

    String getDescription(GuildWrapper guildWrapper);

    String getUsage(GuildWrapper guildWrapper);

    CommandType getType();

    default String getExtraInfo() {
        return null;
    }

    ru.rien.bot.permission.Permission getPermission();

    default EnumSet<net.dv8tion.jda.api.Permission> getDiscordPermission() {
        return getPermission().getDiscordPerm();
    }

    default String[] getAliases() {
        return new String[]{};
    }

    default PerGuildPermissions getPermissions(TextChannel chan) {
        return JustBotManager.instance().getGuild(chan.getGuild().getId()).getPermissions();
    }

    default boolean isDefaultPermission() {
        return getPermission() != null && getPermission().isDefaultPerm() && !getType().isInternal();
    }

    default boolean deleteMessage() {
        return true;
    }

    default boolean isBetaTesterCommand() {
        return false;
    }

    default char getPrefix(Guild guild) {
        return JustBotManager.instance().getGuild(guild.getId()).getPrefix();
    }
}