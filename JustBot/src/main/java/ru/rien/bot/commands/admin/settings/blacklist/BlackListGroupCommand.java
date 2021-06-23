package ru.rien.bot.commands.admin.settings.blacklist;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.command.SubCommandGroups;

import java.util.List;

public class BlackListGroupCommand implements SubCommandGroups {
    @Override
    public SubcommandGroupData getSubcommandGroup() {
        return new SubcommandGroupData("blacklist", "черный список");
    }

    @Override
    public List<SubCommand> subcommands() {
        return Lists.newArrayList(new BlackListListCommand(),
                new BlackListAddCommand(),
                new BlackListRemoveCommand());
    }
}
