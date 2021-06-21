package ru.rien.bot.modules.privatka.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.entity.PrivatkaEntity;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.services.PrivatkaService;
import ru.rien.bot.utils.MessageUtils;

public class PrivatkaNameCommand implements SubCommand {

    private PrivatkaService privatkaService;

    public PrivatkaNameCommand(PrivatkaService privatkaService){
        this.privatkaService = privatkaService;
    }

    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("name", "change name is your room");
    }

    @Override
    public void execute(CommandEvent event) {
        User sender = event.getSender();
        ReplyAction replyAction = event.getEvent().deferReply(true);
        GuildWrapper guild = event.getGuild();
        String name = event.getOptionMappings().get(0).getAsString();
        PrivatkaEntity privatkaEntity = privatkaService.findbyOwnerId(sender.getIdLong(), guild);
        if(privatkaEntity != null){
            change_name(privatkaEntity, name, replyAction, sender, guild);
        }else{
            MessageUtils.sendErrorMessage(MessageUtils.getEmbed(sender).setDescription("У вас нет приватки").build(),  replyAction);
        }
    }

    private void change_name(PrivatkaEntity privatkaEntity, String name, ReplyAction channel, User sender, GuildWrapper guild) {
             VoiceChannel voiceChannel = guild.getGuild().getVoiceChannelById(privatkaEntity.getVchannelid());
             if (voiceChannel != null) {
                 voiceChannel.getManager().setName(name).queue();
                 MessageUtils.sendInfoMessage(MessageUtils.getEmbed(sender).setDescription("Теперь приватка называется: " + name), channel);
             }
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }


    @Override
    public OptionData[] parameters() {
        return new OptionData[]{
                new OptionData(OptionType.STRING, "name", "name your room", true)
        };
    }
}
