package ru.rien.bot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "discord_ban")
@Accessors(chain = true)
@NoArgsConstructor
public class BanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;

    @Column(name = "banned", nullable = false)
    private long banned;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guildEntity;

    @Column(name = "banned_by", nullable = false)
    private long bannedby;

    @Column(name = "cause", nullable = true)
    private String cause;

    @Column(name = "time", nullable = false)
    private LocalDateTime time = LocalDateTime.now();

    @Column(name = "expires_on", nullable = false)
    private LocalDateTime expireson;
}
