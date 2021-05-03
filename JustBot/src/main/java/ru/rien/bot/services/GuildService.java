package ru.rien.bot.services;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rien.bot.entity.GroupEntity;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.ReactionEntity;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Group;
import ru.rien.bot.repository.GroupRepository;
import ru.rien.bot.repository.GuildReposytory;
import ru.rien.bot.repository.ReactionRepository;

import java.util.List;

@Service
public class GuildService {
    private final GuildReposytory guildReposytory;
    private final GroupRepository groupRepository;
    private final ReactionRepository reactionRepository;

    public GuildService(GuildReposytory guildReposytory, GroupRepository groupRepository, ReactionRepository reactionRepository) {
        this.guildReposytory = guildReposytory;
        this.groupRepository = groupRepository;
        this.reactionRepository = reactionRepository;
    }

    @Nullable
    public GuildEntity findById(Long id){
        return guildReposytory.findByGuildid(id);
    }

    @Nullable
    public GroupEntity findGroupById(Long id){
        return groupRepository.findById(id).orElse(null);
    }

    public void save(GuildEntity guildEntity){
        guildReposytory.save(guildEntity);
    }

    public void savegroup(GroupEntity groupEntity){
        groupRepository.save(groupEntity);
    }

    public void saveGroups(GuildEntity guildEntity, List<Group> groups){

    }

    public List<ReactionEntity> reactionEntities(GuildEntity guildEntity){
        return reactionRepository.findByGuildEntity(guildEntity);
    }

    public List<GuildEntity> findall(){
        return guildReposytory.findAll();
    }

    public void deleteGuild(GuildWrapper guildWrapper){
        GuildEntity guildEntity = findById(guildWrapper.getGuildIdLong());
        if(guildEntity != null){
            guildReposytory.delete(guildEntity);
        }
    }

    public void update(GuildEntity guildEntity,GuildEntity guildEntity1) {
        guildEntity1.setPrefix(guildEntity.getPrefix());
        guildEntity1.setBlockcommads(guildEntity.getBlockcommads());
        guildEntity1.setNswfid(guildEntity.getNswfid());
        guildEntity1.setCreate_channel_id(guildEntity.getCreate_channel_id());
        guildEntity1.setCategory_id(guildEntity.getCategory_id());
        guildEntity1.setStatsid(guildEntity.getStatsid());
        guildEntity1.setStats(guildEntity.isStats());
        guildReposytory.save(guildEntity1);
    }

    public List<GroupEntity> getGroups(GuildEntity guildEntity){
        List<GroupEntity> groupEntities = groupRepository.findByGuildEntity(guildEntity);
        return groupEntities;
    }
}
