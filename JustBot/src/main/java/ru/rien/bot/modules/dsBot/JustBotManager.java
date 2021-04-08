package ru.rien.bot.modules.dsBot;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import ru.rien.bot.entity.GroupEntity;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.objects.GuildWrapperLoader;
import ru.rien.bot.permission.Group;
import ru.rien.bot.permission.PerGuildPermissions;
import ru.rien.bot.services.GuildService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class JustBotManager {

    private final ModuleDsBot moduleDsBot;
    private GuildService guildService;

    private static JustBotManager instance;

    public JustBotManager(ModuleDsBot moduleDsBot, GuildService guildService) {
        instance = this;
        this.moduleDsBot = moduleDsBot;
        this.guildService = guildService;
    }

    // Command - reason
    private Map<String, String> disabledCommands = new ConcurrentHashMap<>();

    private Map<Long, Long> lastActive = new ConcurrentHashMap<>();
    private GuildWrapperLoader guildWrapperLoader = new GuildWrapperLoader();

    private LoadingCache<String, GuildWrapper> guilds;
    private final long GUILD_EXPIRE = TimeUnit.MINUTES.toMillis(15);



    public static JustBotManager instance() {
        return instance;
    }

    public void executeCreations() {
        initGuildSaving();
    }

    private void initGuildSaving() {
        guilds = CacheBuilder.newBuilder()
                .expireAfterAccess(GUILD_EXPIRE, TimeUnit.MILLISECONDS)
                .removalListener((RemovalListener<String, GuildWrapper>) removalNotification -> saveGuild(removalNotification.getKey(), removalNotification.getValue()))
                .build(guildWrapperLoader);
    }

    public void saveGuild(String guildId, GuildWrapper guildWrapper) {
        GuildEntity guildEntity = guildService.findById(Long.parseLong(guildId));
        if(guildEntity != null) {
            if (guildWrapper.getGuildEntity().getBlockcommads() == null) {
                for (Command command : guildWrapper.getSettings().getBlacklistCommands()) {
                    List<String> s = new ArrayList<>();
                    s.add(command.getCommand());
                    guildWrapper.getGuildEntity().setBlockcommads(s);
                }
            } else {
                guildWrapper.getGuildEntity().setBlockcommads(guildWrapper.getSettings().getBlacklistCommands().stream().map(Command::getCommand).collect(Collectors.toList()));
            }

            PerGuildPermissions permissions = guildWrapper.getPermissions();
            for (Group group : permissions.getGroups()) {
                GroupEntity groupEntity = guildService.findGroupById(group.getId());
                if(groupEntity == null){
                    groupEntity = new GroupEntity();
                }
                groupEntity.setGuildEntity(guildEntity);
                groupEntity.setName(group.getName());
                groupEntity.setRole_id(Long.parseLong(group.getRoleId()));
                groupEntity.setPermisisons(new ArrayList<>(group.getPermissions()));
                guildService.savegroup(groupEntity);
            }
            guildService.update(guildWrapper.getGuildEntity(),guildEntity);
        }else{
            guildEntity = guildWrapper.getGuildEntity();
            guildService.save(guildEntity);
        }
    }

    public synchronized GuildWrapper getGuild(String id) {
        if (guilds == null) return null; //This is if it's ran before even being loaded
        try {
            return guilds.get(id);
        } catch (ExecutionException e) {
            moduleDsBot.getLogger().error("Failed to load guild from cache!", e);
            return null;
        }
    }

    public GuildWrapper getGuildNoCache(String id) {
        if (guilds == null) return null; //This is if it's ran before even being loaded
        guilds.invalidate(id);
        try {
            return guilds.get(id);
        } catch (ExecutionException e) {
            moduleDsBot.getLogger().error("Failed to load guild from cache!", e);
            return null;
        }
    }

    public LoadingCache<String, GuildWrapper> getGuilds() {
        return guilds;
    }

    public boolean isCommandDisabled(String command) {
        return disabledCommands.containsKey(command);
    }

    public String getDisabledCommandReason(String command) {
        return this.disabledCommands.get(command);
    }

    public boolean toggleCommand(String command, String reason) {
        return disabledCommands.containsKey(command) ? disabledCommands.remove(command) != null :
                disabledCommands.put(command, reason) != null;
    }

    public Map<String, String> getDisabledCommands() {
        return disabledCommands;
    }

    public GuildWrapperLoader getGuildWrapperLoader() {
        return guildWrapperLoader;
    }

    public Map<Long, Long> getLastActive() {
        return lastActive;
    }
}