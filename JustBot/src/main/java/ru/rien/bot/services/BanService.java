package ru.rien.bot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rien.bot.entity.BanEntity;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.repository.BanRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class BanService {

    private final BanRepository banRepository;

    public BanService(BanRepository banRepository) {
        this.banRepository = banRepository;
    }


    public List<BanEntity> gettimeend(){
        return banRepository.findByExpiresonBefore(LocalDateTime.now());
    }

    public void delete(BanEntity banEntity){
        banRepository.delete(banEntity);
    }

    public void ban(long banned_by, long banned, GuildEntity guildEntity, long expires_on, String cause){
        BanEntity banEntity = new BanEntity()
                .setGuildEntity(guildEntity)
                .setBanned(banned)
                .setBannedby(banned_by)
                .setTime(LocalDateTime.now())
                .setExpireson(new Timestamp(new Date().getTime()+expires_on).toLocalDateTime())
                .setCause(cause);
        banRepository.save(banEntity);
    }

}
