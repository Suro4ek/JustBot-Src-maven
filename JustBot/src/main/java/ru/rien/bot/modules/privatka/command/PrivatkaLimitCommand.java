package ru.rien.bot.modules.privatka.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.entity.PrivatkaEntity;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.PrivatkaService;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

public class PrivatkaLimitCommand implements SubCommand {

    private PrivatkaService privatkaService;

    public PrivatkaLimitCommand(PrivatkaService privatkaService){
        this.privatkaService = privatkaService;
    }

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("limit", "limit users for your room");
    }

    @Override
    public void execute(CommandEvent event) {
        User sender = event.getSender();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        GuildWrapper guild = event.getGuild();
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        int limit = (int)optionMappingList.get(0).getAsLong();
        PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
        if (privatkaEntity != null) {
            VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
            if (voiceChannel != null) {
                voiceChannel.getManager().setUserLimit(limit).queue();
                MessageUtils.sendInfoMessage(MessageUtils.getEmbed(sender).setDescription("Новый лимит " + limit), replyAction);
            }
        } else {
            MessageUtils.sendErrorMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(),  replyAction);
        }
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{
                new OptionData(OptionType.INTEGER, "count", "limit user", true)
        };
    }
}