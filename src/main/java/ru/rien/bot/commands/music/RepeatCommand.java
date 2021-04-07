package ru.rien.bot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.stereotype.Component;
import ru.rien.bot.api.music.player.Player;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RepeatCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        TextChannel channel = event.getChannel();
        Player player = ModuleDsBot.getInstance().getMusicManager().getPlayer(channel.getGuild().getId());
        if (player.getPlayingTrack() == null) {
            MessageUtils.sendErrorMessage("Невозможно поставить трек на повтор!", channel);
        } else {
            Queue<Track> queue = new ConcurrentLinkedQueue<>();
            queue.add(player.getPlayingTrack().makeClone());
            queue.addAll(player.getPlaylist());
            player.getPlaylist().clear();
            player.getPlaylist().addAll(queue);
            channel.sendMessage(new EmbedBuilder().setColor(Color.green).setDescription("Повтор трека включен").build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "repeat";
    }

    @Override
    public String getDescription(GuildWrapper guildWrapper) {
        return "Повторяет песню";
    }

    @Override
    public String getUsage(GuildWrapper guildWrapper) {
        return "{%}repeat - Повтор песни";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public Permission getPermission() {
        return Permission.REPEAT_COMMAND;
    }
}
