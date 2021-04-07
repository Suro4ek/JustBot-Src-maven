package ru.rien.bot.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.lang3.StringUtils;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.objects.Getters;
import ru.rien.bot.objects.GuildWrapper;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GuildUtils {

    private static final int LEVENSHTEIN_DISTANCE = 8;
    private static final Pattern userDiscrim = Pattern.compile(".+#[0-9]{4}");

    public static int getGuildUserCount(Guild guild) {
        int i = 0;
        for (Member member : guild.getMembers()){
            if(!member.getUser().isBot()){
                i++;
            }
        }
        return i;
    }

    /**
     * Gets a list of {@link Role} that match a string. Case doesn't matter.
     *
     * @param string The String to get a list of {@link Role} from.
     * @param guild  The {@link Guild} to get the roles from.
     * @return an empty if no role matches, otherwise a list of roles matching the string.
     */
    public static List<Role> getRole(String string, Guild guild) {
        return guild.getRolesByName(string, true);
    }

    /**
     * Gets a {@link Role} from a string. Case Doesn't matter.
     *
     * @param s       The String to get a role from
     * @param guildId The id of the {@link Guild} to get the role from
     * @return null if the role doesn't, otherwise a list of roles matching the string
     */
    public static Role getRole(String s, String guildId) {
        return getRole(s, guildId, null);
    }

    /**
     * Gets a {@link Role} that matches a string. Case doesn't matter.
     *
     * @param s       The String to get a role from
     * @param guildId The id of the {@link Guild} to get the role from
     * @param channel The channel to send an error message to if anything goes wrong.
     * @return null if the role doesn't, otherwise a list of roles matching the string
     */
    public static Role getRole(String s, String guildId, TextChannel channel) {
        Guild guild = Getters.getGuildById(guildId);
        Role role = guild.getRoles().stream()
                .filter(r -> r.getName().equalsIgnoreCase(s))
                .findFirst().orElse(null);
        if (role != null) return role;
        try {
            role = guild.getRoleById(Long.parseLong(s.replaceAll("[^0-9]", "")));
            if (role != null) return role;
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        if (channel != null) {
            if (guild.getRolesByName(s, true).isEmpty()) {
                String closest = null;
                int distance = LEVENSHTEIN_DISTANCE;
                for (Role role1 : guild.getRoles().stream().filter(role1 -> JustBotManager.instance().getGuild(guildId).getSelfAssignRoles()
                        .contains(role1.getId())).collect(Collectors.toList())) {
                    int currentDistance = StringUtils.getLevenshteinDistance(role1.getName(), s);
                    if (currentDistance < distance) {
                        distance = currentDistance;
                        closest = role1.getName();
                    }
                }
                MessageUtils.sendErrorMessage("That role does not exist! "
                        + (closest != null ? "Maybe you mean `" + closest + "`" : ""), channel);
                return null;
            } else {
                return guild.getRolesByName(s, true).get(0);
            }
        }
        return null;
    }

    public static char getPrefix(Guild guild) {
        return guild == null ? '!' : JustBotManager.instance().getGuild(guild.getId()).getPrefix();
    }

    public static Guild getGuildById(String id) {
        return ModuleDsBot.getInstance().getJda().getGuildById(id);
    }

    /**
     * Joins the bot to a {@link TextChannel}.
     *
     * @param channel The chanel to send an error message to in case this fails.
     * @param member  The member requesting the join. This is also how we determine what channel to join.
     */
    public static void joinChannel(TextChannel channel, Member member) {
        if (channel.getGuild().getSelfMember()
                .hasPermission(member.getVoiceState().getChannel(), Permission.VOICE_CONNECT) &&
                channel.getGuild().getSelfMember()
                        .hasPermission(member.getVoiceState().getChannel(), Permission.VOICE_SPEAK)) {
            if (member.getVoiceState().getChannel().getUserLimit() > 0 && member.getVoiceState().getChannel()
                    .getMembers().size()
                    >= member.getVoiceState().getChannel().getUserLimit() && !member.getGuild().getSelfMember()
                    .hasPermission(member.getVoiceState().getChannel(), Permission.MANAGE_CHANNEL)) {
                MessageUtils.sendErrorMessage("НЕ могу войти :(\n\nВ этом канале лимит по пользователям и у меня нет прав ", channel);
                return;
            }
            PlayerManager musicManager = ModuleDsBot.getInstance().getMusicManager();
            channel.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
            musicManager.getPlayer(channel.getGuild().getId()).play();

            if (musicManager.getPlayer(channel.getGuild().getId()).getPaused()) {
                MessageUtils.sendWarningMessage("В данный момент музыка на паузе `{%}resume`", channel);
            }
        } else {
            MessageUtils.sendErrorMessage("У меня нет прав " + (!channel.getGuild().getSelfMember()
                    .hasPermission(member.getVoiceState()
                            .getChannel(), Permission.VOICE_CONNECT) ?
                    "Подключаться" : "Говорить") + " в это канале!", channel);
        }
    }

    /**
     * Gets a {@link TextChannel} from a string. Not case sensitive.
     * The string can eater be the channel name, it's id, or it being mentioned.
     *
     * @param channelArg The string to get the channel from
     * @param wrapper    The Guild wrapper for the {@link Guild} that you want to get the channel from
     * @return null if the channel couldn't be found otherwise a {@link TextChannel}
     */
    public static TextChannel getChannel(String channelArg, GuildWrapper wrapper) {
        try {
            long channelId = Long.parseLong(channelArg.replaceAll("[^0-9]", ""));
            return wrapper != null ? wrapper.getGuild().getTextChannelById(channelId) : Getters.getChannelById(channelId);
        } catch (NumberFormatException e) {
            if (wrapper != null) {
                List<TextChannel> tcs = wrapper.getGuild().getTextChannelsByName(channelArg, true);
                if (!tcs.isEmpty()) {
                    return tcs.get(0);
                }
            }
            return null;
        }
    }

    /**
     * Gets a {@link User} from a string. Not case sensitive.
     * The string can eater be their name, their id, or them being mentioned.
     *
     * @param s       The string to get the user from.
     * @param guildId The string id of the {@link Guild} to get the user from.
     * @return null if the user wasn't found otherwise a {@link User}.
     */
    public static User getUser(String s, String guildId) {
        return getUser(s, guildId, false);
    }

    /**
     * Gets a {@link User} from a string. Not case sensitive.
     * The string can eater be their name, their id, or them being mentioned.
     *
     * @param s        The string to get the user from.
     * @param guildId  The id of the {@link Guild} to get the user from.
     * @param forceGet If you want to get the user from discord instead of from a guild.
     * @return null if the user wasn't found otherwise a {@link User}.
     */
    public static User getUser(String s, String guildId, boolean forceGet) {
        Guild guild = guildId == null || guildId.isEmpty() ? null : Getters.getGuildById(guildId);
        if (userDiscrim.matcher(s).find()) {
            if (guild == null) {
                return Getters.getUserCache().stream()
                        .filter(user -> (user.getName() + "#" + user.getDiscriminator()).equalsIgnoreCase(s))
                        .findFirst().orElse(null);
            } else {
                try {
                    return guild.getMembers().stream()
                            .map(Member::getUser)
                            .filter(user -> (user.getName() + "#" + user.getDiscriminator()).equalsIgnoreCase(s))
                            .findFirst().orElse(null);
                } catch (NullPointerException ignored) {
                }
            }
        } else {
            User tmp;
            if (guild == null) {
                tmp = Getters.getUserCache().stream().filter(user -> user.getName().equalsIgnoreCase(s))
                        .findFirst().orElse(null);
            } else {
                tmp = guild.getMembers().stream()
                        .map(Member::getUser)
                        .filter(user -> user.getName().equalsIgnoreCase(s))
                        .findFirst().orElse(null);
            }
            if (tmp != null) return tmp;
            try {
                long l = Long.parseLong(s.replaceAll("[^0-9]", ""));
                if (guild == null) {
                    tmp = Getters.getUserById(l);
                } else {
                    Member temMember = guild.getMemberById(l);
                    if (temMember != null) {
                        tmp = temMember.getUser();
                    }
                }
                if (tmp != null) {
                    return tmp;
                } else if (forceGet) {
                    return Getters.retrieveUserById(l);
                }
            } catch (NumberFormatException | NullPointerException ignored) {
            }
        }
        return null;
    }
}
