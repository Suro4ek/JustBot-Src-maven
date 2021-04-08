package ru.rien.bot.api.music;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class JDAMultiShard {
    private JDA manager;

    public JDAMultiShard(JDA manager){
        this.manager = manager;
    }

    public boolean isValid(String id){
        return manager.getGuildById(id) != null;
    }

    public Guild getGuild(String id) {
        return manager.getGuildById(id);
    }
}