package ru.rien.bot.modules.stats;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.permission.Permission;
import ru.rien.bot.utils.MessageUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;

@Component
public class AStatsCommand implements Command {

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
                        ReplyAction replyAction = event.getEvent().deferReply(true);
                        if (guildWrapper.getGuildEntity().isStats()) {
                            Category category = guild.getCategoryById(guildWrapper.getGuildEntity().getStatsid());
                            if (category != null) {
                                category.delete().queue();
                            }
                            guildWrapper.removestats(guildWrapper.getGuildEntity().getStatsid());
                            MessageUtils.sendInfoMessage(MessageUtils.getEmbed(event.getSender())
                                    .setDescription("Статистика выключена"), replyAction);
                        } else {
                            LocalDate date = LocalDate.now();
                            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                            Category category = guild.createCategory("Stats").complete();
                            guildWrapper.initstats(category.getIdLong());
                            category.createVoiceChannel(date.format(outputFormat)).addPermissionOverride(guild
                                            .getPublicRole(), null,
                                    EnumSet.of(net.dv8tion.jda.api.Permission.VOICE_CONNECT))
                                    .queue(voiceChannel -> {
                                                voiceChannel.createCopy().setName("Участников: " + guild.getMemberCount()).queue();
                                            }
                                    );
                            MessageUtils.sendInfoMessage(MessageUtils.getEmbed(event.getSender())
                                    .setDescription("Статистика включена"), replyAction);
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
        return "astats";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return "статистика в категориях";
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return "{%}astats - включить/выключить статистику";
//    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{new OptionData(OptionType.STRING, "guild_id", "ваш id сервера", true)};
    }

    @Override
    public Permission getPermission() {
        return Permission.ASTATS_COMMAND;
    }
}
