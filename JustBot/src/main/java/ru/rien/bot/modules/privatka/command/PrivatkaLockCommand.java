package ru.rien.bot.modules.privatka.command;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.entity.GuildEntity;
import ru.rien.bot.entity.PrivatkaEntity;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.PrivatkaService;
import ru.rien.bot.utils.MessageUtils;

import java.util.EnumSet;


public class PrivatkaLockCommand implements SubCommand {

    private PrivatkaService privatkaService;

    public PrivatkaLockCommand(PrivatkaService privatkaService){
        this.privatkaService = privatkaService;
    }

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("lock", "you can lock your room");
    }

    @Override
    public void execute(CommandEvent event) {
        TextChannel channel = event.getChannel();
        User sender = event.getSender();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        GuildWrapper guild = event.getGuild();
        GuildEntity guildEntity = guild.getGuildEntity();
        PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
        if(privatkaEntity != null){
            VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
            if(voiceChannel != null){
                voiceChannel.getManager().putPermissionOverride(guild.getGuild()
                                .getPublicRole(), null,
                        EnumSet.of(net.dv8tion.jda.api.Permission.VOICE_CONNECT)).queue();
                TextChannel textChannel = guild.getGuild().getTextChannelById(privatkaEntity.getTextid());
                if(textChannel != null){
                    textChannel.getManager().putPermissionOverride(guild.getGuild()
                                    .getPublicRole(), null,
                            EnumSet.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL)).queue();
                }
                MessageUtils.sendInfoMessage(MessageUtils.getEmbed(sender).setDescription("\uD83D\uDD12 Приватка заблокирована"), replyAction);
            }
        }else{
            MessageUtils.sendErrorMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(),  replyAction);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }


    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }
}
