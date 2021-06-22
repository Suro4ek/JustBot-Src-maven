package ru.rien.bot.commands.admin.permissions.group;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
import ru.rien.bot.objects.GuildWrapperLoader;
import ru.rien.bot.permission.Group;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

public class PermissionsCreateGroupCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("create", "create group");
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
                        if (!GuildWrapperLoader.ALLOWED_CHARS_REGEX.matcher(group_name).matches()) {
                            if (group_name.length() > 32)
                                MessageUtils.sendErrorMessage("Имя группы не должно быть больше 32 символов!", replyAction);
                            else if (group_name.length() < 3)
                                MessageUtils.sendErrorMessage("Имя группы должно быть с 3 символов!", replyAction);
                            else
                                MessageUtils.sendErrorMessage("Имя содержит странное название. Допустимые символы: `" + new String(GuildWrapperLoader.ALLOWED_SPECIAL_CHARACTERS) + "`", replyAction);
                            return;
                        }
                        if (guildWrapper.getPermissions().addGroup(group_name)) {
                            MessageUtils.sendSuccessMessage("Группа: `" + group_name + "` создана", replyAction, event.getSender());
                        } else {
                            MessageUtils.sendErrorMessage("Группа уже существует", replyAction);
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
                new OptionData(OptionType.STRING, "group", "group name", true)
        };
    }
}