package ru.rien.bot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.SteamEntity;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.repository.SteamRepository;

@Service
public class SteamService {

    @Autowired
    private SteamRepository steamRepository;

    public SteamEntity findDiscordAndGuild(GuildWrapper guildWrapper, long discord_id){
        return steamRepository.findByDiscordidAndGuildEntity(discord_id, guildWrapper.getGuildEntity());
    }

    public void createSteam(long steam_id, long discord_id, GuildWrapper guildWrapper){
        SteamEntity steamEntity = new SteamEntity()
                .setGuildEntity(guildWrapper.getGuildEntity())
                .setDiscordid(discord_id)
                .setSteamid(steam_id);
        steamRepository.save(steamEntity);
    }
    public void createSteam(long discord_id, GuildWrapper guildWrapper){
        SteamEntity steamEntity = new SteamEntity()
                .setGuildEntity(guildWrapper.getGuildEntity())
                .setDiscordid(discord_id);
        steamRepository.save(steamEntity);
    }
    public boolean checkValid(long discord_id, GuildEntity guildEntity){
        return steamRepository.findByDiscordidAndGuildEntity(discord_id, guildEntity) == null;
    }
    public boolean checkValidSteam(long discord_id,GuildEntity guildEntity){
        return  steamRepository.findByDiscordidAndGuildEntity(discord_id, guildEntity).getSteamid() == 0;
    }


}
