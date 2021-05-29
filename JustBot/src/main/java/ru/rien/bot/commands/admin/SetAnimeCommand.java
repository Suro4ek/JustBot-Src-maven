package ru.rien.bot.commands.admin;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

@Component
public class SetAnimeCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        GuildWrapper guild = event.getGuild();
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        guild.setAnime(channel.getIdLong());
        channel.sendMessage(MessageUtils.getEmbed(sender)
                .setDescription("Аниме канал установлен").build())
                .queue();
    }

    @Override
    public String getCommand() {
        return "anime";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return Language.getLanguage(guild.getLang()).getMessage("NSFW_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return Language.getLanguage(guild.getLang()).getMessage("NSFW_USAGE");
    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"setanime"};
    }
}
