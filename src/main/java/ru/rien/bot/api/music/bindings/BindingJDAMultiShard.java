package ru.rien.bot.api.music.bindings;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import ru.rien.bot.api.music.JDAMultiShard;
import ru.rien.bot.api.music.libraries.Library;
import ru.rien.bot.api.music.player.Provider;

import java.nio.ByteBuffer;

public class BindingJDAMultiShard {
    public static Library createLibrary(Object o) {
        JDAMultiShard jda = (JDAMultiShard) o;
        return new Library(o) {
            @Override
            public void setProvider(String guildId, Provider provider) {
                Guild g = jda.getGuild(guildId);
                if(g != null)
                    g.getAudioManager().setSendingHandler(new AudioSendHandler() {
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
                return jda.isValid(guildId);
            }
        };
    }
}