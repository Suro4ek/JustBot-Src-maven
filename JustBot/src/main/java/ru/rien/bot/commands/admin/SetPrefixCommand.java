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
public class SetPrefixCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        GuildWrapper guild = event.getGuild();
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                guild.setPrefix('!');
                MessageUtils.sendInfoMessage(Language.getLanguage(0).getMessage("PREFIX_RESET"), channel, sender);
            } else if (args[0].length() == 1) {
                guild.setPrefix(args[0].charAt(0));
                channel.sendMessage(MessageUtils.getEmbed(sender)
                        .setDescription(Language.getLanguage(0).getMessage("PREFIX_SET",args[0])).build())
                        .queue();
            } else {
                MessageUtils.sendErrorMessage(Language.getLanguage(0).getMessage("PREFIX_MORE_SYMBOLS"), channel, sender);
            }
        } else {
            channel.sendMessage(MessageUtils.getEmbed(sender)
                    .setDescription(Language.getLanguage(0).getMessage("CURRENT_PREFIX",guild.getPrefix())).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "prefix";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return Language.getLanguage(0).getMessage("PREFIX_DESCRIPTION");
    }
    

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return Language.getLanguage(0).getMessage("PREFIX_USAGE");
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
        return new String[]{"setprefix"};
    }
}