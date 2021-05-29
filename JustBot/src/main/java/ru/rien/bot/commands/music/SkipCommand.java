package ru.rien.bot.commands.music;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.objects.Getters;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.objects.ButtonGroup;
import ru.rien.bot.utils.votes.VoteGroup;
import ru.rien.bot.utils.votes.VoteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SkipCommand implements Command {
    private static final UUID skipUUID = UUID.randomUUID();

    private final ModuleDsBot moduleDsBot;

    public SkipCommand(ModuleDsBot moduleDsBot) {
        this.moduleDsBot = moduleDsBot;
    }

    @Override
    public void execute(CommandEvent event) {
        Message message = event.getMessage();
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        String[] args = event.getArgs();
        GuildWrapper guild = event.getGuild();
        User sender = event.getSender();
        boolean songMessage = message.getAuthor().getIdLong() == Getters.getSelfUser().getIdLong();
        PlayerManager musicManager = moduleDsBot.getMusicManager();
        if (!channel.getGuild().getAudioManager().isConnected() ||
                musicManager.getPlayer(channel.getGuild().getId()).getPlayingTrack() == null) {
            MessageUtils.sendAutoDeletedMessage(new MessageBuilder().append(guild.getMessage("SKIP_NOT_PLAY")).build(), TimeUnit.SECONDS.toMillis(5), channel);
            return;
        }
        if (member.getVoiceState().inVoiceChannel() && !channel.getGuild().getSelfMember().getVoiceState().getChannel()
                .getId()
                .equals(member.getVoiceState().getChannel().getId())
                && !getPermissions(channel).hasPermission(member, Permission.SKIP_FORCE)) {
            MessageUtils.sendAutoDeletedMessage(new MessageBuilder().append(guild.getMessage("SKIP_YOU_NOT_CHANNEL")).build(), TimeUnit.SECONDS.toMillis(5), channel);
            return;
        }
        Track currentTrack = musicManager.getPlayer(guild.getGuildId()).getPlayingTrack();
        if (args.length == 0 && currentTrack.getMeta().get("requester").equals(sender.getId())) {
            MessageUtils.sendAutoDeletedMessage(new MessageBuilder().append(guild.getMessage("SKIP_REQUESTER")).build(), TimeUnit.SECONDS.toMillis(5), channel);
            musicManager.getPlayer(guild.getGuildId()).skip();
            if (songMessage)
                SongCommand.updateSongMessage(sender,guild, message, channel);
            return;
        }

        if (args.length != 1) {
            if (!member.getVoiceState().inVoiceChannel() ||
                    member.getVoiceState().getChannel().getIdLong() != channel.getGuild().getSelfMember().getVoiceState().getChannel().getIdLong()) {
                MessageUtils.sendWarningMessage(guild.getMessage("SKIP_YOU_NOT_CHANNEL"), channel);
                return;
            }
            if (VoteUtil.contains(skipUUID, guild.getGuild()))
                MessageUtils.sendWarningMessage(guild.getMessage("SKIP_START_VOTE"), channel, sender);
            else {
                VoteGroup group = new VoteGroup(guild.getMessage("SKIP_VOTE_TITLE"), skipUUID);
                List<User> users = new ArrayList<>();
                for (Member inChannelMember : channel.getGuild().getSelfMember().getVoiceState().getChannel().getMembers()) {
                    if (channel.getGuild().getSelfMember().getUser().getIdLong() != inChannelMember.getUser().getIdLong()) {
                        users.add(inChannelMember.getUser());
                    }
                }
                group.limitUsers(users);
                VoteUtil.sendVoteMessage(skipUUID, (vote) -> {
                            if (vote.equals(VoteGroup.Vote.NONE) || vote.equals(VoteGroup.Vote.NO)) {
                                MessageUtils.sendAutoDeletedMessage(new MessageBuilder().append(guild.getMessage("SKIP_VOTE_NOT")).build(), TimeUnit.SECONDS.toMillis(5),  channel);
                            } else {
                                MessageUtils.sendAutoDeletedMessage(new MessageBuilder().append(guild.getMessage("SKIP_SKIPPING")).build(), TimeUnit.SECONDS.toMillis(5),  channel);
                                if (songMessage)
                                    SongCommand.updateSongMessage(sender,guild, message, channel);
                                musicManager.getPlayer(guild.getGuildId()).skip();
                            }
                        }, group, TimeUnit.MINUTES.toMillis(1), channel, sender, ButtonGroupConstants.VOTE_SKIP,
                        new ButtonGroup.Button("\u23ED", (owner, user, message1) -> {
                            if (getPermissions(channel).hasPermission(channel.getGuild().getMember(user), Permission.SKIP_FORCE)) {
                                musicManager.getPlayer(channel.getGuild().getId()).skip();
                                if (songMessage) {
                                    SongCommand.updateSongMessage(user,guild, message1, channel);
                                }
                                musicManager.getPlayer(guild.getGuildId()).skip();
                                VoteUtil.remove(skipUUID, guild.getGuild());
                            } else {
                                channel.sendMessage(guild.getMessage("NEED_PERMISSION",Permission.SKIP_FORCE))
                                        .queue();
                            }
                        }));
            }
        } else {
            if (args[0].equalsIgnoreCase("force")) {
                if (getPermissions(channel).hasPermission(member, Permission.SKIP_FORCE)) {
                    if (songMessage)
                        SongCommand.updateSongMessage(sender,guild, message, channel);
                    VoteUtil.remove(skipUUID, guild.getGuild());
                    musicManager.getPlayer(guild.getGuildId()).skip();
                } else {
                    channel.sendMessage(guild.getMessage("NEED_PERMISSION",Permission.SKIP_FORCE))
                            .queue();
                }
                return;
            } else if (args[0].equalsIgnoreCase("cancel")) {

                if (getPermissions(channel).hasPermission(member, Permission.SKIP_CANCEL)) {
                    VoteUtil.remove(skipUUID, channel.getGuild());
                } else
                    channel.sendMessage(guild.getMessage("NEED_PERMISSION",Permission.SKIP_CANCEL))
                            .queue();
                return;
            }
            if (!channel.getGuild().getMember(sender).getVoiceState().inVoiceChannel() ||
                    channel.getGuild().getMember(sender).getVoiceState().getChannel().getIdLong() != channel.getGuild().getSelfMember().getVoiceState().getChannel().getIdLong()) {
                MessageUtils.sendWarningMessage(guild.getMessage("SKIP_YOU_NOT_CHANNEL"), channel);
                return;
            }
            VoteGroup.Vote vote = VoteGroup.Vote.parseVote(args[0]);
            if (vote != null) {
                if (!VoteUtil.contains(skipUUID, guild.getGuild()))
                    MessageUtils.sendWarningMessage(guild.getMessage("SKIP_NOT_START"), channel, sender);
                else
                    VoteUtil.getVoteGroup(skipUUID, guild.getGuild()).addVote(vote, sender);
            } else
                MessageUtils.sendUsage(this,event.getGuild(), channel, sender, args);
        }
    }

    public static UUID getSkipUUID() {
        return skipUUID;
    }

    @Override
    public String getCommand() {
        return "skip";
    }

    @Override
    public String getDescription(GuildWrapper guild) {
        return guild.getMessage("SKIP_DESCRIPTION");
    }

    @Override
    public String getUsage(GuildWrapper guild) {
        return guild.getMessage("SKIP_USAGE");
    }

    @Override
    public Permission getPermission() {
        return Permission.SKIP_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}