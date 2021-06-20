package ru.rien.bot.modules.privatka.command;

import net.dv8tion.jda.api.entities.TextChannel;
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

import java.util.EnumSet;
import java.util.List;

public class PrivatkaPermitCommand implements SubCommand {

    private PrivatkaService privatkaService;

    public PrivatkaPermitCommand(PrivatkaService privatkaService){
        this.privatkaService = privatkaService;
    }

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("permit", "allow user join your room");
    }

    @Override
    public void execute(CommandEvent event) {
        User sender = event.getSender();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        GuildWrapper guild = event.getGuild();
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        User user = optionMappingList.get(0).getAsUser();

        PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
        if (privatkaEntity != null) {
            VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
            if (voiceChannel != null) {
                voiceChannel.getManager().putPermissionOverride(guild.getGuild().retrieveMember(user).complete(), EnumSet.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL,net.dv8tion.jda.api.Permission.VOICE_CONNECT),null)
                        .queue();
                TextChannel textChannel = guild.getGuild().getTextChannelById(privatkaEntity.getTextid());
                if(textChannel != null){
                    textChannel.getManager().putPermissionOverride(guild.getGuild().retrieveMember(user).complete(), EnumSet.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL),null).queue();
                }
                MessageUtils.sendInfoMessage(MessageUtils.getEmbed(sender).setDescription("Пользователию " + user.getName()+ " разрешено заходить в приватку"),  replyAction);
            }
        } else {
            MessageUtils.sendErrorMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(), replyAction);
        }
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{
                new OptionData(OptionType.USER, "user", "this is just user", true)
        };
    }
}