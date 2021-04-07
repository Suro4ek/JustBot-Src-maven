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
public class SetNsfwCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        GuildWrapper guild = event.getGuild();
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        guild.setNswf(channel.getIdLong());
        channel.sendMessage(MessageUtils.getEmbed(sender)
                .setDescription(Language.getLanguage(0).getMessage("ADMIN_SET_NSFW")).build())
                .queue();
        System.out.println(Language.getLanguage(0).getMessage("ADMIN_SET_NSFW"));
    }

    @Override
    public String getCommand() {
        return "nsfw";
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
        return new String[]{"setnsfw"};
    }
}