package ru.rien.bot.commands.admin.settings.blacklist;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.command.ModuleCommand;
import ru.rien.bot.modules.command.SubCommand;
import ru.rien.bot.modules.dsBot.JustBotManager;
import ru.rien.bot.objects.GuildWrapper;
import ru.rien.bot.utils.MessageUtils;

import java.util.List;
import java.util.Locale;

public class BlackListAddCommand implements SubCommand {
    @Override
    public SubcommandData getSubCommands() {
        return new SubcommandData("add", "добавить в списка");
    }

    @Override
    public void execute(CommandEvent event) {
        List<OptionMapping> optionMappingList = event.getOptionMappings();
        GuildWrapper guildWrapper = JustBotManager.instance().getGuildNoCache(optionMappingList.get(0).getAsString());
        ReplyAction replyAction = event.getEvent().deferReply(true);
        if(guildWrapper.getGuild() != null){
            Guild guild = guildWrapper.getGuild();
            String command = optionMappingList.get(1).getAsString();
            if(ModuleCommand.getCommand(command.toLowerCase(Locale.ROOT)) != null){
                guildWrapper.getSettings().addBlackListCommands(command.toLowerCase(Locale.ROOT));
                MessageUtils.sendSuccessMessage("Команда добавлена", replyAction, event.getSender());
                return;
            }
            MessageUtils.sendWarningMessage("Недопустимый канал или пользователь! Попробуйте id, если вы уверены, что такое вообще существует", replyAction);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMIN;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[]{
                new OptionData(OptionType.STRING, "guild_id", "сервер id", true),
                new OptionData(OptionType.STRING, "command_name","название команды", true)
        };
    }
}
