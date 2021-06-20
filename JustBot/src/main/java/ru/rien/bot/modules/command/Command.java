package ru.rien.bot.modules.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.PerGuildPermissions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public interface Command {

    void execute(CommandEvent event);

    String getCommand();

    String getDescription(Language language);

//    String getUsage(GuildWrapper guildWrapper);

    default List<SubCommand> getSubCommands(){
        return new ArrayList<>();
    }

    default List<SubCommandGroups> getSubCommandGruops(){
        return new ArrayList<>();
    }

    CommandType getType();

    OptionData[] parameters();

//    default boolean isChat false;

    default String getExtraInfo() {
        return "";
    }

    ru.rien.bot.permission.Permission getPermission();

    default EnumSet<Permission> getDiscordPermission() {
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