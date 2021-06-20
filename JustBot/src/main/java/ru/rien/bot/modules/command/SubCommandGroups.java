package ru.rien.bot.modules.command;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import ru.rien.bot.commands.admin.permissions.PermissionsTest;

import java.util.ArrayList;
import java.util.List;

public interface SubCommandGroups {

    SubcommandGroupData getSubcommandGroup();

    default List<SubCommand> subcommands() {
        return new ArrayList<>();
    }
}
