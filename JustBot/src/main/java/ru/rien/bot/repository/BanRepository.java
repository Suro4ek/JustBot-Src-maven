package ru.rien.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rien.bot.entity.BanEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface BanRepository extends JpaRepository<BanEntity, Long> {

    List<BanEntity> findByExpiresonBefore(LocalDateTime time);
}
