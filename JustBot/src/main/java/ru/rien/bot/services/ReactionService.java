package ru.rien.bot.services;

import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.ReactionEntity;
import ru.rien.bot.objects.GuildWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ReactionService {

    @Autowired
    public ru.rien.bot.repository.ReactionRepository reactionRepository;

    public void createMessage(Long messageid, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = new ReactionEntity()
                .setGuildEntity(guildWrapper.getGuildEntity())
                .setMessageid(messageid)
                .setRoles(new ArrayList<>());
        guildWrapper.getReactionLoader().getReactionEntities().put(messageid,reactionEntity);
        reactionRepository.save(reactionEntity);

    }

    public void addReactionRole(Long message_id, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = reactionRepository.findByMessageidAndGuildEntity(message_id,guildWrapper.getGuildEntity());
        if(reactionEntity != null) {
            reactionEntity.setRoles(guildWrapper.getReactionLoader().reactionEntities.get(message_id).getRoles());
            reactionRepository.save(reactionEntity);
        }
    }

    public void removeReactionRole(Long message_id, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = reactionRepository.findByMessageidAndGuildEntity(message_id, guildWrapper.getGuildEntity());
        if(reactionEntity != null) {
            reactionEntity.setRoles(guildWrapper.getReactionLoader().reactionEntities.get(message_id).getRoles());
            reactionRepository.save(reactionEntity);
        }
    }

    public void delete(Long message_id, GuildWrapper guildWrapper){
        ReactionEntity reactionEntity = reactionRepository.findByMessageidAndGuildEntity(message_id, guildWrapper.getGuildEntity());
        if(reactionEntity != null)
            reactionRepository.delete(reactionEntity);
    }

    public boolean checkmessage(Long message_id, GuildEntity guildEntity){
        return reactionRepository.findByMessageidAndGuildEntity(message_id, guildEntity) == null;
    }

}
