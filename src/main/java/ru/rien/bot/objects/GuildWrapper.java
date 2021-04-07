package ru.rien.bot.objects;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.permission.PerGuildPermissions;
import ru.rien.bot.utils.GuildUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class GuildWrapper {

    public static transient final long DATA_VERSION = 1;
    public final long dataVersion = DATA_VERSION;

    private String guildId;
    private char prefix = '!';
//    private Welcome welcome = new Welcome();
    private PerGuildPermissions permissions = new PerGuildPermissions();
    private Set<String> autoAssignRoles = new HashSet<>();
    private Set<String> selfAssignRoles = new HashSet<>();
    private boolean songnick = false;
    private Long nswf = null;
    // Should be moved to their own manager.
    private boolean blocked = false;
    @Getter
    private int lang = 0;
    private long unBlockTime = -1;
    private GuildEntity guildEntity;
    private String blockReason = null;
    // TODO: Move to Moderation fully - This will be a breaking change so we will basically just refer to the new location
    private String mutedRoleID = null;
    //NINO
    private Map<String, List<String>> warnings = new ConcurrentHashMap<>();
    private Map<String, String> tags = new ConcurrentHashMap<>();
    private String musicAnnounceChannelId = null;
//    private Moderation moderation;
//    private NINO nino = null;
    private GuildSettings settings = null;


    public void setNswf(Long nswf) {
        this.nswf = nswf;
        this.guildEntity.setNswfid(nswf);
    }

    public Long getNswf() {
        return nswf;
    }

    public GuildSettings getSettings() {
        if (settings == null)
            settings = new GuildSettings();
        return settings;
    }

    /**
     * <b>Do not use</b>
     *
     * @param guildId Guild Id of the desired new GuildWrapper
     */
    public GuildWrapper(String guildId) {
        this.guildId = guildId;
    }

    public void setGuildEntity(GuildEntity guildEntity) {
        this.guildEntity = guildEntity;
    }

    public GuildEntity getGuildEntity() {
        return guildEntity;
    }

    public String getMessage(String key, Object ... objects){
        return Language.getLanguage(getLang()).getMessage(key,objects);
    }

    public Guild getGuild() {
        return Getters.getGuildById(guildId);
    }

    public String getGuildId() {
        return this.guildId;
    }

    public long getGuildIdLong() {
        return Long.parseLong(this.guildId);
    }

//    public Welcome getWelcome() {
//        if (welcome == null) {
//            welcome = new Welcome();
//            welcome.setDmEnabled(false);
//            welcome.setGuildEnabled(false);
//        }
//        return this.welcome;
//    }

    public PerGuildPermissions getPermissions() {
        if (permissions == null) {
            permissions = new PerGuildPermissions();
        }
        return permissions;
    }

    public void setPermissions(PerGuildPermissions permissions) {
        this.permissions = permissions;
    }

    public Set<String> getAutoAssignRoles() {
        return this.autoAssignRoles;
    }

    public Set<String> getSelfAssignRoles() {
        return this.selfAssignRoles;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void addBlocked(String reason) {
        blocked = true;
        blockReason = reason;
        unBlockTime = -1; //-1 represents both infinite and unblocked
    }

    public void addBlocked(String reason, long unBlockTime) {
        blocked = true;
        blockReason = reason;
        this.unBlockTime = unBlockTime;
    }

    public void revokeBlock() {
        blocked = false;
        blockReason = "";
        unBlockTime = -1; //-1 represents both infinite and unblocked
    }

    public String getBlockReason() {
        return blockReason;
    }

    public long getUnBlockTime() {
        return unBlockTime;
    }

    public boolean isSongnickEnabled() {
        return songnick;
    }

    public void setSongnick(boolean songnick) {
        this.songnick = songnick;
    }

    @Nullable
    public Role getMutedRole() {
        if (mutedRoleID == null) {
            Role mutedRole =
                    GuildUtils.getRole("Молчун", getGuild()).isEmpty() ? null : GuildUtils.getRole("Молчун", getGuild()).get(0);
            if (mutedRole == null) {
                if (!getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES, Permission.MANAGE_PERMISSIONS))
                    return null;
                try {
                    mutedRole = getGuild().createRole().setName("Молчун").submit().get();
                    if (!getGuild().getSelfMember().getRoles().isEmpty())
                        getGuild().modifyRolePositions().selectPosition(mutedRole)
                                .moveTo(getGuild().getSelfMember().getRoles().get(0).getPosition() - 1).queue();
                    mutedRoleID = mutedRole.getId();
                    handleMuteChannels(mutedRole);
                    return mutedRole;
                } catch (InterruptedException | ExecutionException e) {
                    ModuleDsBot.getInstance().getLogger().error("Не смог создать роль!", e);
                    return null;
                }
            } else {
                mutedRoleID = mutedRole.getId();
                handleMuteChannels(mutedRole);
                return mutedRole;
            }
        } else {
            Role mutedRole = getGuild().getRoleById(mutedRoleID);
            if (mutedRole == null) {
                mutedRoleID = null;
                return getMutedRole();
            } else {
                handleMuteChannels(mutedRole);
                return mutedRole;
            }
        }
    }

    /**
     * This will go through all the channels in a guild, if there is no permission override or it doesn't block message write then deny it.
     *
     * @param muteRole This is the muted role of the server, the role which will have MESSAGE_WRITE denied.
     */
    private void handleMuteChannels(Role muteRole) {
        getGuild().getTextChannels().forEach(channel -> {
            if (!getGuild().getSelfMember().hasPermission(channel, Permission.MANAGE_PERMISSIONS)) return;
            if (channel.getPermissionOverride(muteRole) != null &&
                    !channel.getPermissionOverride(muteRole).getDenied().contains(Permission.MESSAGE_WRITE))
                channel.getPermissionOverride(muteRole).getManager().deny(Permission.MESSAGE_WRITE).queue();
            else if (channel.getPermissionOverride(muteRole) == null)
                channel.createPermissionOverride(muteRole).setDeny(Permission.MESSAGE_WRITE).queue();
        });
    }

//    public ReportManager getReportManager() {
//        if (reportManager == null) reportManager = new ReportManager();
//        return reportManager;
//    }

    public List<String> getUserWarnings(User user) {
        if (warnings == null) warnings = new ConcurrentHashMap<>();
        return warnings.getOrDefault(user.getId(), new ArrayList<>());
    }

    public void addWarning(User user, String reason) {
        List<String> warningsList = getUserWarnings(user);
        warningsList.add(reason);
        warnings.put(user.getId(), warningsList);
    }

    public Map<String, List<String>> getWarningsMap() {
        return warnings;
    }

    public Map<String, String> getTags() {
        if (tags == null) tags = new ConcurrentHashMap<>();
        return tags;
    }

//    public Moderation getModeration() {
//        if (this.moderation == null) this.moderation = new Moderation();
//        return this.moderation;
//    }
//
//    public Moderation getModConfig() {
//        return getModeration();
//    }

    public char getPrefix() {
        return prefix == Character.MIN_VALUE ? (prefix = '!') : prefix;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
        this.guildEntity.setPrefix(prefix);
    }

    public String getMusicAnnounceChannelId() {
        return musicAnnounceChannelId;
    }

    public void setMusicAnnounceChannelId(String musicAnnounceChannelId) {
        this.musicAnnounceChannelId = musicAnnounceChannelId;
    }

//    public NINO getNINO() {
//        if (nino == null)
//            nino = new NINO();
//        return nino;
//    }
//
//    public GuildSettings getSettings() {
//        if (settings == null)
//            settings = new GuildSettings();
//        return settings;
//    }

}
