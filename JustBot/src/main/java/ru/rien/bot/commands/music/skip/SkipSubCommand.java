package ru.rien.bot.commands.music.skip;


import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
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
import ru.rien.bot.utils.votes.VoteGroup;
import ru.rien.bot.utils.votes.VoteUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SkipSubCommand implements SubCommand {

    private final ModuleDsBot moduleDsBot;

    public SkipSubCommand(ModuleDsBot moduleDsBot) {
        this.moduleDsBot = moduleDsBot;
    }

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("skip", "пропустить трек");
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
        if(optionMappingList.size() == 1){
            boolean skip = optionMappingList.get(0).getAsBoolean();
            VoteGroup.Vote vote = VoteGroup.Vote.parseVote(skip);
            if (vote != null) {
                if (!VoteUtil.contains(SkipCommand.getSkipUUID(), guild.getGuild()))
                    MessageUtils.sendWarningMessage(guild.getMessage("SKIP_NOT_START"), replyAction);
                else
                    VoteUtil.getVoteGroup(SkipCommand.getSkipUUID(), guild.getGuild()).addVote(vote, sender);
            }
        }
    }


    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.BOOLEAN, "голосовать", "эта команда дает тебе голосовать за пропуск" +
                " трека")};
    }
}
