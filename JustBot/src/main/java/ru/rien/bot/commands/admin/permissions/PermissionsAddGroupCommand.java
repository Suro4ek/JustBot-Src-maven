package ru.rien.bot.commands.admin.permissions;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.permission.Group;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

public class PermissionsAddGroupCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("add", "add permission for group");
    }

    @Override
    public void execute(CommandEvent event) {
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String group_name = optionMappingList.get(0).getAsString();
        TextChannel channel = event.getChannel();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        Group group = getPermissions(channel).getGroup(group_name);
        if (group == null) {
            MessageUtils.sendErrorMessage("Такой группы нет. Создайте её `{%}permissions group " + group_name + " create`", channel);
            return;
        }
        String permission = optionMappingList.get(1).getAsString();
        if (!Permission.isValidPermission(permission)) {
            MessageUtils.sendErrorMessage("Такие права не найдены! Права начинаются с `justbot.` затем название команды!\n" +
                    "**Пример:** `justbot.play`\n" +
                    "Просмотр всех прав `"+event.getGuild().getPrefix()+"permissions list`", event.getEvent().deferReply(true));
            return;
        }
        if (group.addPermission(permission)) {
            MessageUtils.sendSuccessMessage("+ права `" + permission + "` в группу `" + group_name + "`", replyAction, event.getSender());
        } else {
            MessageUtils.sendErrorMessage("Не удалось добавить права (возможно они уже есть)", replyAction);
        }
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "name", "group name", true),
                new OptionData(OptionType.STRING, "permission", "permission for group", true),
        };
    }
}
