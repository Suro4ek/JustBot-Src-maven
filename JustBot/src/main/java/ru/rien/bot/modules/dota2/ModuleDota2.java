package ru.rien.bot.modules.dota2;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.rien.bot.entity.BanEntity;
import ru.rien.bot.module.ModuleDiscord;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.schedule.JustBotTask;
import ru.rien.bot.utils.MessageUtils;

import java.util.HashMap;
import java.util.List;

@Component
public class ModuleDota2 extends ModuleDiscord {

    public static HashMap<Vote, Long> matchscan = new HashMap<>();
    public ModuleDota2() {
        super("dota2", false);
    }

    @Override
    protected void onEnable() {
        new JustBotTask("CheckDota"){
            @Override
            public void run() {
                matchscan.forEach((vote, aLong) -> {
                    long guildid = vote.guildid;
                    GuildWrapper guildWrapper = getModuleDsBot().getManager().getGuildNoCache(guildid+"");
                    TextChannel textChannel = guildWrapper.getGuild().getTextChannelById(vote.getChannelid());
                    if(textChannel != null) {
                        try {
                            HttpResponse<JsonNode> response = Unirest.get("https://api.opendota.com/api/players/" + aLong + "/matches?limit=1").asJson();
                            JSONObject jsonObject = response.getBody().getArray().getJSONObject(0);
                            long start_time = jsonObject.getLong("start_time");
                            long vote_time = vote.start_time/1000;
                            if(vote_time<start_time || vote_time-600<start_time) {
                                int slot = jsonObject.getInt("player_slot");
                                if (jsonObject.getBoolean("radiant_win") && slot <= 127) {
                                    textChannel.sendMessage("Матч " + jsonObject.getLong("match_id") + " победил свет").queue();
                                } else {
                                    textChannel.sendMessage("Матч " + jsonObject.getLong("match_id") + " победила тьма").queue();
                                }
                                matchscan.remove(vote);
                            }

                        } catch (UnirestException e) {
                            e.printStackTrace();
                        }
//                        matchscan.remove(aLong,vote);
                    }

                });
            }
        }.repeat(0,   5*600* 1000);
    }
}
