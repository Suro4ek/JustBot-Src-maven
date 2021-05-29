package ru.rien.bot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.music.extractors.YouTubeExtractor;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.pagination.PagedEmbedBuilder;
import ru.rien.bot.utils.pagination.PaginationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class QueueCommand implements Command {

    private final ModuleDsBot moduleDsBot;

    public QueueCommand(ModuleDsBot moduleDsBot) {
        this.moduleDsBot = moduleDsBot;
    }

    @Override
    public void execute(CommandEvent event) {
        Message message = event.getMessage();
        String[] args = event.getArgs();
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        GuildWrapper guild = event.getGuild();
        User sender = event.getSender();
        PlayerManager manager = moduleDsBot.getMusicManager();
        if (args.length < 1 || args.length > 2) {
            send(guild,channel, member);
        } else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("clear")) {
                    if (!this.getPermissions(channel).hasPermission(member, Permission.QUEUE_CLEAR)) {
                        MessageUtils.sendErrorMessage(guild.getMessage("NEED_PERMISSION", Permission.QUEUE_CLEAR), channel, sender);
                        return;
                    }
                    manager.getPlayer(channel.getGuild().getId()).getPlaylist().clear();
                    channel.sendMessage(guild.getMessage("QUEUE_CLEAN")).queue();
                } else if (args[0].equalsIgnoreCase("remove")) {
                    MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
                } else if (args[0].equalsIgnoreCase("here")) {
                    send(guild,channel, member);
                } else {
                    MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
                }
            } else {
                if (args[0].equalsIgnoreCase("remove")) {
                    int number;
                    try {
                        number = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        MessageUtils.sendErrorMessage(guild.getMessage("NOT_NUMBER"), channel);
                        return;
                    }

                    Queue<Track> queue = manager.getPlayer(channel.getGuild().getId()).getPlaylist();

                    if (number < 1 || number > queue.size()) {
                        MessageUtils
                                .sendErrorMessage(guild.getMessage("QUEUE_INDEX",queue
                                        .size()), channel);
                        return;
                    }

                    List<Track> playlist = new ArrayList<>(queue);
                    playlist.remove(number - 1);
                    queue.clear();
                    queue.addAll(playlist);

                    channel.sendMessage(MessageUtils.getEmbed(sender)
                            .setDescription(guild.getMessage("QUEUE_DELETE_LIST", number))
                            .build()).queue();
                }
            }
        }
    }

    private void send(GuildWrapper guild,TextChannel channel, Member sender) {
        PlayerManager manager = moduleDsBot.getMusicManager();
        Track currentTrack = manager.getPlayer(channel.getGuild().getId()).getPlayingTrack();

        if (!manager.getPlayer(channel.getGuild().getId()).getPlaylist().isEmpty()
                || currentTrack != null) {
            List<String> songs = new ArrayList<>();
            songs.add(guild.getMessage("QUEUE_NOW_PLAYING",
                    currentTrack.getTrack().getInfo().title,
                    YouTubeExtractor.WATCH_URL + currentTrack.getTrack().getIdentifier(),
                    currentTrack.getMeta().get("requester")));
            AtomicInteger i = new AtomicInteger(1);
            manager.getPlayer(channel.getGuild().getId()).getPlaylist().forEach(track ->
                    songs.add(guild.getMessage("QUEUE_INC",i.getAndIncrement(),
                            track.getTrack().getInfo().title,
                            YouTubeExtractor.WATCH_URL + track.getTrack().getIdentifier(),
                            track.getMeta().get("requester"))));
            PagedEmbedBuilder pe = new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(songs.stream()
                    // 21 for 10 per page. 2 new lines per song and 1 more because it's annoying
                    .collect(Collectors.joining("\n")) + "\n", PaginationUtil.SplitMethod.NEW_LINES, 21))
                    .setTitle(guild.getMessage("QUEUE_TITLE"));
            PaginationUtil.sendEmbedPagedMessage(pe.build(), 0, channel, sender.getUser(), ButtonGroupConstants.QUEUE);
        } else {
            MessageUtils.sendErrorMessage(MessageUtils.getEmbed().setDescription(guild.getMessage("QUEUE_NOT_MUSIC")), channel);
        }
    }

    @Override
    public String getCommand() {
        return "queue";
    }

    // TODO: FIX THIS MONSTROSITY
    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("QUEUE_DESCRIPTION");
//                "NOTE: If too many it shows only the amount that can fit. You can use `queue clear` to remove all songs." +
//                " You can use `queue remove #` to remove a song under #.\n" +
//                "To make it not send a DM do `queue here`";
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("QUEUE_USAGE");
    }

    @Override
    public Permission getPermission() {
        return Permission.QUEUE_COMMAND;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"q"};
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}