package ru.rien.bot.modules.dota2;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.services.SteamService;
import ru.rien.bot.utils.MessageUtils;

@Component
public class SteamLinkCommand implements Command {
    @Autowired
    private SteamService steamService;

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        GuildWrapper guild = event.getGuild();
        User user = event.getSender();
        TextChannel channel = event.getChannel();
        if (args.length == 0) {
            MessageUtils.sendUsage(this, guild, event.getChannel(), event.getSender(), args);
            return;
        }
        try {
            Long steam_id = Long.parseLong(args[0]);
            steamService.createSteam(steam_id, user.getIdLong(), guild);
            MessageUtils.sendInfoMessage("Аккаунт привязан",channel,user);
        } catch (NumberFormatException e) {
            MessageUtils.sendErrorMessage("Введино не число", channel);
        }
    }

    @Override
    public String getCommand() {
        return "steam";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "привязка стима";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}steam [id]";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.STEAM_COMMAND;
    }
}
