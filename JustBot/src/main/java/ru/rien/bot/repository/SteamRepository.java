package ru.rien.bot.repository;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.SteamEntity;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface SteamRepository extends JpaRepository<SteamEntity, Long> {

    @Nullable
    SteamEntity findByDiscordidAndGuildEntity(long discord_id, GuildEntity guildEntity);

    @Nullable
    SteamEntity findBySteamid(long steam_id);
}
