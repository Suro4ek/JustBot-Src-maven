package ru.rien.bot.repository;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.PrivatkaEntity;

import java.util.List;

@Repository
@Transactional
public interface PrivatkaRepository extends JpaRepository<PrivatkaEntity, Long> {

    List<PrivatkaEntity> findByGuildEntity(GuildEntity guildEntity);

    @Nullable
    PrivatkaEntity findByVchannelidAndGuildEntity(Long channel_id, GuildEntity guildEntity);
}
