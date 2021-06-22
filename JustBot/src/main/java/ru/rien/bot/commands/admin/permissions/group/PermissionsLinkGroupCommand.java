package ru.rien.bot.commands.admin.permissions.group;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Group;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

public class PermissionsLinkGroupCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("link", "link group");
    }

    @Override
    public void execute(CommandEvent event) {
        User user = event.getSender();
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String guildId = optionMappingList.get(0).getAsString();
        GuildWrapper guildWrapper = JustBotManager.instance().getGuildNoCache(guildId);
        if (guildWrapper != null) {
            Guild guild = guildWrapper.getGuild();
            if (guild != null) {
                Member member = guild.retrieveMember(user).complete();
                if (member != null) {
                    if (member.getPermissions().contains(net.dv8tion.jda.api.Permission.ADMINISTRATOR)) {
                        String group_name = optionMappingList.get(1).getAsString();
                        Role role = optionMappingList.get(2).getAsRole();
                        ReplyAction replyAction = event.getEvent().deferReply(true);
                        Group group = guildWrapper.getPermissions().getGroup(group_name);
                        if (group == null) {
                            MessageUtils.sendErrorMessage("Такой группы нет. Создайте её `{%}permissions group " + group_name + " create`", replyAction);
                            return;
                        }
                        group.linkRole(role.getId());
                        MessageUtils.sendSuccessMessage("Группа `" + group_name + "` связана с `" + role.getName() + "`",
                                replyAction, event.getSender());
                    } else {
                        MessageUtils.sendErrorMessage("У вас не хватает прав", event.getEvent().deferReply(true));
                    }
                } else {
                    MessageUtils.sendErrorMessage("У вас не хватает прав", event.getEvent().deferReply(true));
                }
            } else {
                MessageUtils.sendErrorMessage("Такого сервера нет", event.getEvent().deferReply(true));
            }
        } else {
            MessageUtils.sendErrorMessage("Такого сервера нет", event.getEvent().deferReply(true));
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "guild_id", "ваш id сервера", true),
                new OptionData(OptionType.STRING, "group", "group name", true),
                new OptionData(OptionType.ROLE, "role", "this is just role server", true)
        };
    }
}