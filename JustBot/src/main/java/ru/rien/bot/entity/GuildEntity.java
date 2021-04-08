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
@Table(name = "dsbot_guild_beta")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@NoArgsConstructor
@Accessors(chain = true)
public class GuildEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;

    @Column(name = "guildid", nullable = false, unique = true)
    private long guildid;

    @Column(name = "prefix", nullable = false)
    private char prefix;

    @Column(name = "volume")
    private Integer volume = 100;

    @Column(name = "musicid", nullable = true)
    private Long musicid;

    @Column(name = "nsfwid", nullable = true)
    private Long nswfid;

    @Column(name = "category_id", nullable = false)
    private long category_id = 0;

    @Column(name = "create_channel_id", nullable = false)
    private long create_channel_id = 0;

    @Column(name = "lang", nullable = false)
    private int lang = 0;

    @Type(type = "list-array")
    @Column(
            name = "blockcommads",
            columnDefinition = "text[]"
    )
    private List<String> blockcommads = new ArrayList<>();


}


