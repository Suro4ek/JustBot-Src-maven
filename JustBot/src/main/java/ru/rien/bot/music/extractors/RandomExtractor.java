package ru.rien.bot.music.extractors;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import ru.rien.bot.api.music.player.Player;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.utils.MessageUtils;

public class RandomExtractor implements Extractor {

    @Override
    public Class<? extends AudioSourceManager> getSourceManagerClass() {
        return YoutubeAudioSourceManager.class;
    }

    @Override
    public void process(String input, Player player, InteractionHook message, User user) throws Exception {
        int i = 0;
        for (String s : input.split(",")) {
            try {
                AudioItem probablyATrack = player.resolve(s);
                if (probablyATrack == null)
                    continue;
                Track track = new Track((AudioTrack) probablyATrack);
                track.getMeta().put("requester", user.getId());
                track.getMeta().put("guildId", player.getGuildId());
                player.queue(track);
                i++;
            } catch (FriendlyException ignored) {
            }
        }
        message.sendMessageEmbeds( MessageUtils.getEmbed()
                .setDescription("+ " + i + " случайная песня в плейлист!").build()).queue();
    }

    @Override
    public boolean valid(String input) {
        return input.matches("([^,]{11},)*[^,]{11}");
    }

    @Override
    public AudioSourceManager newSourceManagerInstance() throws Exception {
        YoutubeAudioSourceManager manager = new YoutubeAudioSourceManager();
        manager.setPlaylistPageCount(100);
        return manager;
    }
}