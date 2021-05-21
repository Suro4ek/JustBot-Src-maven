package ru.rien.bot.commands.music;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandException;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SkipTimeCommand implements Command {

    @Autowired
    private static ModuleDsBot moduleDsBot = ModuleDsBot.getInstance();

    private static final Map<Character, Long> timeValue = new HashMap<>();
    private static final List<Character> numbers = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    @Override
    public void execute(CommandEvent event) {
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        GuildWrapper guild = event.getGuild();
        String[] args = event.getArgs();
        event.checkSizeArguments(1);
        PlayerManager manager = moduleDsBot.getMusicManager();
        if (manager.getPlayer(channel.getGuild().getId()).getPlayingTrack() != null) {
            Track track = manager.getPlayer(channel.getGuild().getId()).getPlayingTrack();
            if (!track.getTrack().getInfo().isStream) {
                String experies = args[0];
                long time = this.getTime(experies, event.getSender(), event);
                if (time + track.getTrack().getPosition() < track.getTrack().getDuration()) {
                    track.getTrack().setPosition(track.getTrack().getPosition() + time);
                    MessageUtils.sendInfoMessage("Пропущено на " + experies, event.getChannel(), sender);
                } else {
                    manager.getPlayer(guild.getGuildId()).skip();
                    MessageUtils.sendInfoMessage("Пропущена музыка", event.getChannel(), sender);
                }
            }
        }else{
            MessageUtils.sendErrorMessage("Нельзя пропустить стрим", event.getChannel(),sender);
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
                    this.errorTime(value.toString() + c, sender, event.getChannel());
                    throw new CommandException();
                }

                if (value.length() == 0) {
                    this.errorTime("?" + c, sender, event.getChannel());
                    throw new CommandException();
                }

                time += type * (long) event.getInt(value.toString());
                value = new StringBuilder();
            }
        }

        if (value.length() != 0) {
            this.errorTime(value.toString() + "?", sender, event.getChannel());
            throw new CommandException();
        } else if (time == 0L) {
            this.errorTime(data, sender, event.getChannel());
            throw new CommandException();
        } else {
            return time;
        }
    }

    private void errorTime(String data, User sender, TextChannel channel) {
        MessageUtils.sendErrorMessage("Время задано не корректно", channel);
    }


    @Override
    public String getCommand() {
        return "skiptime";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public Permission getPermission() {
        return Permission.SKIP_TIME_COMMAND;
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
