package ru.rien.bot.commands.admin.permissions;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import ru.rien.bot.commands.admin.permissions.group.*;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.command.SubCommandGroups;

import java.util.List;

public class PermissionsGroupCommand implements SubCommandGroups {

    @Override
    public SubcommandGroupData getSubcommandGroup() {
        return new SubcommandGroupData("group","manage groups");
    }

    @Override
    public List<SubCommand> subcommands() {
        return Lists.newArrayList(new PermissionsAddGroupCommand(),
                new PermissionsCreateGroupCommand(),
                new PermissionsListGroupCommand(),
                new PermissionsDeleteGroupCommand(),
                new PermissionsLinkGroupCommand(),
                new PermissionsUnlinkGroupCommand(),
                new PermissionsRemoveGroupCommand(),
                new PermissionsClearGropCommand(),
                new PermissionsCloneGroupCommand(),
                new PermissionsDefaultGroupCommand(),
                new PermissionsMassAddGroupCommand(),
                new PermissionsMoveGroupCommand(),
                new PermissionsRenameGroupCommand()
        );
    }
}
