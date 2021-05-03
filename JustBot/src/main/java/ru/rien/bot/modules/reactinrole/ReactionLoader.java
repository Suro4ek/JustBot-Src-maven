package ru.rien.bot.modules.reactinrole;

import ru.rien.bot.entity.ReactionEntity;
import ru.rien.bot.objects.GuildWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReactionLoader {

    public HashMap<Long, ReactionEntity> reactionEntities = new HashMap<>();

    private GuildWrapper guildWrapper;
    public ReactionLoader(GuildWrapper guildWrapper){
        this.guildWrapper = guildWrapper;
    }

    public HashMap<Long, ReactionEntity> getReactionEntities() {
        return reactionEntities;
    }

    public void setReactionEntities(HashMap<Long, ReactionEntity> reactionEntities) {
        this.reactionEntities = reactionEntities;
    }

    public HashMap<String, Long> getReactionEntity(long message_id){
        HashMap<String, Long> roles = new HashMap<>();
        ReactionEntity reactionEntity = reactionEntities.get(message_id);
        if(reactionEntity != null) {
            if (reactionEntity.getRoles() != null) {
                reactionEntity.getRoles().forEach(s -> {
                    String reaction = s.split(" ")[0];
                    Long role = Long.parseLong(s.split(" ")[1]);
                    roles.put(reaction, role);
                });
            }
        }
        return roles;
    }

    public void addReaction(Long message_id, String reaction, Long role){
        if(reactionEntities.get(message_id) != null){
            reactionEntities.get(message_id).getRoles().add(reaction + " "  +role);
        }else{
            List<String> roles = new ArrayList<>();
            roles.add(reaction + " " +  role);
            reactionEntities.put(message_id, new ReactionEntity().setGuildEntity(guildWrapper.getGuildEntity()).setMessageid(message_id).setRoles(roles));
        }
    }

    public void removeReaction(Long message_id, String reaction, Long role){
        if(reactionEntities.get(message_id) != null){
            reactionEntities.get(message_id).getRoles().add(reaction + " "  +role);
        }else{
            reactionEntities.put(message_id, new ReactionEntity().setGuildEntity(guildWrapper.getGuildEntity()).setMessageid(message_id).setRoles(new ArrayList<>()));
        }
    }

}
