package ru.rien.bot.commands.admin.permissions.group;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GeneralUtils;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.pagination.PagedEmbedBuilder;
import ru.rien.bot.utils.pagination.PaginationUtil;

import java.util.List;
import java.util.Set;


public class PermissionsRemoveGroupCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("remove", "remove permission for group");
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
                        ReplyAction replyAction = event.getEvent().deferReply(true);
                        Group group = guildWrapper.getPermissions().getGroup(group_name);
                        if (group == null) {
                            MessageUtils.sendErrorMessage("Такой группы нет. Создайте её `{%}permissions group " + group_name + " create`", replyAction);
                            return;
                        }
                        String permission = optionMappingList.get(1).getAsString();
                        if (!Permission.isValidPermission(permission)) {
                            MessageUtils.sendErrorMessage("Такие права не найдены! Права начинаются с `justbot.` затем название команды!\n" +
                                    "**Пример:** `justbot.play`\n" +
                                    "Просмотр всех прав `"+event.getGuild().getPrefix()+"permissions list`", event.getEvent().deferReply(true));
                            return;
                        }
                        if (group.removePermission(permission)) {
                            MessageUtils.sendSuccessMessage("+ права `" + permission + "` в группу `" + group_name + "`", replyAction, event.getSender());
                        } else {
                            MessageUtils.sendErrorMessage("Не удалось добавить права (возможно они уже есть)", replyAction);
                        }
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
                new OptionData(OptionType.STRING, "name", "group name", true),
                new OptionData(OptionType.STRING, "permission", "permission for group", true),
        };
    }
}
