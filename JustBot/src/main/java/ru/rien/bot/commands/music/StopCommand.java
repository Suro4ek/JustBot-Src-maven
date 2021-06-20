package ru.rien.bot.commands.music;


import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.modules.messsage.Language;
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
        event.getEvent().reply("list music was cleaned").queue();
        moduleDsBot.getMusicManager().getPlayer(event.getGuild().getGuildId()).stop();
    }

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public String getDescription(Language guild) {
        return guild.getMessage("STOP_DESCRIPTION");
    }

//    @Override
//    public String getUsage(GuildWrapper guild) {
//        return guild.getMessage("STOP_USAGE");
//    }

    @Override
    public Permission getPermission() {
        return Permission.STOP_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }
}