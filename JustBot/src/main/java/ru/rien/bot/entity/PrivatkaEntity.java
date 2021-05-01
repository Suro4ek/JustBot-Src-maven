package ru.rien.bot.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "dsbot_privatka")
public class PrivatkaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guildEntity;

    @Column(name = "channel_id", nullable = false)
    private Long vchannelid;

    @Column(name = "owner_id", nullable = false)
    private Long ownerid;

}
