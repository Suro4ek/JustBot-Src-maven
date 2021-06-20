package ru.rien.bot.commands.admin;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.stereotype.Component;
import ru.rien.bot.modules.command.Command;
import ru.rien.bot.modules.command.CommandEvent;
import ru.rien.bot.modules.command.CommandType;
import ru.rien.bot.modules.messsage.Language;
import ru.rien.bot.permission.Permission;

@Component
public class LangCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
            event.getEvent().reply("Помоги переводом").
                    setEphemeral(true).
                    addActionRow(Button.link("https://github.com/1Suro1/JustBot/tree/main/lang",
                    "пофиксить перевод")).queue();
    }

    @Override
    public String getCommand() {
        return "lang";
    }

    @Override
    public String getDescription(Language guildWrapper) {
        return guildWrapper.getMessage("LANG_DESCRIPTION");
    }

//    @Override
//    public String getUsage(GuildWrapper guildWrapper) {
//        return guildWrapper.getMessage("LANG_USAGE");
//    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public OptionData[] parameters() {
        return new OptionData[0];
    }

    @Override
    public Permission getPermission() {
        return Permission.LANG_COMMAND;
    }
}
