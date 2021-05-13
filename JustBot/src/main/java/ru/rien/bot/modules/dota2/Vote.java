package ru.rien.bot.modules.dota2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Vote {

    public ArrayList<Long> wins = new ArrayList<>();
    public ArrayList<Long> loses = new ArrayList<>();
    public Long guildid;
    public Long channelid;
    public long start_time = new Date().getTime();

    public Vote(Long guildid,Long channelid){
        this.guildid = guildid;
        this.channelid = channelid;
    }

    public ArrayList<Long> getLoses() {
        return loses;
    }

    public Long getChannelid() {
        return channelid;
    }

    public ArrayList<Long> getWins() {
        return wins;
    }

    public Long getGuildid() {
        return guildid;
    }
}
