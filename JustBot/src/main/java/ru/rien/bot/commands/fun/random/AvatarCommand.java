package ru.rien.bot.commands.fun.random;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

import java.awt.*;

@Component
public class AvatarCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        User user = event.getSender();
        String[] args = event.getArgs();
        TextChannel channel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        User sender = event.getSender();
        if (args.length > 0)
            user = GuildUtils.getUser(MessageUtils.getMessage(args, 0), guild.getGuildId());
        if (user != null) {
            if (!user.getId().equals(sender.getId()) && !getPermissions(channel).hasPermission(event.getMember(), Permission.AVATAR_OTHER)) {
                MessageUtils.sendErrorMessage("У вас нет прав `" + Permission.AVATAR_OTHER + "` ",
                        channel);
                return;
            }
            channel.sendMessage(MessageUtils.getEmbed(sender).setColor(Color.cyan).setAuthor(user.getName(), null, null)
                    .setImage(user.getEffectiveAvatarUrl()).build()).queue();
        } else
            MessageUtils.sendErrorMessage("Пользователь не найден!", channel);
    }

    @Override
    public String getCommand() {
        return "avatar";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Получить аватарку участника сервера";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}avatar [пользователь] - Получить аватарку участника сервера";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Permission getPermission() {
        return Permission.AVATAR_COMMAND;
    }
}
