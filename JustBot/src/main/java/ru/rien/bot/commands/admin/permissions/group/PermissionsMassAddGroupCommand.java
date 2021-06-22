package ru.rien.bot.commands.admin.permissions.group;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Group;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

public class PermissionsMassAddGroupCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("massadd", "добавить всех в группу");
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
                        Group group = guildWrapper.getPermissions().getGroup(group_name);
                        if (group == null) {
                            MessageUtils.sendErrorMessage("Такой группы нет. Создайте её `/permissions group [guild_id] " + group_name + " create`", event.getEvent().deferReply(true));
                            return;
                        }
                        String who = optionMappingList.get(2).getAsString();
                        List<Member> roleMembers;
                        String roleName = "";
                        switch (who) {
                            case "@everyone":
                                roleMembers = guild.getMembers();
                                roleName = "everyone";
                                break;
                            case "@here":
                                roleMembers = guild.getMembers();
                                roleName = "here";
                                break;
                            default:
                                Role role = GuildUtils.getRole(who, guild.getId());
                                if (role != null) {
                                    roleMembers = guild.getMembersWithRoles(role);
                                } else {
                                    MessageUtils.sendErrorMessage("Такой роли нет", event.getEvent().deferReply(true));
                                    return;
                                }
                                break;
                        }
                        for (Member user1 : roleMembers) {
                            guildWrapper.getPermissions().getUser(user1).addGroup(group);
                        }
                        MessageUtils.sendSuccessMessage("Успешно добавлена `" + group_name + "` группа для всех  @" + roleName, event.getEvent().deferReply(true), user);
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
                new OptionData(OptionType.STRING, "who add", "example: @everyone, @here, role_id", true)
        };
    }
}
