package ru.rien.bot.modules.command;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.List;

public interface SubCommandGroups {

    SubcommandGroupData getSubcommandGroup();

    default List<SubCommand> subcommands() {
        return new ArrayList<>();
    }
}
