package ru.rien.bot.objects;

import com.google.common.cache.CacheLoader;
import ru.rien.bot.entity.GroupEntity;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.ModuleCommand;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.permission.Group;
import ru.rien.bot.services.GuildService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class GuildWrapperLoader extends CacheLoader<String, GuildWrapper> {

    private final GuildService guildService = ModuleDsBot.getInstance().getGuildService();

    private List<Long> loadTimes = new CopyOnWriteArrayList<>();

    public static final char[] ALLOWED_SPECIAL_CHARACTERS = {'$', '_', ' ', '&', '%', '£', '!', '*', '@', '#', ':'};
    public static final Pattern ALLOWED_CHARS_REGEX = Pattern.compile("[\\w" + new String(ALLOWED_SPECIAL_CHARACTERS) + "\\p{Ll}\\p{Lu}\\p{Lt}\\p{Lm}\\p{Lo}]{3,32}");


    @Override
    @ParametersAreNonnullByDefault
    public GuildWrapper load(String id) {
        long start = System.currentTimeMillis();
        GuildEntity guildEntity = guildService.findById(Long.parseLong(id));
        GuildWrapper wrapper;
        if(guildEntity != null){
            wrapper = new GuildWrapper(String.valueOf(guildEntity.getGuildid()));
            wrapper.setGuildEntity(guildEntity);
            wrapper.setPrefix(guildEntity.getPrefix());
            wrapper.setNswf(guildEntity.getNswfid());
            if(guildEntity.getBlockcommads() != null) {
                for (String cmd : guildEntity.getBlockcommads()) {
                    Command command = ModuleCommand.getInstance().getCommand(cmd, null);
                    wrapper.getSettings().addBlackListCommands(command);
                }
            }
            List<GroupEntity> groupEntities = guildService.getGroups(guildEntity);
            for (GroupEntity groupEntity : groupEntities){
                if(wrapper.getPermissions().addGroup(groupEntity.getName())) {
                    Group group = wrapper.getPermissions().getGroup(groupEntity.getName());
                    group.setId(groupEntity.getId());
                    group.setDef(groupEntity.isDef());
                    group.linkRole(String.valueOf(groupEntity.getRole_id()));
                    group.addPermission(groupEntity.getPermisisons());
                }
            }
//            List<GroupEntity> groupEntity = AsyncDB.getDaoFactory().getGroupDao().findByGuild(guildEntity);
//            for (GroupEntity entity : groupEntity) {
//                if (wrapper.getPermissions().addGroup(entity.getName())) {
//                    Group group = wrapper.getPermissions().getGroup(entity.getName());
//                    List<GroupPermissions> groupPermissions = AsyncDB.getDaoFactory().getGroupPermDao().findByGroup(entity);
//                    groupPermissions.forEach(groupPermissions1 -> group.addPermission(groupPermissions1.getPermission()));
//                }
//            }
//            if(!guildEntity.getChannelBlacklist().isEmpty()) {
//                for (String s : Collections.singleton(guildEntity.getChannelBlacklist())) {
//                    wrapper.getSettings().addChannelToBlacklist(Long.parseLong(s));
//                }
//            }
//            wrapper.setMusicAnnounceChannelId(guildEntity.getMusicid().toString());
        }else{
            wrapper = new GuildWrapper(id);
            guildEntity = new GuildEntity().setPrefix(wrapper.getPrefix())
                .setGuildid(Long.parseLong(id))
                .setMusicid(null);
            wrapper.setGuildEntity(guildEntity);
            guildService.save(guildEntity);
        }

        long total = (System.currentTimeMillis() - start);
        loadTimes.add(total);

        return wrapper;
    }


}