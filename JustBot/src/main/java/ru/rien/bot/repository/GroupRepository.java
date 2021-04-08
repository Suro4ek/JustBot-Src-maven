package ru.rien.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rien.bot.entity.GroupEntity;
import ru.rien.bot.entity.GuildEntity;

import java.util.List;

@Repository
@Transactional
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    List<GroupEntity> findByGuildEntity(GuildEntity guildEntity);
}

