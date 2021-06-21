package ru.rien.bot.commands.music.skip;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.api.music.PlayerManager;
import ru.rien.bot.api.music.player.Track;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.dsBot.ModuleDsBot;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;
import ru.rien.bot.utils.buttons.ButtonGroupConstants;
import ru.rien.bot.utils.objects.MyButton;
import ru.rien.bot.utils.votes.VoteGroup;
import ru.rien.bot.utils.votes.VoteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SkipStartCommand implements SubCommand {

    private final ModuleDsBot moduleDsBot;

    public SkipStartCommand(ModuleDsBot moduleDsBot) {
        this.moduleDsBot = moduleDsBot;
    }

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("start", "начать голосование за пропуск");
    }

    @Override
    public void execute(CommandEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        GuildWrapper guild = event.getGuild();
        User sender = event.getSender();
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        PlayerManager musicManager = moduleDsBot.getMusicManager();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        if(SkipCommand.checkSkip(channel,musicManager,guild,member,replyAction)){
            return;
        }
        Track currentTrack = musicManager.getPlayer(guild.getGuildId()).getPlayingTrack();
        if (optionMappingList.size() == 0 && currentTrack.getMeta().get("requester").equals(sender.getId())) {
            MessageUtils.sendInfoMessage(MessageUtils.getEmbed(sender).setDescription(guild.getMessage("SKIP_REQUESTER")),  replyAction.setEphemeral(false));
            musicManager.getPlayer(guild.getGuildId()).skip();
            return;
        }
        if (VoteUtil.contains(SkipCommand.getSkipUUID(), guild.getGuild()))
            MessageUtils.sendWarningMessage(guild.getMessage("SKIP_START_VOTE"), channel, sender);
        else {
            VoteGroup group = new VoteGroup(guild.getMessage("SKIP_VOTE_TITLE"), SkipCommand.getSkipUUID());
            List<User> users = new ArrayList<>();
            for (Member inChannelMember : channel.getGuild().getSelfMember().getVoiceState().getChannel().getMembers()) {
                if (channel.getGuild().getSelfMember().getUser().getIdLong() != inChannelMember.getUser().getIdLong()) {
                    users.add(inChannelMember.getUser());
                }
            }
            group.limitUsers(users);
            VoteUtil.sendVoteMessage(SkipCommand.getSkipUUID(), (vote) -> {
                        if (vote.equals(VoteGroup.Vote.NONE) || vote.equals(VoteGroup.Vote.NO)) {
                            MessageUtils.sendAutoDeletedMessage(new MessageBuilder().append(guild.getMessage("SKIP_VOTE_NOT")).build(), TimeUnit.SECONDS.toMillis(5),  channel);
                        } else {
                            MessageUtils.sendAutoDeletedMessage(new MessageBuilder().append(guild.getMessage("SKIP_SKIPPING")).build(), TimeUnit.SECONDS.toMillis(5),  channel);
/*                            if (songMessage)
                                SongCommand.updateSongMessage(sender,guild, message, channel);*/
                            musicManager.getPlayer(guild.getGuildId()).skip();
                        }
                    }, group, TimeUnit.MINUTES.toMillis(1), channel, sender, ButtonGroupConstants.VOTE_SKIP,
                    Lists.newArrayList(
                            new MyButton(Button.secondary(UUID.randomUUID()+":skipforce","Пропустить"), (event1) -> {
                                if (getPermissions(channel).hasPermission(channel.getGuild().retrieveMember(event1.getUser()).complete(), Permission.SKIP_FORCE)) {
                                    musicManager.getPlayer(channel.getGuild().getId()).skip();
//                            if (songMessage) {
//                                SongCommand.updateSongMessage(user,guild, message1, channel);
//                            }
                                    musicManager.getPlayer(guild.getGuildId()).skip();
                                    VoteUtil.remove(SkipCommand.getSkipUUID(), guild.getGuild());
                                } else {
                                    channel.sendMessage(guild.getMessage("NEED_PERMISSION",Permission.SKIP_FORCE))
                                            .queue();
                                }
                            }
                    )));
        }

    }

    @Override
    public CommandType getType() {
        return null;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }
}
