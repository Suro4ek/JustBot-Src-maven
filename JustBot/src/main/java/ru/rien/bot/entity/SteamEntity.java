package ru.rien.bot.entity;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "dsbot_steam")
@NoArgsConstructor
@Accessors(chain = true)
public class SteamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guildEntity;

    @Column(name = "steam_id", nullable = false)
    private Long steamid = 0L;

    @Column(name = "discord_id", nullable = false)
    private Long discordid;

    @Column(name = "score", nullable = false)
    private Long score = 0L;
}