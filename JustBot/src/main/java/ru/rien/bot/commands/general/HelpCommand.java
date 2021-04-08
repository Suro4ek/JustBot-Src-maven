package ru.rien.bot.commands.general;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.pagination.PagedEmbedBuilder;
import ru.rien.bot.utils.pagination.PaginationList;
import ru.rien.bot.utils.pagination.PaginationUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("Duplicates") // IntelliJ IDEA Ultimate is bitching about it.
public class HelpCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        String[] args = event.getArgs();
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        GuildWrapper guild = event.getGuild();
        Member member = event.getMember();
        if (args.length == 1) {
            CommandType type;
            try {
                type = CommandType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException ignored) {
                channel.sendMessage(MessageUtils.getEmbed(sender).setDescription(guild.getMessage("HELP_NO_CATEGORY")).build()).queue();
                return;
            }

            sendCommands(guild, channel, member, type);
        } else
            sendCommands(channel.getGuild(), channel, sender, event.getMember(), event.getGuild());
    }

    private void sendCommands(Guild guild, TextChannel channel, User sender, Member member, GuildWrapper guildWrapper) {
        List<String> pages = new ArrayList<>();
        for (CommandType c : CommandType.getTypes()) {
            List<String> help = c.getCommands()
                    .stream().filter(cmd -> cmd.getPermission() != null && member != null &&
                            JustBotManager.instance().getGuild(guild.getId())
                                    .getPermissions()
                                    .hasPermission(member, cmd
                                            .getPermission()))
                    .filter(command -> !guildWrapper.getSettings().getBlacklistCommands().contains(command))
                    .map(command -> JustBotManager.instance().getGuild(guild.getId()).getPrefix() + command.getCommand() + " - " + command
                            .getDescription(guildWrapper) + '\n')
                    .collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            sb.append("**").append(c.getName()).append("**\n");
            for (String s : help) {
                if (sb.length() + s.length() > 1024) {
                    pages.add(sb.toString());
                    sb.setLength(0);
                    sb.append("**").append(c).append("**\n");
                }
                sb.append(s);
            }
            if(help.isEmpty()) continue;
            if (sb.toString().trim().isEmpty()) continue;
            pages.add(sb.toString());
        }
        PagedEmbedBuilder<String> builder = new PagedEmbedBuilder<>(new PaginationList<>(pages));
        builder.setColor(Color.CYAN);
        PaginationUtil.sendEmbedPagedMessage(builder.build(), 0, channel, sender, ButtonGroupConstants.HELP);
    }

    public void sendCommands(GuildWrapper guild, TextChannel channel, Member member, CommandType type) {
        List<String> pages = new ArrayList<>();
        List<String> help = type.getCommands()
                .stream().filter(cmd -> getPermissions(channel)
                        .hasPermission(member, cmd.getPermission()))
                .filter(command -> !guild.getSettings().getBlacklistCommands().contains(command))
                .map(command -> guild.getPrefix() + command.getCommand() + " - " + command
                        .getDescription(guild) + '\n')
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (String s : help) {
            if (sb.length() + s.length() > 1024) {
                pages.add(sb.toString());
                sb.setLength(0);
            }
            sb.append(s);
        }
        pages.add(sb.toString());
        PagedEmbedBuilder<String> builder = new PagedEmbedBuilder<>(new PaginationList<>(pages));
        builder.setTitle("***Команды " + type.getName() + "!***")
                .setColor(Color.CYAN);
        PaginationUtil.sendEmbedPagedMessage(builder.build(), 0, channel, member.getUser(), ButtonGroupConstants.HELP);
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"commands"};
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("HELP_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("HELP_USAGE");
    }

    @Override
    public Permission getPermission() {
        return Permission.HELP_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}