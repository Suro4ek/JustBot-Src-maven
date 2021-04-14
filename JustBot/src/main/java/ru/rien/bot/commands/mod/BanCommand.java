package ru.rien.bot.commands.mod;

import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandException;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.punishment.ModulePunishment;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.GuildUtils;
import ru.rien.bot.utils.MessageUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BanCommand implements Command {

    public final ModulePunishment modulePunishment;

    private static final Map<Character, Long> timeValue = new HashMap<>();
    private static final List<Character> numbers = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    public BanCommand(ModulePunishment modulePunishment) {
        this.modulePunishment = modulePunishment;
    }

    @Override
    public void execute(CommandEvent event) {
        User banned_by = event.getSender();
        String[] args = event.getArgs();
        GuildWrapper guild = event.getGuild();
        event.checkSizeArguments(3);
        User banned = GuildUtils.getUser(args[1], guild.getGuildId());
        String cause = event.getArguments(3);
        String experies = args[2];
        long time = this.getTime(experies, event.getSender(), event);
        if (time >= 1L && time <= 315360000000L) {
            modulePunishment.ban(banned_by, banned, guild, cause, time);
            MessageUtils.sendInfoMessage("Вы забанили " + banned.getName(),event.getChannel(),banned_by);
        }
    }

    private long getTime(String data, User sender, CommandEvent event) {
        long time = 0L;
        char[] chars = data.toCharArray();
        StringBuilder value = new StringBuilder();
        for (char c : chars) {
            if (numbers.contains(c)) {
                value.append(c);
            } else {
                Long type = timeValue.get(c);
                if (type == null) {
                    this.errorTime(value.toString() + c, sender);
                    throw new CommandException();
                }

                if (value.length() == 0) {
                    this.errorTime("?" + c, sender);
                    throw new CommandException();
                }

                time += type * (long) event.getInt(value.toString());
                value = new StringBuilder();
            }
        }

        if (value.length() != 0) {
            this.errorTime(value.toString() + "?", sender);
            throw new CommandException();
        } else if (time == 0L) {
            this.errorTime(data, sender);
            throw new CommandException();
        } else {
            return time;
        }
    }

    @Override
    public String getCommand() {
        return "ban";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Забанить пользователя";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}ban - забанить пользователя";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Permission getPermission() {
        return Permission.BAN_COMMAND;
    }

    private void errorTime(String data, User sender) {
//        Message.sendMessage(sender, "время_некорректно", "{value}", data);
    }

    static {
        timeValue.put('д', 86400000L);
        timeValue.put('Д', 86400000L);
        timeValue.put('d', 86400000L);
        timeValue.put('D', 86400000L);
        timeValue.put('ч', 3600000L);
        timeValue.put('Ч', 3600000L);
        timeValue.put('h', 3600000L);
        timeValue.put('H', 3600000L);
        timeValue.put('м', 60000L);
        timeValue.put('М', 60000L);
        timeValue.put('m', 60000L);
        timeValue.put('M', 60000L);
        timeValue.put('с', 1000L);
        timeValue.put('С', 1000L);
        timeValue.put('s', 1000L);
        timeValue.put('S', 1000L);
    }
}
