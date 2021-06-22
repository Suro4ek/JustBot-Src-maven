package ru.rien.bot.commands.admin;

import net.dv8tion.jda.api.entities.Guild;
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

import java.util.List;

@Component
public class LeaveGuild implements Command {
    @Override
    public void execute(CommandEvent event) {
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        String guild_id = optionMappingList.get(0).getAsString();
        GuildWrapper guildWrapper = JustBotManager.instance().getGuildNoCache(guild_id);
        ReplyAction replyAction = event.getEvent().deferReply(true);
        if(guildWrapper != null){
            Guild guild = guildWrapper.getGuild();
            if(guild != null){
                guild.leave().queue();
                MessageUtils.sendInfoMessage("Бот успешно ливнул", replyAction);
            }else{
                MessageUtils.sendErrorMessage("Такой гильдии нет", replyAction);
            }
        }else {
            MessageUtils.sendErrorMessage("Такой гильдии нет1", replyAction);
        }

    }

    @Override
    public String getCommand() {
        return "leaveguild";
    }

    @Override
    public String getDescription(Language language) {
        return "leave guild";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{
                new OptionData(OptionType.STRING, "guild_id", "guild id", true)
        };
    }

    @Override
    public Permission getPermission() {
        return Permission.ALL_PERMISSIONS;
    }
}
