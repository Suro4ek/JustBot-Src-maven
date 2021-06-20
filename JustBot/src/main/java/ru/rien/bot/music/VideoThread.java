package ru.rien.bot.music;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyActionImpl;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.music.extractors.*;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.utils.MessageUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VideoThread extends Thread {

    private static PlayerManager manager;
    private static final List<Class<? extends Extractor>> extractors = Arrays.asList(YouTubeExtractor.class,
            SavedPlaylistExtractor.class, RandomExtractor.class);
    private static final Set<Class<? extends AudioSourceManager>> managers = new HashSet<>();
    public static final ThreadGroup VIDEO_THREADS = new ThreadGroup("Video Threads");
    private User user;
    private InteractionHook channel;
    private GuildWrapper guildWrapper;
    private String url;
    private Extractor extractor;

    private VideoThread() {
        if (manager == null)
            manager = ModuleDsBot.getInstance().getMusicManager();
        setName("Video Thread " + VIDEO_THREADS.activeCount());
    }

    @Override
    public void run() {
        try {
            if (extractor == null)
                for (Class<? extends Extractor> clazz : extractors) {
                    Extractor extractor = clazz.newInstance();
                    if (!extractor.valid(url))
                        continue;
                    this.extractor = extractor;
                    break;
                }
            if (extractor == null) {
                channel.sendMessage("Не смог найти").queue();
                return;
            }
            if (managers.add(extractor.getSourceManagerClass()))
                manager.getManager().registerSourceManager(extractor.newSourceManagerInstance());
            extractor.process(url, manager.getPlayer(guildWrapper.getGuild().getId()), channel, user);
        } catch (Exception e) {
            ModuleDsBot.getInstance().getLogger().warn(("Не удалось инициализировать экстрактор для '{}'. Guild ID: " + guildWrapper.getGuild().getId()).replace("{}", url), e);
//            BotMain.reportError(channel, "Something went wrong while searching for the video!", e);
        }
    }

    @Override
    public void start() {
        if (url == null)
            throw new IllegalStateException("URL-Адрес не установлен!");
        super.start();
    }

    public static VideoThread getThread(String url, InteractionHook channel, GuildWrapper guildWrapper, User user) {
        VideoThread thread = new VideoThread();
        thread.url = url;
        thread.channel = channel;
        thread.guildWrapper = guildWrapper;
        thread.user = user;
        return thread;
    }

    public static VideoThread getSearchThread(String term, InteractionHook channel, GuildWrapper guildWrapper, User user) {
        VideoThread thread = new VideoThread();
        thread.url = term;
        thread.channel = channel;
        thread.guildWrapper = guildWrapper;
        thread.user = user;
        thread.extractor = new YouTubeSearchExtractor();
        return thread;
    }
}