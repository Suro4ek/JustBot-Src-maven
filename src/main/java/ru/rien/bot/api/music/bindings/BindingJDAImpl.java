package ru.rien.bot.api.music.bindings;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import ru.rien.bot.api.music.libraries.Library;
import ru.rien.bot.api.music.player.Provider;

import java.nio.ByteBuffer;

public class BindingJDAImpl {
    public static Library createLibrary(Object o) {
        JDA jda = (JDA) o;
        return new Library(o) {
            @Override
            public void setProvider(String guildId, Provider provider) {
                jda.getGuildById(guildId).getAudioManager().setSendingHandler(new AudioSendHandler() {
                    @Override
                    public boolean canProvide() {
                        return provider.isReady();
                    }

                    @Override
                    public ByteBuffer provide20MsAudio() {
                        return ByteBuffer.wrap(provider.provide());
                    }

                    @Override
                    public boolean isOpus() {
                        return true;
                    }
                });
            }

            @Override
            public boolean isValidGuild(String guildId) {
                return jda.getGuildById(guildId) != null;
            }
        };
    }
}