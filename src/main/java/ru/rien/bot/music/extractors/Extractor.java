package ru.rien.bot.music.extractors;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import ru.rien.bot.api.music.player.Player;


public interface Extractor {

    Class<? extends AudioSourceManager> getSourceManagerClass();

    void process(String input, Player player, Message message, User user) throws Exception;

    boolean valid(String input);

    default AudioSourceManager newSourceManagerInstance() throws Exception {
        return getSourceManagerClass().newInstance();
    }
}