package ru.rien.bot.commands.admin;

import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;

@Component
public class LangCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
            event.getChannel().sendMessage("Correct text: " + "https://github.com/1Suro1/JustBot/tree/main/lang").queue();
    }

    @Override
    public String getCommand() {
        return "lang";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return guildWrapper.getMessage("LANG_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return guildWrapper.getMessage("LANG_USAGE");
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.LANG_COMMAND;
    }
}
