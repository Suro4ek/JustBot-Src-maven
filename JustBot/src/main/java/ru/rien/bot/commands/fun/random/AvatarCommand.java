package ru.rien.bot.commands.fun.random;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

import java.awt.*;
import java.util.List;

@Component
public class AvatarCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        User user = event.getSender();
        TextChannel channel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        User sender = event.getSender();
        List<OptionMapping> optionMappings = event.getOptionMappings();
        if (optionMappings.size() > 0)
            user = optionMappings.get(0).getAsUser();
        if (user != null) {
            if (!user.getId().equals(sender.getId()) && !getPermissions(channel).hasPermission(event.getMember(), Permission.AVATAR_OTHER)) {
                MessageUtils.sendErrorMessage("У вас нет прав `" + Permission.AVATAR_OTHER + "` ",
                        replyAction);
                return;
            }
            event.getEvent().replyEmbeds(MessageUtils.getEmbed(sender).setColor(Color.cyan).setAuthor(user.getName(), null, null)
                    .setImage(user.getEffectiveAvatarUrl()).build()).setEphemeral(true).queue();
        } else
            MessageUtils.sendErrorMessage("Пользователь не найден!", replyAction.setEphemeral(true));
    }

    @Override
    public String getCommand() {
        return "avatar";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return "Получить аватарку участника сервера";
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}avatar [пользователь] - Получить аватарку участника сервера";
//    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.USER, "user", "get avatar user")};
    }

    @Override
    public Permission getPermission() {
        return Permission.AVATAR_COMMAND;
    }
}
