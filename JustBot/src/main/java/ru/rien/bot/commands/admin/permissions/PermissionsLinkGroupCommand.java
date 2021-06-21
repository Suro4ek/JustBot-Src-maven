package ru.rien.bot.commands.admin.permissions;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
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
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String group_name = optionMappingList.get(0).getAsString();
        Role role = optionMappingList.get(1).getAsRole();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        TextChannel textChannel = event.getChannel();
        Group group = getPermissions(textChannel).getGroup(group_name);
        if (group == null) {
            MessageUtils.sendErrorMessage("Такой группы нет. Создайте её `{%}permissions group " + group_name + " create`", replyAction);
            return;
        }
        group.linkRole(role.getId());
        MessageUtils.sendSuccessMessage("Группа `" + group_name + "` связана с `" + role.getName() + "`",
                replyAction, event.getSender());
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "group", "group name", true),
                new OptionData(OptionType.ROLE, "role", "this is just role server", true)
        };
    }
}