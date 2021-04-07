package ru.rien.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rien.bot.entity.GuildEntity;

@Repository
@Transactional
public interface GuildReposytory extends JpaRepository<GuildEntity, Long> {

    GuildEntity findByGuildid(Long guildid);

}

