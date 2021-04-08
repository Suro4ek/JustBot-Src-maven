package ru.rien.bot.commands.general;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.ModuleCommand;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class UsageCommand implements Command {

    @Autowired
    private ModuleCommand moduleCommand;

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        GuildWrapper guild = event.getGuild();
        if (args.length == 0) {
            MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
        } else {
            Command c = moduleCommand.getCommand(args[0], sender);
            if(c != null){
                MessageUtils.sendUsage(c,event.getGuild(), channel, sender, new String[]{});
            }else{
                MessageUtils.sendErrorMessage(guild.getMessage("USAGE_NOT_COMMAND"), channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "usage";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("USAGE_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("USAGE_USAGE");
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.USAGE_COMMAND;
    }
}
