package ru.rien.bot.services;

import com.sun.istack.Nullable;
import org.springframework.stereotype.Service;
import ru.rien.bot.entity.PrivatkaEntity;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.repository.PrivatkaRepository;

import java.util.List;

@Service
public class PrivatkaService {

    private final PrivatkaRepository privatkaRepository;


    public PrivatkaService(PrivatkaRepository privatkaRepository) {
        this.privatkaRepository = privatkaRepository;
    }

    @Nullable
    public List<PrivatkaEntity> findByGuild(GuildWrapper guildWrapper){
        return privatkaRepository.findByGuildEntity(guildWrapper.getGuildEntity());
    }

    @Nullable
    public List<PrivatkaEntity> findAll(){
        return privatkaRepository.findAll();
    }

    @Nullable
    public PrivatkaEntity findbyChannelId(long channel_id, GuildWrapper guildWrapper){
        return privatkaRepository.findByVchannelidAndGuildEntity(channel_id, guildWrapper.getGuildEntity());
    }

    public void createPrivatka(long channel_id, GuildWrapper guildWrapper){
        PrivatkaEntity privatkaEntity = new PrivatkaEntity()
                .setGuildEntity(guildWrapper.getGuildEntity())
                .setVchannelid(channel_id);
        privatkaRepository.save(privatkaEntity);
    }

    public void delete(PrivatkaEntity privatkaEntity){
        privatkaRepository.delete(privatkaEntity);
    }
}
