package ru.rien.bot.modules.command;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.permission.PerGuildPermissions;

public interface SubCommand {

    SubcommandData getSubCommands();

    void execute(CommandEvent event);

    default PerGuildPermissions getPermissions(TextChannel chan) {
        return JustBotManager.instance().getGuild(chan.getGuild().getId()).getPermissions();
    }

    OptionData[] parameters();
}
