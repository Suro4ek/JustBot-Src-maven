package ru.rien.bot.commands.nsfw;

import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;

@Component
public class YaoiCommand implements Command {
    @Override
    public void execute(CommandEvent event) {

    }

    @Override
    public String getCommand() {
        return "yaoi";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"яой"};
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "случайная яой картинка";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}yaoi - случайная яой картинка";
    }

    @Override
    public CommandType getType() {
        return CommandType.NSWF;
    }

    @Override
    public Permission getPermission() {
        return Permission.YAOI_COMMAND;
    }
}
