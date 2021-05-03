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
@Table(name = "dsbot_reactionrole")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@NoArgsConstructor
@Accessors(chain = true)
public class ReactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guildEntity;

    @Column(name = "message_id", nullable = false)
    private Long messageid;

    @Type(type = "list-array")
    @Column(
            name = "roles",
            columnDefinition = "text[]"
    )
    private List<String> roles = new ArrayList<>();
}
