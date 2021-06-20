package ru.rien.bot.commands.admin.permissions;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.objects.GuildWrapperLoader;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

public class PermissionsDeleteGroupCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("delete", "delete group");
    }

    @Override
    public void execute(CommandEvent event) {
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String group_name = optionMappingList.get(0).getAsString();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        TextChannel textChannel = event.getChannel();
        getPermissions(textChannel).deleteGroup(group_name);
        MessageUtils.sendSuccessMessage("Группа `" + group_name + "` удалена", replyAction, event.getSender());
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "group", "group name", true)
        };
    }
}