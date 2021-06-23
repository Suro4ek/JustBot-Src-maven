package ru.rien.bot.commands.admin.settings;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import ru.rien.bot.commands.admin.settings.blacklist.BlackListGroupCommand;
import ru.rien.bot.modules.command.*;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SettingsCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
//        String[] args= event.getArgs();
//        TextChannel channel = event.getChannel();
//        User sender = event.getSender();
//        GuildWrapper guild = event.getGuild();
//        if (args[0].equalsIgnoreCase("list")) {
//            channel.sendMessage(MessageUtils.getEmbed(sender).setTitle("Настройки сервера")
//                    .setDescription(String.format(
//                            "`Выключенные команды` - %b\n" +
//                                    "`Заблокированные команды` - %s\n" +
//                            guild.getSettings().shouldDeleteCommands(),
//                            getBlackListedCommands(guild))
//                    ).build()).queue();
//        } else if (args[0].equalsIgnoreCase("blacklist")) {
//            if (args.length >= 3) {
//                if (args[1].equalsIgnoreCase("add")) {
//                    TextChannel tc = GuildUtils.getChannel(args[2], guild);
//                    if (tc != null) {
//                        guild.getSettings().getChannelBlacklist().add(tc.getIdLong());
//                        MessageUtils.sendSuccessMessage("Добалвен " + tc.getAsMention() + " в черный список!",
//                                channel);
//                        return;
//                    }
//                    User user = GuildUtils.getUser(args[2], guild.getGuildId());
//                    if (user != null) {
//                        guild.getSettings().getUserBlacklist().add(user.getIdLong());
//                        MessageUtils.sendSuccessMessage("Добавлен " + user.getAsMention() + " в черный список!",
//                                channel);
//                        return;
//                    }
//                    Command command = ModuleCommand.getInstance().getCommand(args[2], null);
//                    if(command != null){
//                        guild.getSettings().getBlacklistCommands().add(command);
//                        MessageUtils.sendSuccessMessage("Добавлен " + args[2] + " в черный список!",
//                                channel);
//                        return;
//                    }
//                    MessageUtils.sendWarningMessage("Недопустимый канал или пользователь! Попробуйте id, если вы уверены, что такое вообще существует", channel);
//                } else if (args[1].equalsIgnoreCase("remove")) {
//                    TextChannel tc = GuildUtils.getChannel(args[2], guild);
//                    if (tc != null) {
//                        if (!guild.getSettings().getChannelBlacklist().contains(tc.getIdLong())) {
//                            MessageUtils.sendWarningMessage("Канал уже в списке!", channel);
//                            return;
//                        }
//                        guild.getSettings().getChannelBlacklist().remove(tc.getIdLong());
//                        MessageUtils.sendSuccessMessage("Удален " + tc.getAsMention() + " с списка!",
//                                channel);
//                        return;
//                    }
//                    User user = GuildUtils.getUser(args[2], guild.getGuildId());
//                    if (user != null) {
//                        if (!guild.getSettings().getUserBlacklist().contains(user.getIdLong())) {
//                            MessageUtils.sendWarningMessage("Пользователь в списке!", channel);
//                            return;
//                        }
//                        guild.getSettings().getUserBlacklist().remove(user.getIdLong());
//                        MessageUtils.sendSuccessMessage("Удален " + user.getAsMention() + " с списка!",
//                                channel);
//                        return;
//                    }
//                    MessageUtils.sendWarningMessage("Недопустимый канал или пользователь! Попробуйте id, если вы уверены, что такое вообще существует", channel);
//                } else
//                    MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
//            } else
//                MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
//        }
//        MessageUtils.sendUsage(this,event.getGuild(),event.getChannel(),event.getSender(),args);
    }

    private String getBlackListedCommands(GuildWrapper wrapper) {
        if (wrapper.getSettings().getBlacklistCommands().isEmpty())
            return "Нет заблокированных команд!";
        return String.join(", ", wrapper.getSettings().getBlacklistCommands());
    }

    @Override
    public String getCommand() {
        return "settings";
    }

//    @Override
//    public String getDescription(GuildWrapper guild) {
//
//    }
//
//    @Override
//    public String getUsage(GuildWrapper guild) {
//        return "{%}settings - Список команд\n" +
//                "";
//    }

    @Override
    public String getDescription(Language language) {
        return "Настройки бота";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public List<SubCommandGroups> getSubCommandGruops() {
        return Lists.newArrayList(new BlackListGroupCommand());
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }

}
