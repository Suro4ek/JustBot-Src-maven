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
@Table(name = "group_guild")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@NoArgsConstructor
@Accessors(chain = true)
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guildEntity;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role_id", nullable = false)
    private long role_id;

    @Column(name = "def", nullable = false)
    private boolean def = false;

    @Type(type = "list-array")
    @Column(
            name = "permisisons",
            columnDefinition = "text[]"
    )
    private List<String> permisisons = new ArrayList<>();
}
