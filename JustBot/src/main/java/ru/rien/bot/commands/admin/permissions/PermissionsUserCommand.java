package ru.rien.bot.commands.admin.permissions;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.command.SubCommandGroups;

import java.util.List;

public class PermissionsUserCommand implements SubCommandGroups {

    @Override
    public SubcommandGroupData getSubcommandGroup() {
        return new SubcommandGroupData("user","manage users");
    }

    @Override
    public List<SubCommand> subcommands() {
        return Lists.newArrayList(

        );
    }
}
