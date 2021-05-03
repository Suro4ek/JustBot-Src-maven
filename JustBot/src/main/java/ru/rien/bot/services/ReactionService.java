package ru.rien.bot.services;

import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rien.bot.entity.ReactionEntity;
import ru.rien.bot.objects.GuildWrapper;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ReactionService {

    @Autowired
    public ru.rien.bot.repository.ReactionRepository reactionRepository;

    public void createMessage(Long messageid, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = new ReactionEntity()
                .setGuildEntity(guildWrapper.getGuildEntity())
                .setMessageid(messageid)
                .setRoles(new ArrayList<>());
        reactionRepository.save(reactionEntity);

    }

    public void addReactionRole(Long message_id,Long reaction, Long role, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = reactionRepository.findByMessageidAndGuildEntity(message_id,guildWrapper.getGuildEntity());
        reactionEntity.getRoles().add(reaction + " " + role);
        reactionRepository.save(reactionEntity);
    }

    public void removeReactionRole(Long message_id, Long reaction, Long role, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = reactionRepository.findByMessageidAndGuildEntity(message_id, guildWrapper.getGuildEntity());
        reactionEntity.getRoles().remove(reaction + " " + role);
        reactionRepository.save(reactionEntity);
    }

    public HashMap<Long, Long> getReactionRoles(Long message_id, GuildWrapper guildWrapper){
        HashMap<Long, Long> roles = new HashMap<>();
        ReactionEntity reactionEntity = reactionRepository.findByMessageidAndGuildEntity(message_id, guildWrapper.getGuildEntity());
        reactionEntity.getRoles().forEach(s -> {
                Long reaction = Long.parseLong(s.split(" ")[0]);
                Long role = Long.parseLong(s.split(" ")[1]);
                roles.put(reaction, role);
        });
        return roles;
    }

    public boolean checkMessageRole(Long message_id, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = reactionRepository.findByMessageidAndGuildEntity(message_id, guildWrapper.getGuildEntity());
        return reactionEntity != null;
    }
}
