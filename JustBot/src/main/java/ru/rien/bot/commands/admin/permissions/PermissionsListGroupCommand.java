package ru.rien.bot.commands.admin.permissions;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.permission.Group;
import ru.rien.bot.utils.GeneralUtils;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.pagination.PagedEmbedBuilder;
import ru.rien.bot.utils.pagination.PaginationUtil;

import java.util.List;
import java.util.Set;

public class PermissionsListGroupCommand  implements SubCommand {

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("list", "list group");
    }

    @Override
    public void execute(CommandEvent event) {
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String group_name = optionMappingList.get(0).getAsString();
        event.getEvent().deferReply(true).queue();
        InteractionHook interactionHook = event.getEvent().getHook();
        TextChannel textChannel = event.getChannel();
        Group group = getPermissions(textChannel).getGroup(group_name);
        if (group == null) {
            MessageUtils.sendErrorMessage("Такой группы нет. Создайте её `/permissions group " +
                    group_name + " create`", interactionHook);
            return;
        }
        int page = 1;
        if(optionMappingList.size() == 2){
            page = (int)optionMappingList.get(1).getAsLong();
        }
        Set<String> perms = group.getPermissions();
        List<String> permList = GeneralUtils.orderList(perms);

        String list = String.join("\n", permList);

        PagedEmbedBuilder<String> pe =
                new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(list, PaginationUtil.SplitMethod.NEW_LINES, 25));
        pe.setTitle("Список прав группы: " + group.getName());
        pe.enableCodeBlock();

        PaginationUtil.sendEmbedPagedMessage(pe.build(), page - 1, interactionHook, event.getSender(), ButtonGroupConstants.PERMISSIONS_GROUP);

    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "group", "group name", true),
                new OptionData(OptionType.INTEGER, "page", "page of list")
        };
    }
}