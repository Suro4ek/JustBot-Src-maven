package ru.rien.bot.modules.dsBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import ru.rien.bot.api.music.player.Player;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.commands.music.SkipCommand;
import ru.rien.bot.commands.music.SongCommand;
import ru.rien.bot.objects.Getters;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.utils.FormatUtils;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.votes.VoteUtil;

import java.util.Queue;

public class PlayerListener extends AudioEventAdapter {

    private Player player;

    public PlayerListener(Player player) {
        this.player = player;
    }

    @Override
    public void onTrackEnd(AudioPlayer aplayer, AudioTrack atrack, AudioTrackEndReason reason) {
        GuildWrapper wrapper = JustBotManager.instance().getGuild(player.getGuildId());

        if (wrapper == null) return;

        // No song on next
        if (player.getPlaylist().isEmpty()) {
            JustBotManager.instance().getLastActive().put(Long.parseLong(player.getGuildId()), System.currentTimeMillis());
        }

        VoteUtil.remove(SkipCommand.getSkipUUID(), wrapper.getGuild());
    }

    @Override
    public void onTrackStart(AudioPlayer aplayer, AudioTrack atrack) {
        JustBotManager.instance().getLastActive().remove(Long.parseLong(player.getGuildId()));

        GuildWrapper wrapper = JustBotManager.instance().getGuild(player.getGuildId());
        if (wrapper.getMusicAnnounceChannelId() != null) {
            TextChannel c = Getters.getChannelById(wrapper.getMusicAnnounceChannelId());
            if (c != null) {
                if (c.getGuild().getSelfMember().hasPermission(c,
                        Permission.MESSAGE_EMBED_LINKS,
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_WRITE)) {
                    Track track = player.getPlayingTrack();
                    Queue<Track> playlist = player.getPlaylist();
                    c.sendMessage(MessageUtils.getEmbed()
                            .addField(wrapper.getMessage("SONG_NOW_PLAYING"), SongCommand.getLink(track), false)
                            .addField(wrapper.getMessage("SONG_DURATION"), FormatUtils
                                    .formatDuration(track.getTrack().getDuration()), false)
                            .addField(wrapper.getMessage("SONG_REQUESTER"),
                                    String.format("<@!%s>", track.getMeta()
                                            .get("requester")), false)
                            .addField(wrapper.getMessage("SONG_NEXT"), playlist.isEmpty() ? wrapper.getMessage("SONG_NOTHING") :
                                    SongCommand.getLink(playlist.peek()), false)
                            .setImage("https://img.youtube.com/vi/" + track.getTrack().getIdentifier() + "/hqdefault.jpg")
                            .build()).queue();
                } else {
                    wrapper.setMusicAnnounceChannelId(null);
                }
            } else {
                wrapper.setMusicAnnounceChannelId(null);
            }
        }
    }

}
