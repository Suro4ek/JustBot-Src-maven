package ru.rien.bot.commands.general;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class InfoCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        TextChannel channel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        User user = event.getSender();
        //TODO Пофиксить имя создатель сервера
        MessageUtils.sendInfoMessage("Создатель сервера: " + guild.getGuild().getOwner().getUser().getName() + "\n"
                    +"Участников: " + guild.getGuild().getMembers().size() + "\n"
                    +"Регион: " + guild.getGuild().getRegion().getName(), channel,user);

    }

    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Получить информацию про сервер";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}info - получение информации про сервер";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.INFO_COMMAND;
    }
}
