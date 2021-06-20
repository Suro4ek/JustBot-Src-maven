package ru.rien.bot.commands.admin;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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
        GuildWrapper guild = event.getGuild();
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        guild.setNswf(channel.getIdLong());
        event.getEvent().replyEmbeds(MessageUtils.getEmbed(sender)
                .setDescription(Language.getLanguage(0).getMessage("ADMIN_SET_NSFW")).build())
                .setEphemeral(true).queue();
    }

    @Override
    public String getCommand() {
        return "nsfw";
    }

    @Override
    public String getDescription(Language guild) {
        return guild.getMessage("NSFW_DESCRIPTION");
    }

//    @Override
//    public String getUsage(GuildWrapper guild) {
//        return Language.getLanguage(guild.getLang()).getMessage("NSFW_USAGE");
//    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }

    @Override
    public String[] getAliases() {
        return new String[]{"setnsfw"};
    }
}