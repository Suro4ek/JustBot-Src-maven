package ru.rien.bot.music.extractors;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import ru.rien.bot.api.music.player.Player;
import ru.rien.bot.api.music.player.Playlist;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.utils.MessageUtils;

import java.util.ArrayList;

public class SavedPlaylistExtractor implements Extractor {

    @Override
    public Class<? extends AudioSourceManager> getSourceManagerClass() {
        return YoutubeAudioSourceManager.class;
    }

    @Override
    public void process(String input, Player player, InteractionHook message, User user) throws Exception {
        input = input.substring(input.indexOf('\u200B') + 1).replaceAll("\\[? ?]?", "");
        int i = 0;
        ArrayList<Track> playlist = new ArrayList<>();
        for (String s : input.split(",")) {
            String url = YouTubeExtractor.WATCH_URL + s;
//            Document doc;
//            try {
//                doc = Jsoup.connect(url).get();
//            } catch (Exception e) {
//                continue;
//            }
//            if (!doc.title().endsWith("YouTube") || doc.title().equals("YouTube")) {
//                continue;
//            }
            try {
                Track track = new Track((AudioTrack) player.resolve(url));
                track.getMeta().put("requester", user.getId());
                track.getMeta().put("guildId", player.getGuildId());
                playlist.add(track);
                if (playlist.size() == 10) {
                    player.queue(new Playlist(playlist));
                    playlist.clear();
                }
                i++;
            } catch (FriendlyException ignored) {
            }
        }
        if (!playlist.isEmpty()) {
            player.queue(new Playlist(playlist));
        }
        message.sendMessageEmbeds(MessageUtils.getEmbed(user)
                .setDescription(String.format("*Загружено %s песен!*", i)).build()).queue();
    }

    @Override
    public boolean valid(String input) {
        return input.matches(".+\u200B\\[(.{11}(, )?)+]");
    }

    @Override
    public AudioSourceManager newSourceManagerInstance() throws Exception {
        YoutubeAudioSourceManager manager = new YoutubeAudioSourceManager();
        manager.setPlaylistPageCount(100);
        return manager;
    }
}