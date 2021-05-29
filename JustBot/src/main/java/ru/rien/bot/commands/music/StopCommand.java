package ru.rien.bot.commands.music;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;

@Component
public class StopCommand implements Command {

    private final ModuleDsBot moduleDsBot;

    public StopCommand(ModuleDsBot moduleDsBot) {
        this.moduleDsBot = moduleDsBot;
    }

    @Override
    public void execute(CommandEvent event) {
        moduleDsBot.getMusicManager().getPlayer(event.getGuild().getGuildId()).stop();
    }

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("STOP_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("STOP_USAGE");
    }

    @Override
    public Permission getPermission() {
        return Permission.STOP_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}