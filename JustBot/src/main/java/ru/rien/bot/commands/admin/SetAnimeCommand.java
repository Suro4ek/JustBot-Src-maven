package ru.rien.bot.commands.admin;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;

@Component
public class SetAnimeCommand implements Command {

    @Override
    public void execute(CommandEvent event) {
        User user = event.getSender();
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String guildId = optionMappingList.get(0).getAsString();
        GuildWrapper guildWrapper = JustBotManager.instance().getGuildNoCache(guildId);
        if(guildWrapper != null){
            Guild guild = guildWrapper.getGuild();
            if(guild != null){
                Member member = guild.retrieveMember(user).complete();
                if(member != null) {
                    if (member.getPermissions().contains(net.dv8tion.jda.api.Permission.ADMINISTRATOR)) {
                        TextChannel textChannel = guild.getTextChannelById(optionMappingList.get(0).getAsString());
                        if(textChannel != null) {
                            guildWrapper.setAnime(textChannel.getIdLong());
                            event.getEvent().replyEmbeds(MessageUtils.getEmbed(user)
                                    .setDescription("Аниме канал установлен").build())
                                    .setEphemeral(true).queue();
                        }else{
                            MessageUtils.sendErrorMessage("Такого канала нет", event.getEvent().deferReply(true));
                        }
                    } else {
                        MessageUtils.sendErrorMessage("У вас не хватает прав", event.getEvent().deferReply(true));
                    }
                }else{
                    MessageUtils.sendErrorMessage("У вас не хватает прав", event.getEvent().deferReply(true));
                }
            }else{
                MessageUtils.sendErrorMessage("Такого сервера нет", event.getEvent().deferReply(true));
            }
        }else {
            MessageUtils.sendErrorMessage("Такого сервера нет", event.getEvent().deferReply(true));
        }
    }

    @Override
    public String getCommand() {
        return "anime";
    }

    @Override
    public String getDescription(Language guild) {
        return guild.getMessage("NSFW_DESCRIPTION");
    }

//    @Override
//    public String getUsage(GuildWrapper guild) {
//        return Language.getLanguage(guild.getLang()).getMessage("NSFW_USAGE");
//    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "guild_id", "ваш id сервера", true),
        new OptionData(OptionType.STRING, "channel_id", "id канала текстового", true)};
    }

    @Override
    public String[] getAliases() {
        return new String[]{"setanime"};
    }
}
