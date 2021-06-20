package ru.rien.bot.music.extractors;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.rien.bot.api.music.player.Player;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.WebUtils;

import java.io.IOException;
import java.net.URLEncoder;


public class YouTubeSearchExtractor extends YouTubeExtractor {

    @Override
    public void process(String input, Player player, InteractionHook message, User user) throws Exception {
        Response response = WebUtils.get(new Request.Builder().get().url(String.format("https://www.googleapis.com/youtube/v3/search" +
                        "?q=%s&part=snippet&key=%s&type=video,playlist",
                URLEncoder.encode(input, "UTF-8"), "AIzaSyBM8M39G7DjN7_KuY8iHHwAJaoraGhr8PU")));

        if (response.code() == 403) {
            // \uD83E\uDD15 = :head_bandage:
            message.sendMessageEmbeds( MessageUtils.getEmbed(user)
                    .setDescription("Похоже, мы попали в лимит YouTube API \uD83E\uDD15 "
                            + " Попробуйте поискать позже!").build()).queue();
            response.close();
            return;
        }
        if (!response.isSuccessful()) {
            response.close();
            throw new IOException("Неожиданный код " + response);
        }
        JSONArray results = new JSONObject(response.body().string()).getJSONArray("items");
        response.close();
        String link = null;
        for (Object res : results) {
            if (res instanceof JSONObject) {
                JSONObject result = (JSONObject) res;
                if (!result.getJSONObject("snippet").getString("liveBroadcastContent").contains("none"))
                    continue;
                JSONObject id = result.getJSONObject("id");
                if (id.getString("kind").equals("youtube#playlist")) {
                    link = PLAYLIST_URL + id.getString("playlistId");
                } else {
                    link = WATCH_URL + id.getString("videoId");
                }
                break;
            }
        }
        if (link == null) {
            message.sendMessageEmbeds(MessageUtils.getEmbed(user)
                    .setDescription(String
                            .format("Ничего не смог найти по `%s`", input)).build()).queue();
            return;
        }
        super.process(link, player, message, user);
    }

    @Override
    public AudioSourceManager newSourceManagerInstance() throws Exception {
        YoutubeAudioSourceManager manager = new YoutubeAudioSourceManager();
        manager.setPlaylistPageCount(100);
        return manager;
    }
}