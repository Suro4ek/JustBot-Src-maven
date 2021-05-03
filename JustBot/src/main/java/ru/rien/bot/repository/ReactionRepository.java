package ru.rien.bot.repository;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.ReactionEntity;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    @Nullable
    ReactionEntity findByMessageidAndGuildEntity(Long messageid, GuildEntity guildEntity);


}
